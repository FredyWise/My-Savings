package com.fredy.addrecord.data.mappers

import com.fredy.addrecord.data.tabScannerModel.tabScannerDTO.ResultResponse
import com.fredy.data.database.converter.TimestampConverter
import com.fredy.domain.enums.RecordType
import timber.log.Timber
import java.time.LocalDateTime
import com.fredy.data.database.dto.Record as DataRecord
import java.time.format.DateTimeFormatter

fun ResultResponse.convertToDataRecords(): List<DataRecord> {
    val records = mutableListOf<DataRecord>()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val recordTimestamp =
        TimestampConverter.fromDateTime(LocalDateTime.parse(this.result.date, formatter))

    this.result.lineItems.forEachIndexed { index, item ->
        val discount = item.discount.replace(".000", "").replace(",", "").toDoubleOrNull() ?: 0.0
        val recordAmount =
            item.lineTotal.replace(".000", "").replace(",", "").toDoubleOrNull() ?: 0.0
        Timber.e("${recordAmount}")
        val recordNotes = """
            Address: ${this.result.address}
            Establishment: ${this.result.establishment}
            Document Type: ${this.result.documentType}
            Item Description: ${item.desc}
            Item Quantity: ${item.qty}
            """.trimIndent()

        val record = DataRecord(
            recordId = "$index",
            walletIdFromFk = "",
            walletIdToFk = "",
            categoryIdFk = "",
            userIdFk = "",
            bookIdFk = "",
            recordTimestamp = recordTimestamp,
            recordAmount = recordAmount,
            recordCurrency = this.result.currency,
            recordType = RecordType.Expense,
            recordNotes = recordNotes
        )

        records.add(record)
    }

    this.result.summaryItems.firstOrNull { it.lineType == "Discount" }?.let { item ->
        val recordAmount =
            item.lineTotal.replace(".000", "").replace(",", "").toDoubleOrNull() ?: 0.0
        val record = DataRecord(
            recordId = "Discount",
            walletIdFromFk = "",
            walletIdToFk = "",
            categoryIdFk = "",
            userIdFk = "",
            bookIdFk = "",
            recordTimestamp = recordTimestamp,
            recordAmount = recordAmount,
            recordCurrency = this.result.currency,
            recordType = RecordType.Income,
            recordNotes = "Item Description: Total Discount: \n"
        )

        records.add(record)
    }

    return records
}
