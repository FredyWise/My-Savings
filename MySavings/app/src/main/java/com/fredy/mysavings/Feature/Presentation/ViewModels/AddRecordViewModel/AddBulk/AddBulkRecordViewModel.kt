package com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddBulk

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Feature.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.RecordType.*
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.TabScannerUseCase.TabScannerUseCases
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddRecordEvent
import com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel.AddRecordState
import com.fredy.mysavings.Util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddBulkRecordViewModel @Inject constructor(
//    private val recordUseCases: RecordUseCases,
//    private val currencyUseCase: CurrencyUseCases,
    private val tabScannerUseCases: TabScannerUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(AddRecordState())

    val resource = MutableStateFlow<Resource<String>>(
        Resource.Loading()
    )

    init {
        viewModelScope.launch {
            savedStateHandle.get<String>("bookId")?.let { bookId ->
//                savedStateHandle.get<String>("recordId")?.let { recordId ->
//                    if (recordId != "-1") {
//                        recordUseCases.getRecordById(
//                            recordId
//                        ).collectLatest {
//                            state = state.copy(
//                                fromWallet = it.fromWallet,
//                                toWallet = it.toWallet,
//                                toCategory = it.toCategory,
//                                recordId = it.record.recordId,
//                                walletIdFromFk = it.record.walletIdFromFk,
//                                walletIdToFk = it.record.walletIdToFk,
//                                categoryIdFk = it.record.categoryIdFk,
//                                recordDate = it.record.recordDateTime.toLocalDate(),
//                                recordTime = it.record.recordDateTime.toLocalTime(),
//                                recordAmount = it.record.recordAmount,
//                                recordCurrency = it.record.recordCurrency,
//                                recordType = it.record.recordType,
//                                recordNotes = it.record.recordNotes,
//                            )
//                            calcState = calcState.copy(
//                                number1 = it.record.recordAmount.absoluteValue.toString()
//                            )
//                        }
//                    }
//                }
                state = state.copy(bookIdFk = bookId)
            }
        }
    }

    fun onEvent(event: AddRecordEvent) {
        viewModelScope.launch {
            when (event) {
                is AddRecordEvent.SaveRecord -> {
                    Log.e("babi: start")
                    resource.update {
                        Resource.Loading()
                    }
                    val records = state.records?.map {
                        Log.e("babi: $it")
                        it.fillRequiredInformation() ?: return@launch
                    }
                    Log.e("babi: $records")

                    records?.let {
                        tabScannerUseCases.upsertRecords(records)

                        resource.update {
                            Resource.Success("Record Data Successfully Added")
                        }

                        state = AddRecordState()
                        event.sideEffect()
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
                    state = state.copy(
                        categoryIdFk = event.toCategory.categoryId,
                        toCategory = event.toCategory
                    )
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

                is AddRecordEvent.ImageToRecords -> {
                    val records = tabScannerUseCases.processImage(event.imageUri)
                    state = state.copy(records = records)
                    val firstRecordDateTime = records?.firstOrNull()?.recordDateTime
                    firstRecordDateTime?.let {
                        state = state.copy(recordDate = it.toLocalDate(), recordTime = it.toLocalTime())
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

                is AddRecordEvent.UpdateRecord -> {
                    state = state.copy(
                        record = event.record
                    )
                }

                else -> {}
            }
        }
    }

    private fun Record.fillRequiredInformation(): Record? {
        val recordId= state.recordId
        val walletIdFromFk = state.walletIdFromFk
        val walletIdToFk = walletIdFromFk
        val categoryIdToFk = state.categoryIdFk
        val bookIdFk = state.bookIdFk
        val recordDateTime = state.recordDate.atTime(
            state.recordTime.withNano(
                (state.recordTime.nano.div(1000000)).times(1000000)
            )
        )
        var calculationResult = recordAmount
        val recordCurrency = state.recordCurrency
        val recordType = recordType

        if (recordDateTime == null || calculationResult == 0.0 || recordCurrency.isBlank() || walletIdFromFk == null || walletIdToFk == null || categoryIdToFk == null) {
            resource.update {
                Resource.Error(
                    "Please fill all required information"
                )
            }
            return null
        }

        when (recordType) {
            Income -> {
                state.fromWallet.walletAmount += calculationResult
            }

            Expense -> {
                if (state.fromWallet.walletAmount < calculationResult) {
                    resource.update {
                        Resource.Error(
                            "Account balance is not enough"
                        )
                    }
                    return null
                }
                state.fromWallet.walletAmount -= calculationResult
                calculationResult = -calculationResult
            }

            Transfer -> {}
        }



        return this.copy(
            recordId = recordId,
            walletIdFromFk = walletIdFromFk,
            walletIdToFk = walletIdToFk,
            categoryIdFk = categoryIdToFk,
            bookIdFk = bookIdFk,
            recordTimestamp = TimestampConverter.fromDateTime(recordDateTime),
            recordAmount = calculationResult,
            recordCurrency = recordCurrency,
            recordType = recordType,
            recordNotes = recordNotes,
        )
    }

}


