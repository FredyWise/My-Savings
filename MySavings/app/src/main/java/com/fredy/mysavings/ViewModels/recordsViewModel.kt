package com.fredy.mysavings.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.fredy.mysavings.Data.Records.Item
import com.fredy.mysavings.Data.Records.Record
import com.fredy.mysavings.Data.Records.RecordsData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class recordsViewModel: ViewModel() {
    var recordsData by mutableStateOf(
        RecordsData()
    )

    fun formatDate(date: LocalDate): String {
        return DateTimeFormatter.ofPattern(
            "MMM dd, EEEE"
        ).format(date)
    }

    fun sortRecords() {
        recordsData.records.forEach { record ->
            record.items = sortItems(record.items)
        }

        recordsData = recordsData.copy(
            records = recordsData.records.sortedWith(
                compareBy(
                    Record::date
                )
            )
        )
    }

    fun sortItems(items: List<Item>): List<Item> {
        return items.sortedWith(
            compareBy(
                Item::time
            )
        )
    }

}

