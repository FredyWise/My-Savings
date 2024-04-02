package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Repository.CSVRepository
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.formatDateYear
import com.fredy.mysavings.ViewModels.Event.IOEvent
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
    private val recordRepository: RecordRepository,
    private val csvRepository: CSVRepository,
) : ViewModel() {

    init {
        viewModelScope.launch {
            csvRepository.getDBInfoFlow()
                .collectLatest { dbInfo ->
                    _state.update {
                        it.copy(
                            dbInfo = dbInfo
                        )
                    }
                }
        }
    }

    private val _state = MutableStateFlow(
        IOState()
    )

    private val _trueRecordsWithinSpecificTime = _state.flatMapLatest { state ->
        recordRepository.getAllTrueRecordsWithinSpecificTime(state.startDate, state.endDate)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    val state = combine(
        _state,
        _trueRecordsWithinSpecificTime
    ) { state, recordsResource ->
        if (recordsResource is Resource.Success && recordsResource.data!!.isNotEmpty()) {
            state.copy(
                exportRecords = recordsResource.data.toList(),
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
                            csvRepository.outputToCSV(
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
                    val importRecords = csvRepository.inputFromCSV(
                        event.uri.path,
                    )
                    if (importRecords.isNotEmpty()) {
                        _state.update {
                            it.copy(
                                importRecords = importRecords,
                                importDBInfo = calculateDBInfo(importRecords),
                                importConfirmation = true
                            )
                        }
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
                        if (exportRecords.isNotEmpty()) {
                            _state.update {
                                it.copy(
                                    exportDBInfo = calculateDBInfo(exportRecords)
                                )
                            }
                        }
                    }
                }

                IOEvent.OnAfterClickedImport -> {
                    _state.value.importRecords.forEach {
                        recordRepository.upsertRecordItem(it.record)
                    }
                }
            }
        }
    }

    private fun calculateDBInfo(records: List<TrueRecord>): DBInfo {
        val sumOfRecords: Int = records.size
        val sumOfAccounts: Int = countUniqueAccounts(records)
        val sumOfCategories: Int = countUniqueCategories(records)
        val sumOfExpense: Int = countRecordsByType(records, RecordType.Expense)
        val sumOfIncome: Int = countRecordsByType(records, RecordType.Income)
        val sumOfTransfer: Int = countRecordsByType(records, RecordType.Transfer)
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
        val uniqueRecordType = mutableSetOf<String>()

        for (trueRecord in trueRecords) {
            if (trueRecord.record.recordType == recordType) {
                uniqueRecordType.add(trueRecord.record.recordId)
            }
        }

        return uniqueRecordType.size
    }

    private fun countUniqueAccounts(trueRecords: List<TrueRecord>): Int {
        val uniqueAccounts = mutableSetOf<String>()

        for (trueRecord in trueRecords) {
            uniqueAccounts.add(trueRecord.fromAccount.accountName)
            uniqueAccounts.add(trueRecord.toAccount.accountName)
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
    val exportRecords: List<TrueRecord> = listOf(),
    val importRecords: List<TrueRecord> = listOf(),
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