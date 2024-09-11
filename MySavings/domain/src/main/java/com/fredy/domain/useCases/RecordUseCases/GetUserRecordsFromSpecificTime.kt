package com.fredy.domain.useCases.RecordUseCases

import com.fredy.core.util.resource.DataError
import com.fredy.core.util.resource.Resource
import com.fredy.domain.enums.RecordType
import com.fredy.domain.enums.SortType
import com.fredy.domain.model.Book
import com.fredy.domain.model.Record
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.domain.useCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Feature.Domain.Util.Mappers.filterRecordCurrency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
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
    ): Flow<Resource<List<Record>, DataError.Local>> {
        return flow<Resource<List<Record>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser.firebaseUserId
                val userCurrency = currentUser.userCurrency
                Timber.i(
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
                    Timber.i(
                        "getUserRecordsFromSpecificTime.Data: $data",

                        )
                    emit(Resource.Success(data))
                }
            }
        }.catch { e ->
            Timber.e(
                "getUserRecordsFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
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