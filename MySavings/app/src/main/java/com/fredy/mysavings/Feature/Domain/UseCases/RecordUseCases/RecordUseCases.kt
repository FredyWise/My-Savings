package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Database.Model.TrueRecord
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.ViewModels.RecordMap
import kotlinx.coroutines.flow.Flow
import com.fredy.mysavings.Feature.Data.Database.Model.Record
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Feature.Domain.Repository.AccountWithAmountType
import com.fredy.mysavings.Feature.Domain.Repository.CategoryWithAmount
import com.fredy.mysavings.Util.BalanceItem
import java.time.LocalDateTime

data class RecordUseCases(
    val upsertRecordItem: UpsertRecordItem,
    val deleteRecordItem: DeleteRecordItem,
    val updateRecordItemWithDeletedAccount: UpdateRecordItemWithDeletedAccount,
    val updateRecordItemWithDeletedCategory: UpdateRecordItemWithDeletedCategory,
    val getRecordById: GetRecordById,
    val getAllTrueRecordsWithinSpecificTime: GetAllTrueRecordsWithinSpecificTime,
    val getAllRecords: GetAllRecords,
    val getUserCategoryRecordsOrderedByDateTime: GetUserCategoryRecordsOrderedByDateTime,
    val getUserAccountRecordsOrderedByDateTime: GetUserAccountRecordsOrderedByDateTime,
    val getUserTrueRecordMapsFromSpecificTime: GetUserTrueRecordMapsFromSpecificTime,
    val getUserRecordsFromSpecificTime: GetUserRecordsFromSpecificTime,
    val getUserCategoriesWithAmountFromSpecificTime: GetUserCategoriesWithAmountFromSpecificTime,
    val getUserAccountsWithAmountFromSpecificTime: GetUserAccountsWithAmountFromSpecificTime,
    val getUserTotalAmountByType: GetUserTotalAmountByType,
    val getUserTotalAmountByTypeFromSpecificTime: GetUserTotalAmountByTypeFromSpecificTime,
    val getUserTotalRecordBalance: GetUserTotalRecordBalance
)

class UpsertRecordItem(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(record: Record): String {
        return recordRepository.upsertRecordItem(record)
    }
}

class DeleteRecordItem(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(record: Record) {
        recordRepository.deleteRecordItem(record)
    }
}

class UpdateRecordItemWithDeletedAccount(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(account: Account) {
        recordRepository.updateRecordItemWithDeletedAccount(account)
    }
}

class UpdateRecordItemWithDeletedCategory(
    private val recordRepository: RecordRepository
) {
    suspend operator fun invoke(category: Category) {
        recordRepository.updateRecordItemWithDeletedCategory(category)
    }
}

class GetRecordById(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(recordId: String): Flow<TrueRecord> {
        return recordRepository.getRecordById(recordId)
    }
}

class GetAllTrueRecordsWithinSpecificTime(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(startDate: LocalDateTime, endDate: LocalDateTime): Flow<Resource<List<TrueRecord>>> {
        return recordRepository.getAllTrueRecordsWithinSpecificTime(startDate, endDate)
    }
}

class GetAllRecords(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(): Flow<Resource<List<RecordMap>>> {
        return recordRepository.getAllRecords()
    }
}

class GetUserCategoryRecordsOrderedByDateTime(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(categoryId: String, sortType: SortType): Flow<Resource<List<RecordMap>>> {
        return recordRepository.getUserCategoryRecordsOrderedByDateTime(categoryId, sortType)
    }
}

class GetUserAccountRecordsOrderedByDateTime(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(accountId: String, sortType: SortType): Flow<Resource<List<RecordMap>>> {
        return recordRepository.getUserAccountRecordsOrderedByDateTime(accountId, sortType)
    }
}

class GetUserTrueRecordMapsFromSpecificTime(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(startDate: LocalDateTime, endDate: LocalDateTime, sortType: SortType, currency: List<String>, useUserCurrency: Boolean): Flow<Resource<List<RecordMap>>> {
        return recordRepository.getUserTrueRecordMapsFromSpecificTime(startDate, endDate, sortType, currency, useUserCurrency)
    }
}

class GetUserRecordsFromSpecificTime(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(recordType: RecordType, sortType: SortType, startDate: LocalDateTime, endDate: LocalDateTime, currency: List<String>, useUserCurrency: Boolean): Flow<Resource<List<Record>>> {
        return recordRepository.getUserRecordsFromSpecificTime(recordType, sortType, startDate, endDate, currency, useUserCurrency)
    }
}

class GetUserCategoriesWithAmountFromSpecificTime(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(categoryType: RecordType, sortType: SortType, startDate: LocalDateTime, endDate: LocalDateTime, currency: List<String>, useUserCurrency: Boolean): Flow<Resource<List<CategoryWithAmount>>> {
        return recordRepository.getUserCategoriesWithAmountFromSpecificTime(categoryType, sortType, startDate, endDate, currency, useUserCurrency)
    }
}

class GetUserAccountsWithAmountFromSpecificTime(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(sortType: SortType, startDate: LocalDateTime, endDate: LocalDateTime, useUserCurrency: Boolean): Flow<Resource<List<AccountWithAmountType>>> {
        return recordRepository.getUserAccountsWithAmountFromSpecificTime(sortType, startDate, endDate, useUserCurrency)
    }
}

class GetUserTotalAmountByType(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(recordType: RecordType): Flow<BalanceItem> {
        return recordRepository.getUserTotalAmountByType(recordType)
    }
}

class GetUserTotalAmountByTypeFromSpecificTime(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(recordType: RecordType, startDate: LocalDateTime, endDate: LocalDateTime): Flow<BalanceItem> {
        return recordRepository.getUserTotalAmountByTypeFromSpecificTime(recordType, startDate, endDate)
    }
}

class GetUserTotalRecordBalance(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(startDate: LocalDateTime, endDate: LocalDateTime): Flow<BalanceItem> {
        return recordRepository.getUserTotalRecordBalance(startDate, endDate)
    }
}
