package com.fredy.mysavings.Feature.Data.CSV

import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import androidx.core.content.ContextCompat.getSystemService
import com.fredy.mysavings.Feature.Data.Database.Converter.TimestampConverter
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Database.Model.Record
import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Util.formatBalanceAmount
import com.fredy.mysavings.Util.formatDateYearTime
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.Reader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


interface CSVDao {
    fun outputToCSV(
        directory: String,
        filename: String,
        trueRecords: List<TrueRecord>,
        delimiter: String = ","
    )

    fun inputFromCSV(
        directory: String,
        delimiter: String = ","
    ): List<TrueRecord>
}

class CSVDaoImpl(private val context: Context) : CSVDao {
    override fun inputFromCSV(
        directory: String,
        delimiter: String
    ): List<TrueRecord> {
        return try {
            Log.i("inputFromCSV: Start")
            val storageManager = getSystemService(context, StorageManager::class.java)!!
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                storageManager.storageVolumes[0].directory?.path
            } else {
                null
            }
            val directoryPath = directory.replace("%3A", ":").replace("%20", " ")
                .replace("%2F", "/").replace("%2C", ",")
                .replace(
                    "content://com.android.externalstorage.documents/document/primary:",
                    uri.toString() + "/"
                )

            FileReader(directoryPath).use { reader ->
                reader.readCsv(delimiter)
            }
        } catch (e: IOException) {
            Log.e("Error reading CSV: $e")
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
            Log.i("outputToCSV: Start")
            Log.i("outputToCSV: $trueRecords")
            val storageManager = getSystemService(context, StorageManager::class.java)!!
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                storageManager.storageVolumes[0].directory?.path
            } else {
                null
            }
            val directoryPath = directory.replace("%3A", ":").replace("%20", " ")
                .replace("%2F", "/").replace(
                    "content://com.android.externalstorage.documents/tree/primary:",
                    uri.toString() + "/"
                )
            val filenameClean = filename.replace(" ", "")

            val file = getUniqueFile(directoryPath, "$filenameClean.csv")
            Log.i("outputToCSV: $file")

            FileWriter(file, false).use { writer ->
                writer.writeCsv(trueRecords, delimiter)
            }
        } catch (e: IOException) {
            Log.e("Error writing CSV: $e")
        }
    }


    private fun Reader.readCsv(delimiter: String): List<TrueRecord> {
        return this.useLines { lines ->
            val linesList = lines.toList()
            Log.i("readCsv: $linesList")
            val header = linesList.firstOrNull()?.split(delimiter) ?: emptyList()
            linesList.drop(1).mapNotNull { line ->
                Log.e("readCsv: $line")
                val values = line.split(delimiter)
                if (values.size == header.size) {
                    createTrueRecordFromValues(header, values)
                } else {
                    Log.w("Skipping malformed CSV line: $line")
                    null
                }
            }
        }
    }

    private fun createTrueRecordFromValues(
        header: List<String>,
        values: List<String>
    ): TrueRecord {
        val recordValues = header.zip(values).toMap()

        val recordDateTime = recordValues["Record Date Time"]!!.toLocalDateTimeConverter()
        val amount = recordValues["Record Amount"]!!.split(" ")
        val recordAmount = amount[0].toDouble()
        val recordCurrency = amount[1]
        val recordType = RecordType.valueOf(recordValues["Record Type"]!!)
        val recordNotes = recordValues["Record Notes"] ?: ""
        val senderAccount = recordValues["From Account Name"]!!.split("-")
        val senderAccountName = senderAccount[0]
        val senderAccountCurrency = senderAccount[1]
        val senderAccountIconDescription = recordValues["From Account Icon"]!!
        val recipientAccount = recordValues["To Account Name"]!!.split("-")
        val recipientAccountName = recipientAccount[0]
        val recipientAccountCurrency = recipientAccount[1]
        val recipientAccountIconDescription = recordValues["To Account Icon"]
        val recipientCategoryName = recordValues["To Category Name"]!!
        val recipientCategoryIconDescription = recordValues["To Category Icon"]


        val record = Record(
            recordTimestamp = TimestampConverter.fromDateTime(recordDateTime),
            recordAmount = recordAmount,
            recordCurrency = recordCurrency,
            recordType = recordType,
            recordNotes = recordNotes,
        )

        val fromAccount = Account(
            accountName = senderAccountName,
            accountCurrency = senderAccountCurrency,
            accountIconDescription = senderAccountIconDescription
        )
        val toAccount =
            if (recipientAccountName != null && recipientAccountIconDescription != null) Account(
                accountName = recipientAccountName,
                accountCurrency = recipientAccountCurrency,
                accountIconDescription = recipientAccountIconDescription
            ) else Account()
        val toCategory =
            if (recipientCategoryName != null && recipientCategoryIconDescription != null) Category(
                categoryName = recipientCategoryName,
                categoryIconDescription = recipientCategoryIconDescription
            ) else Category()

        return TrueRecord(
            record,
            fromAccount,
            toAccount,
            toCategory
        )
    }

    private fun FileWriter.writeCsv(
        trueRecords: List<TrueRecord>,
        delimiter: String = ","
    ) {
        val propertyNames = listOf(
            "Record Date Time",
            "Record Type",
            "From Account Name",
            "From Account Icon",
            "To Category Name",
            "To Category Icon",
            "To Account Name",
            "To Account Icon",
            "Record Amount",
            "Record Notes"
        ).joinToString(delimiter) {
            it.replace("\"", "\"\"")
        }
        write(propertyNames)
        write("\n")
        trueRecords.forEach { trueRecord ->
            write(trueRecordToString(trueRecord))
            write("\n")
        }
        flush()
    }

    private fun getUniqueFile(directory: String, filename: String): File {
        var file = File(directory, filename)
        var fileNo = 0

        while (file.exists()) {
            fileNo++
            val newName = "${filenameWithoutExtension(filename)}($fileNo).${file.extension}"
            file = File(directory, newName)
        }

        return file
    }

    private fun filenameWithoutExtension(filename: String): String {
        val dotIndex = filename.lastIndexOf('.')
        return if (dotIndex == -1) filename else filename.substring(0, dotIndex)
    }


    private fun trueRecordToString(trueRecord: TrueRecord): String {
        val values = listOf(
            formatDateYearTime(trueRecord.record.recordDateTime).replace(",", ""),
            trueRecord.record.recordType.name,
            trueRecord.fromAccount.accountName + "-" + trueRecord.fromAccount.accountCurrency,
            trueRecord.fromAccount.accountIconDescription,
            trueRecord.toCategory.categoryName,
            trueRecord.toCategory.categoryIconDescription,
            trueRecord.toAccount.accountName + "-" + trueRecord.toAccount.accountCurrency,
            trueRecord.toAccount.accountIconDescription,
            formatBalanceAmount(
                trueRecord.record.recordAmount,
                trueRecord.record.recordCurrency
            ),
            trueRecord.record.recordNotes
        )
        val escapedValues = values.map {
            it.replace("\"", "\"\"")
        }
        return escapedValues.joinToString(",")
    }

    private fun String.toLocalDateTimeConverter(): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy hh:mm a", Locale.ENGLISH)
        return LocalDateTime.parse(this, formatter)
    }
}
