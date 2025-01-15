package com.fredy.domain.useCases

import com.fredy.domain.model.Category
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.util.DefaultData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

class UpdateRecordItemWithDeletedCategory(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(category: Category) {
        withContext(Dispatchers.IO) {
            Timber.d("startDelCategory")
            val currentUser = userRepository.getCurrentUser()!!
            val userId = currentUser?.firebaseUserId ?: ""
            val records = recordRepository.getUserRecords(userId).first()
            Timber.d("$records")
            val tempRecords = records.filter {
                it.categoryIdFk == category.categoryId
            }.map {
                it.copy(categoryIdFk = DefaultData.deletedCategory.categoryId + userId)
            }
            recordRepository.upsertAllRecordItems(tempRecords)
        }
    }
}