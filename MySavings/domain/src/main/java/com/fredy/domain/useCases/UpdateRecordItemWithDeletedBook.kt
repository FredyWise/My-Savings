package com.fredy.domain.useCases

import com.fredy.domain.model.Book
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

class UpdateRecordItemWithDeletedBook(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(book: Book) {
        withContext(Dispatchers.IO) {
            Timber.d("startDelBook")
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser.firebaseUserId
                val records = recordRepository.getUserRecords(userId).first()
                Timber.d("$records")
                val tempRecords = records.filter {
                    it.bookIdFk == book.bookId
                }
                recordRepository.deleteAllRecordItems(tempRecords)
            }
        }
    }
}