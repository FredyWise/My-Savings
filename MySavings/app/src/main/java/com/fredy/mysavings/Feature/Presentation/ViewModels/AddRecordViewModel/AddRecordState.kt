package com.fredy.mysavings.Feature.Presentation.ViewModels.AddRecordViewModel

import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Model.Wallet
import java.time.LocalDate
import java.time.LocalTime

data class AddRecordState(
    val recordId: String = "",
    val walletIdFromFk: String? = null,
    val fromWallet: Wallet = Wallet(),
    val walletIdToFk: String? = null,
    val toWallet: Wallet = Wallet(),
    val categoryIdFk: String? = null,
    val bookIdFk: String = "",
    val toCategory: Category = Category(),
    val recordDate: LocalDate = LocalDate.now(),
    val recordTime: LocalTime = LocalTime.now(),
    val recordAmount: Double = 0.0,
    val previousAmount: Double = 0.0,
    val recordCurrency: String = "",
    val recordNotes: String = "",
    val recordType: RecordType = RecordType.Expense,
    val isShowWarning: Boolean = false,
    val isAgreeToConvert: Boolean = false,
    //special Bulk
    val records: List<Record>? = emptyList(),
    val record: Record? = null
)