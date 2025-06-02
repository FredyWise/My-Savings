package com.fredy.search.data


import com.fredy.domain.mappers.recordUIMapper.toBookSortedMaps
import com.fredy.domain.model.BookMap
import com.fredy.domain.repository.BookRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.search.domain.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val recordRepository: RecordRepository,
    private val bookRepository: BookRepository
) : SearchRepository {

    override fun getBookMaps(userId: String): Flow<List<BookMap>> {
        Timber.i("getRecordMapsRepo: $userId")
        return flow {
            val userBooks = bookRepository.getUserBooks(
                userId
            ).first()
            recordRepository.getRecordMaps(userId).collect { records ->
                Timber.i("getRecordMapsRepo.Data: $records")
                emit(records.toBookSortedMaps(userBooks))
            }

        }
    }

}