package com.fredy.domain.useCases.RecordUseCases

import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.enums.RecordType
import com.fredy.domain.enums.SortType
import com.fredy.domain.mappers.recordUIMapper.filterRecordCurrency
import com.fredy.domain.model.Book
import com.fredy.domain.model.Category
import com.fredy.domain.model.CategoryWithAmount
import com.fredy.domain.model.Record
import com.fredy.domain.repository.CategoryRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.useCases.CurrencyUseCases.CurrencyUseCases
import com.fredy.domain.useCases.CurrencyUseCases.currencyConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime

class GetUserCategoriesWithAmountFromSpecificTime(
    private val recordRepository: RecordRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val currencyUseCases: CurrencyUseCases,
) {
    operator fun invoke(
        categoryType: RecordType,
        sortType: SortType,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        currency: List<String>,
        useUserCurrency: Boolean,
        book: Book,
    ): Flow<Resource<List<CategoryWithAmount>, DataError.Local>> {
        return flow<Resource<List<CategoryWithAmount>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser.firebaseUserId
                val userCurrency = currentUser.userCurrency
                Timber.i(
                    "getUserCategoriesWithAmountFromSpecificTime: $currency\n$categoryType\n$startDate\n:\n$endDate"
                )
                val userCategories = categoryRepository.getUserCategories(
                    userId
                ).first()

                recordRepository.getUserRecordsByTypeFromSpecificTime(
                    userId,
                    listOf(categoryType),
                    startDate,
                    endDate,
                ).map { records ->
                    records.filter { it.bookIdFk == book.bookId }
                        .filterRecordCurrency(currency)
                        .combineSameCurrencyCategory(
                            sortType,
                            userCategories,
                            userCurrency,
                            useUserCurrency
                        )
                }.collect { data ->
                    Timber.i(
                        "getUserCategoriesWithAmountFromSpecificTime.Data: $data",

                        )
                    emit(Resource.Success(data))
                }
            }
        }.catch { e ->
            Timber.e(
                "getUserCategoriesWithAmountFromSpecificTime.Error: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }

    private suspend fun List<Record>.combineSameCurrencyCategory(
        sortType: SortType = SortType.DESCENDING,
        userCategories: List<Category>,
        userCurrency: String,
        useUserCurrency: Boolean
    ): List<CategoryWithAmount> {
        val categoryWithAmountMap = mutableMapOf<String, CategoryWithAmount>()
        this.forEach { record ->
            val currency = if (useUserCurrency) userCurrency else record.recordCurrency
            val key = record.categoryIdFk + currency
            val existingCategory = categoryWithAmountMap[key]
            val amount = if (useUserCurrency) {
                currencyUseCases.currencyConverter(
                    record.recordAmount,
                    record.recordCurrency,
                    userCurrency
                )
            } else {
                record.recordAmount
            }
            if (existingCategory != null) {
                categoryWithAmountMap[key] = existingCategory.copy(
                    amount = existingCategory.amount + amount,
                )
            } else {
                val newCategory = CategoryWithAmount(
                    category = userCategories.first { it.categoryId == record.categoryIdFk },
                    amount = amount,
                    currency = currency
                )
                categoryWithAmountMap[key] = newCategory
            }
        }

        val data = categoryWithAmountMap.values.toList().let { value ->
            if (sortType == SortType.ASCENDING) {
                value.sortedBy { it.amount }
            } else {
                value.sortedByDescending { it.amount }

            }
        }
        return data
    }

}