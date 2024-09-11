package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.domain.model.Book
import com.fredy.domain.model.BookMap
import com.fredy.domain.model.TrueRecord
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.BookRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.domain.useCases.CurrencyUseCases.currencyConverter
import com.fredy.domain.util.mappers.filterTrueRecordCurrency
import com.fredy.domain.util.mappers.toBookSortedMaps
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class GetUserTrueRecordMapsFromSpecificTime(
    // main screen
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
    private val currencyUseCases: CurrencyUseCases,
    private val bookRepository: BookRepository,
) {
    operator fun invoke(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sortType: SortType,
        currency: List<String>,
        useUserCurrency: Boolean,
        book: Book,
    ): Flow<Resource<List<BookMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val userCurrency = if (currency.isEmpty()) "" else currentUser.userCurrency
            Log.i(
                "getUserTrueRecordMapsFromSpecificTime: $startDate\n:\n$endDate,\ncurrency: $currency"
            )

            val books = bookRepository.getUserBooks(userId).first()

            recordRepository.getUserTrueRecordsFromSpecificTime(
                userId,
                startDate,
                endDate,
            ).map { records ->
                records.filter { it.record.bookIdFk == book.bookId }
                    .filterTrueRecordCurrency(currency + userCurrency)
                    .convertRecordCurrency(userCurrency, useUserCurrency)
                    .toBookSortedMaps(books)
            }.collect { data ->
                Log.i("getUserTrueRecordMapsFromSpecificTime.Data: $data")
                emit(Resource.Success(data))
            }

        }.catch { e ->
            Log.e(
                "getUserTrueRecordMapsFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }


    private suspend fun List<TrueRecord>.convertRecordCurrency(
        userCurrency: String,
        useUserCurrency: Boolean
    ): List<TrueRecord> {
        return if (useUserCurrency) {
            this.map { trueRecord ->
                trueRecord.copy(
                    record = trueRecord.record.copy(
                        recordAmount = currencyUseCases.currencyConverter(
                            trueRecord.record.recordAmount,
                            trueRecord.record.recordCurrency,
                            userCurrency
                        ),
                        recordCurrency = userCurrency
                    )
                )
            }
        } else {
            this
        }
    }
}