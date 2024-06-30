package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Domain.Util.Mappers.filterRecordCurrency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class GetUserRecordsFromSpecificTime(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
    private val currencyUseCases: CurrencyUseCases,
) {
    operator fun invoke(
        recordType: RecordType,
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
        useUserCurrency: Boolean,
        book: Book,
    ): Flow<Resource<List<Record>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = currentUser.userCurrency
            Log.i(
                "getUserRecordsFromSpecificTime: $startDate\n:\n$endDate"
            )

            recordRepository.getUserRecordsByTypeFromSpecificTime(
                userId,
                listOf(recordType),
                startDate,
                endDate,
            ).map { records ->
                records.filter { it.bookIdFk == book.bookId }
                    .filterRecordCurrency(currency)
                    .combineSameCurrencyData(sortType, userCurrency, useUserCurrency)
            }.collect { data ->
                Log.i(
                    "getUserRecordsFromSpecificTime.Data: $data",

                    )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.e(
                "getUserRecordsFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }

    private suspend fun List<Record>.combineSameCurrencyData(
        sortType: SortType = SortType.DESCENDING,
        userCurrency: String,
        useUserCurrency: Boolean
    ): List<Record> {
        val recordsMap = mutableMapOf<String, Record>()
        this.forEach { record ->
            val key = record.recordDateTime.toLocalDate().toString()
            val existingRecord = recordsMap[key]
            val currency = if (useUserCurrency) userCurrency else record.recordCurrency
            val amount = if (useUserCurrency) {
                currencyUseCases.currencyConverter(
                    record.recordAmount,
                    record.recordCurrency,
                    userCurrency
                )
            } else {
                record.recordAmount
            }

            if (existingRecord != null) {
                val tempAmount =
                    if (record.recordCurrency != existingRecord.recordCurrency && !useUserCurrency) {
                        currencyUseCases.currencyConverter(
                            amount,
                            record.recordCurrency,
                            existingRecord.recordCurrency
                        )
                    } else {
                        amount
                    }
                recordsMap[key] = existingRecord.copy(
                    recordAmount = existingRecord.recordAmount + tempAmount,
                )
            } else {
                recordsMap[key] = record.copy(recordAmount = amount, recordCurrency = currency)
            }
        }
        val data = recordsMap.values.toList().let { value ->
            if (sortType == SortType.ASCENDING) {
                value.sortedBy { it.recordAmount }
            } else {
                value.sortedByDescending { it.recordAmount }
            }
        }
        return data
    }


}