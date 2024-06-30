package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class UpdateRecordItemWithDeletedBook(
    private val recordRepository: RecordRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(book: Book) {
        withContext(Dispatchers.IO) {
            Log.d("startDelBook")
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
            val records = recordRepository.getUserRecords(userId).first()
            Log.d("$records")
            val tempRecords = records.filter {
                it.bookIdFk == book.bookId
            }
            recordRepository.deleteAllRecordItems(tempRecords)
        }
    }
}