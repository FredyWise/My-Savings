package com.fredy.mysavings.Data.CSV

import android.content.Context
import android.content.Context.STORAGE_SERVICE
import android.os.Build
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.fredy.mysavings.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Data.Database.Model.Account
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Database.Model.Record
import com.fredy.mysavings.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Util.TAG
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatDateTime
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDateTime


interface CSVDao {
    fun outputToCSV(
        directory: String,
        filename: String,
        trueRecords: List<TrueRecord>,
        delimiter: String = ","
    )

    fun inputFromCSV(
        directory: String,
        filename: String,
        delimiter: String = ","
    ): List<TrueRecord>
}

class CSVDaoImpl(private val context: Context): CSVDao {
    override fun inputFromCSV(
        directory: String,
        filename: String,
        delimiter: String
    ): List<TrueRecord> {
        val name = filename.replace(" ","_")
        return try {
            FileInputStream("$directory/$name.csv").use { inputStream ->
                inputStream.readCsv(delimiter)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error reading CSV: $e")
            emptyList()
        }
    }

    override fun outputToCSV(
        directory: String,
        filename: String,
        trueRecords: List<TrueRecord>,
        delimiter: String
    ) {

        try {
            val storageManager = getSystemService(context,StorageManager::class.java)!!
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                storageManager.storageVolumes[0].directory?.path
            } else {
                null
            }
            val directory = directory.replace("%3A",":").replace("%20"," ").replace("%2F","/").replace("content://com.android.externalstorage.documents/tree/primary:",uri.toString()+"/")
            val filename = filename.replace(" ","")

            val file = File("$directory/$filename.csv")
            Log.e(TAG, "outputToCSV: $directory/$filename.csv")
            if (!file.exists()) {
                Log.e(TAG, "outputToCSV: $file\n$directory/$filename.csv")
                file.createNewFile()
            }
            FileOutputStream(file).use { outputStream ->
                outputStream.writeCsv(trueRecords, delimiter)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error writing CSV: $e")
        }
    }

    private fun InputStream.readCsv(delimiter: String): List<TrueRecord> {
        bufferedReader().useLines { lines ->
            val header = lines.firstOrNull()?.split(delimiter) ?: emptyList()
            return lines.drop(1).mapNotNull { line ->
                val values = line.split(delimiter)
                if (values.size == header.size) {
                    createTrueRecordFromValues(header, values)
                } else {
                    Log.w(TAG, "Skipping malformed CSV line: $line")
                    null
                }
            }.toList()
        }
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
            formatDateTime(trueRecord.record.recordDateTime).replace(",",""),
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
