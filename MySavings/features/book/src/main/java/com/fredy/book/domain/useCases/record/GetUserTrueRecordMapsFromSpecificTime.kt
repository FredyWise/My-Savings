package com.fredy.book.domain.useCases.record

import com.fredy.book.domain.BookSpecificRepository
import com.fredy.book.domain.RecordSpecificRepository
import com.fredy.currency.domain.useCases.CurrencyUseCases
import com.fredy.currency.domain.useCases.currencyConverter
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.enums.SortType
import com.fredy.domain.mappers.recordUIMapper.filterTrueRecordCurrency
import com.fredy.domain.mappers.recordUIMapper.toBookSortedMaps
import com.fredy.domain.model.Book
import com.fredy.domain.model.BookMap
import com.fredy.domain.model.TrueRecord

import com.fredy.domain.repository.UserRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime

class GetUserTrueRecordMapsFromSpecificTime(
    // main screen
    private val recordSpecificRepository: RecordSpecificRepository,
    private val userRepository: UserRepository,
    private val currencyUseCases: CurrencyUseCases,
    private val bookSpecificRepository: BookSpecificRepository,
) {
    operator fun invoke(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        sortType: SortType,
        currency: List<String>,
        useUserCurrency: Boolean,
        book: Book,
    ): Flow<Resource<List<BookMap>, DataError.Local>> {
        return flow<Resource<List<BookMap>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser.firebaseUserId
                val userCurrency = if (currency.isEmpty()) "" else currentUser.userCurrency
                Timber.i(
                    "getUserTrueRecordMapsFromSpecificTime: $startDate\n:\n$endDate,\ncurrency: $currency"
                )

                val books = bookSpecificRepository.getUserBooks(userId).first()

                recordSpecificRepository.getUserTrueRecordsFromSpecificTime(
                    userId,
                    startDate,
                    endDate,
                ).map { records ->
                    records.filter { it.record.bookIdFk == book.bookId }
                        .filterTrueRecordCurrency(currency + userCurrency)
                        .convertRecordCurrency(userCurrency, useUserCurrency)
                        .toBookSortedMaps(books)
                }.collect { data ->
                    Timber.i("getUserTrueRecordMapsFromSpecificTime.Data: $data")
                    emit(Resource.Success(data))
                }
            }
        }.catch { e ->
            Timber.e(
                "getUserTrueRecordMapsFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
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