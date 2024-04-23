package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Model.BookMap
import com.fredy.mysavings.Feature.Domain.Model.TrueRecord
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.BookRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Util.Mappers.filterTrueRecordCurrency
import com.fredy.mysavings.Util.Mappers.toBookSortedMaps
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class GetUserTrueRecordMapsFromSpecificTime(
    // main screen
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
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
            val currentUser = authRepository.getCurrentUser()!!
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
            }.collect { data ->
                val bookMap = books.toBookSortedMaps(data, sortType)
                Log.i("getUserTrueRecordMapsFromSpecificTime.Data: $bookMap")
                emit(Resource.Success(bookMap))
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