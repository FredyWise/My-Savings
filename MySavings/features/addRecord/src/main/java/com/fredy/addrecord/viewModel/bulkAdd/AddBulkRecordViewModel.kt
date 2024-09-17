package com.fredy.addrecord.viewModel.bulkAdd

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.addrecord.domain.usecases.bulkAdd.TabScannerUseCases
import com.fredy.addrecord.viewModel.AddRecordEvent
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.model.Record
import com.fredy.addrecord.viewModel.AddRecordState
import com.fredy.addrecord.viewModel.BulkAddRecordEvent
import com.fredy.domain.enumsChecker.isExpense


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddBulkRecordViewModel @Inject constructor(
    private val tabScannerUseCases: TabScannerUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(AddRecordState())

    val resource = MutableStateFlow<Resource<AddRecordState, DataError.Local>>(
        Resource.Loading()
    )

    init {
        viewModelScope.launch {
            savedStateHandle.get<String>("bookId")?.let { bookId ->
                state = state.copy(bookIdFk = bookId)
            }
        }
    }

    fun onEvent(event: AddRecordEvent) {
        viewModelScope.launch {
            when (event) {
                is AddRecordEvent.SaveRecord -> {
                    viewModelScope.launch {
                        tabScannerUseCases.upsertRecords(state).collect { result ->

                            resource.update { result }
                            if (result is Resource.Success) {
                                state = result.data!!
                                event.sideEffect()
                                state = AddRecordState()
                            }
                        }
                    }
                }

                is AddRecordEvent.AccountIdFromFk -> {
                    state = state.copy(
                        recordCurrency = event.fromWallet.walletCurrency,
                        walletIdFromFk = event.fromWallet.walletId,
                        fromWallet = event.fromWallet
                    )
                }

                is AddRecordEvent.AccountIdToFk -> {
                    state = state.copy(
                        walletIdToFk = event.toWallet.walletId,
                        toWallet = event.toWallet
                    )
                }

                is AddRecordEvent.CategoryIdFk -> {
                    state = if (event.toCategory.categoryType.isExpense()) {
                        state.copy(
                            categoryIdFk = event.toCategory.categoryId,
                            toCategory = event.toCategory
                        )
                    } else {
                        state.copy(
                            categoryIncomeIdFk = event.toCategory.categoryId,
                            toIncomeCategory = event.toCategory
                        )
                    }
                }

                is AddRecordEvent.RecordDate -> {
                    state = state.copy(
                        recordDate = event.date
                    )
                }

                is AddRecordEvent.RecordTime -> {
                    state = state.copy(
                        recordTime = event.time
                    )
                }

                is AddRecordEvent.RecordCurrency -> {//useless
                    state = state.copy(
                        recordCurrency = event.currency
                    )
                }

                is BulkAddRecordEvent.ImageToRecords -> {
                    val records = tabScannerUseCases.processImage(event.imageUri)
                    state = state.copy(records = records)
                    val firstRecordDateTime = records?.firstOrNull()?.recordDateTime
                    firstRecordDateTime?.let {
                        state =
                            state.copy(recordDate = it.toLocalDate(), recordTime = it.toLocalTime())
                    }
                }

                is AddRecordEvent.RecordNotes -> {
                    state = state.copy(
                        recordNotes = event.notes
                    )
                }

                is AddRecordEvent.DismissWarning -> {
                    state = state.copy(
                        isShowWarning = false
                    )
                }

                is BulkAddRecordEvent.UpdateRecord -> {
                    val records = state.records
                    records?.let {
                        state = if (state.isAdding) {
                            state.copy(
                                records = records.toMutableList().apply {
                                    add(event.record)
                                }.sortedBy { it.recordId }
                            )
                        } else {
                            state.copy(
                                records = records.toMutableList().apply {
                                    removeIf { it.recordId == event.record.recordId }
                                    add(event.record)
                                }.sortedBy { it.recordId }
                            )
                        }
                    }
                }

                is BulkAddRecordEvent.DeleteRecord -> {
                    val records = state.records
                    records?.let {
                        state = state.copy(
                            records = records.toMutableList().apply {
                                removeIf { it.recordId == event.record.recordId }
                            }.sortedBy { it.recordId },
                            record = Record(),
                        )
                    }
                }

                BulkAddRecordEvent.CloseAddRecordItemDialog -> {
                    state = state.copy(isShowWarning = false)
                }

                is BulkAddRecordEvent.ShowAddRecordItemDialog -> {
                    state = state.copy(
                        record = event.record,
                        isAdding = event.record.recordId.isEmpty(),
                        isShowWarning = true
                    )
                }

            }
        }
    }
}


