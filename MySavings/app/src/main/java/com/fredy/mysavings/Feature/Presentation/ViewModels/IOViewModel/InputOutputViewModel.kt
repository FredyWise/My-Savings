package com.fredy.mysavings.Feature.Presentation.ViewModels.IOViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.BookUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases.IOUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.Util.formatDateYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InputOutputViewModel @Inject constructor(
    private val recordUseCases: RecordUseCases,
    private val bookUseCases: BookUseCases,
    private val IOUseCases: IOUseCases,
) : ViewModel() {

    init {
        viewModelScope.launch {
            IOUseCases.getDBInfo()
                .collectLatest { dbInfo ->
                    _state.update {
                        it.copy(
                            dbInfo = dbInfo
                        )
                    }

                    bookUseCases.getUserBooks().collectLatest { bookResource ->
                        when (bookResource) {
                            is Resource.Success -> {
                                _state.update {
                                    it.copy(
                                        currentBook = bookResource.data!!.first(),
                                        books = bookResource.data
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                }
        }
    }

    private val _state = MutableStateFlow(
        IOState()
    )

    private val _trueRecordsWithinSpecificTime = _state.flatMapLatest { state ->
        recordUseCases.getAllTrueRecordsWithinSpecificTime(
            state.startDate,
            state.endDate,
            state.currentBook
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    val state = combine(
        _state,
        _trueRecordsWithinSpecificTime,
    ) { state, recordsResource ->
        if (recordsResource is Resource.Success) {
            state.copy(
                exportRecords = recordsResource.data!!,
                exportDBInfo = calculateDBInfo(recordsResource.data)
            )
        } else {
            state
        }

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        IOState()
    )

    fun onEvent(event: IOEvent) {
        viewModelScope.launch {
            when (event) {
                is IOEvent.SelectEndExportDate -> {
                    _state.update {
                        it.copy(endDate = LocalDateTime.of(event.endDate, LocalTime.MAX))
                    }
                }

                is IOEvent.SelectStartExportDate -> {
                    _state.update {
                        it.copy(startDate = LocalDateTime.of(event.startDate, LocalTime.MIN))
                    }
                }

                is IOEvent.OnExport -> {
                    _state.update {
                        it.copy(
                            updateRecordValue = !it.updateRecordValue, exportConfirmation = false
                        )
                    }
                    state.value.run {
                        if (exportRecords.isNotEmpty()) {
                            IOUseCases.outputToCSV(
                                event.uri,
                                formatDateYear(startDate.toLocalDate()) + "- " + formatDateYear(
                                    endDate.toLocalDate()
                                ),
                                exportRecords
                            )
                        }
                    }
                }

                is IOEvent.OnImport -> {
                    val importRecords = IOUseCases.inputFromCSV(
                        event.uri.path, book = state.value.currentBook
                    )
                    _state.update {
                        it.copy(
                            importRecords = importRecords,
                            importDBInfo = calculateDBInfo(importRecords),
                            importConfirmation = true
                        )
                    }

                }

                IOEvent.OnClickedExport -> {
                    _state.update {
                        it.copy(
                            updateRecordValue = !it.updateRecordValue,
                            exportConfirmation = true
                        )
                    }
                    state.value.run {
                        _state.update {
                            it.copy(
                                exportDBInfo = calculateDBInfo(exportRecords)
                            )
                        }
                    }
                }

                IOEvent.OnAfterClickedImport -> {
                    IOUseCases.upsertTrueRecords(_state.value.importRecords)
                }

                is IOEvent.OnChooseBook -> {
                    _state.update {
                        it.copy(currentBook = it.books.first { book -> book.bookName == event.bookName })
                    }
                }
            }
        }
    }

    private fun calculateDBInfo(trueRecords: List<TrueRecord>): DBInfo {
        val sumOfRecords: Int = trueRecords.size
        val sumOfAccounts: Int = countUniqueAccounts(trueRecords)
        val sumOfCategories: Int = countUniqueCategories(trueRecords)
        val sumOfExpense: Int = countRecordsByType(trueRecords, RecordType.Expense)
        val sumOfIncome: Int = countRecordsByType(trueRecords, RecordType.Income)
        val sumOfTransfer: Int = countRecordsByType(trueRecords, RecordType.Transfer)
        return DBInfo(
            sumOfRecords = sumOfRecords,
            sumOfAccounts = sumOfAccounts,
            sumOfCategories = sumOfCategories,
            sumOfExpense = sumOfExpense,
            sumOfIncome = sumOfIncome,
            sumOfTransfer = sumOfTransfer
        )
    }

    private fun countRecordsByType(trueRecords: List<TrueRecord>, recordType: RecordType): Int {
        val uniqueRecordType = mutableSetOf<Record>()

        for (trueRecord in trueRecords) {
            if (trueRecord.record.recordType == recordType) {
                uniqueRecordType.add(trueRecord.record)
            }
        }

        return uniqueRecordType.size
    }

    private fun countUniqueAccounts(trueRecords: List<TrueRecord>): Int {
        val uniqueAccounts = mutableSetOf<String>()

        for (trueRecord in trueRecords) {
            uniqueAccounts.add(trueRecord.fromWallet.walletName)
            uniqueAccounts.add(trueRecord.toWallet.walletName)
        }

        return uniqueAccounts.size
    }

    private fun countUniqueCategories(trueRecords: List<TrueRecord>): Int {
        val uniqueCategories = mutableSetOf<String>()

        for (trueRecord in trueRecords) {
            uniqueCategories.add(trueRecord.toCategory.categoryName)
        }

        return uniqueCategories.size
    }


}

