package com.fredy.mysavings.Feature.Presentation.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases.BookUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.IOUseCases.IOUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.formatDateYear
import com.fredy.mysavings.Feature.Presentation.ViewModels.Event.IOEvent
import dagger.hilt.android.lifecycle.HiltViewModel
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
                    _state.value.importRecords.forEach {
                        recordUseCases.upsertRecordItem(it.record)
                    }
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

data class IOState(
    val startDate: LocalDateTime = LocalDateTime.now().minusMonths(1),
    val endDate: LocalDateTime = LocalDateTime.now(),
    val books: List<Book> = emptyList(),
    val currentBook: Book = Book(bookName = ""),
    val exportRecords: List<TrueRecord> = emptyList(),
    val importRecords: List<TrueRecord> = emptyList(),
    val exportConfirmation: Boolean = false,
    val importConfirmation: Boolean = false,
    val updateRecordValue: Boolean = false,
    val dbInfo: DBInfo = DBInfo(),
    val exportDBInfo: DBInfo = DBInfo(),
    val importDBInfo: DBInfo = DBInfo()
)

data class DBInfo(
    val sumOfRecords: Int = 0,
    val sumOfAccounts: Int = 0,
    val sumOfCategories: Int = 0,
    val sumOfExpense: Int = 0,
    val sumOfIncome: Int = 0,
    val sumOfTransfer: Int = 0,
)