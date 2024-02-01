package com.fredy.mysavings.Data.CSV

import android.util.Log
import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatDateTime
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDateTime

interface CSVDao {
    fun inputToCSV(
        filename: String,
        trueRecords: List<TrueRecord>,
        delimiter: String = ","
    )

    fun outputFromCSV(
        filename: String,
        delimiter: String = ","
    ): List<TrueRecord>
}

class CSVDaoImpl: CSVDao {
    override fun outputFromCSV(
        filename: String,
        delimiter: String
    ): List<TrueRecord> {
        return try {
            FileInputStream("$filename.csv").readCsv(
                delimiter
            )
        } catch (e: IOException) {
            Log.e(TAG, "Error reading CSV: $e")
            emptyList()
        }
    }

    override fun inputToCSV(
        filename: String,
        trueRecords: List<TrueRecord>,
        delimiter: String
    ) {
        try {
            FileOutputStream("$filename.csv").writeCsv(
                trueRecords,
                delimiter
            )
        } catch (e: IOException) {
            Log.e(TAG, "Error writing CSV: $e")
        }
    }

    private fun InputStream.readCsv(delimiter: String): List<TrueRecord> {
        val reader = bufferedReader()
        val header = reader.readLine()?.split(
            delimiter
        ) ?: emptyList()
        val records = mutableListOf<TrueRecord>()
        reader.forEachLine { line ->
            val values = line.split(delimiter)
            if (values.size == header.size) {
                val trueRecord = createTrueRecordFromValues(
                    header,
                    values
                )
                records.add(trueRecord)
            } else {
                Log.w(
                    TAG,
                    "Skipping malformed CSV line: $line"
                )
            }
        }
        return records
    }

    private fun createTrueRecordFromValues(
        header: List<String>,
        values: List<String>
    ): TrueRecord {
        val recordValues = header.zip(values).toMap()

        val recordDateTime = LocalDateTime.parse(recordValues["Record Date Time"]!!)
        val recordAmount = recordValues["Record Amount"]!!.toDouble()
        val recordCurrency = recordValues["Record Currency"]!!
        val recordType = RecordType.valueOf(recordValues["Record Type"]!!)
        val recordNotes = recordValues["Record Notes"] ?: ""

        val senderName = recordValues["From Account Name"]!!
        val senderIcon = recordValues["From Account Icon"]!!
        val recipientAccountName = recordValues["To Account Name"]
        val recipientAccountIcon = recordValues["To Account Icon"]
        val recipientCategoryName =  recordValues["To Category Name"]
        val recipientCategoryIcon =  recordValues["To Category Icon"]


        val record = Record(
            recordTimestamp = TimestampConverter.fromDateTime(recordDateTime),
            recordAmount = recordAmount,
            recordCurrency = recordCurrency,
            recordType = recordType,
            recordNotes = recordNotes,
        )

        val fromAccount = Account(accountName = senderName, accountIcon = senderIcon.toInt())
        val toAccount = if (recipientAccountName != null && recipientAccountIcon!=null) Account(accountName = recipientAccountName, accountIcon = recipientAccountIcon.toInt()) else Account()
        val toCategory = if (recipientCategoryName != null && recipientCategoryIcon!=null) Category(categoryName = recipientCategoryName, categoryIcon = recipientCategoryIcon.toInt()) else Category()

        return TrueRecord(
            record,
            fromAccount,
            toAccount,
            toCategory
        )
    }


    private fun OutputStream.writeCsv(
        trueRecords: List<TrueRecord>,
        delimiter: String = ","
    ) {
        val writer = bufferedWriter()
        val propertyNames = listOf(
            "Record Date Time",
            "Record Type",
            "From Account Name",
            "To Category Name",
            "To Account Name",
            "Record Amount",
            "Record Notes",
            "From Account Icon",
            "To Category Icon",
            "To Account Icon"
        ).joinToString(delimiter) {
            it.replace(
                "\"", "\"\""
            )
        }
        writer.write(propertyNames)
        writer.newLine()
        trueRecords.forEach {
            writer.write(trueRecordToString(it))
            writer.newLine()
        }
        writer.flush()
    }

    private fun trueRecordToString(trueRecord: TrueRecord): String {
        var toCategoryName = "null"
        var toAccountName = "null"
        var toCategoryIcon = "null"
        var toAccountIcon = "null"
        if (trueRecord.toAccount == trueRecord.fromAccount) {
            toCategoryName = trueRecord.toCategory.categoryName
            toCategoryIcon = trueRecord.toCategory.categoryIcon.toString()
        } else {
            toAccountName = trueRecord.toAccount.accountName
            toAccountIcon = trueRecord.toAccount.accountIcon.toString()
        }

        val values = listOf(
            formatDateTime(trueRecord.record.recordDateTime),
            trueRecord.record.recordType.name,
            trueRecord.fromAccount.accountName,
            toCategoryName,
            toAccountName,
            formatBalanceAmount(
                trueRecord.record.recordAmount,
                trueRecord.record.recordCurrency
            ),
            trueRecord.record.recordNotes,
            trueRecord.fromAccount.accountIcon.toString(),
            toCategoryIcon,
            toAccountIcon
        )
        val escapedValues = values.map {
            it.replace("\"", "\"\"")
        }
        return escapedValues.joinToString(",")
    }


}
