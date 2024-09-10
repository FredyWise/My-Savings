package com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel

import com.fredy.domain.enums.RecordType
import com.fredy.domain.model.Category
import com.fredy.domain.model.Record
import com.fredy.domain.model.Wallet
import java.time.LocalDate
import java.time.LocalTime

data class AddRecordState(
    val recordId: String = "",
    val walletIdFromFk: String? = null,
    val fromWallet: Wallet = Wallet(),
    val walletIdToFk: String? = null,
    val toWallet: Wallet = Wallet(),
    val categoryIdFk: String? = null,
    val toCategory: Category = Category(),
    val bookIdFk: String = "",
    val recordDate: LocalDate = LocalDate.now(),
    val recordTime: LocalTime = LocalTime.now(),
    val recordAmount: Double = 0.0,
    var previousAmount: Double = 0.0,
    val recordCurrency: String = "",
    val recordNotes: String = "",
    val recordType: RecordType = RecordType.Expense,
    var isShowWarning: Boolean = false,
    val isAgreeToConvert: Boolean = false,
    //special Bulk
    val records: List<Record>? = emptyList(),
    val record: Record = Record(),
    val isAdding: Boolean = false,
    val categoryIncomeIdFk: String? = null,
    val toIncomeCategory: Category = Category(),
)