package com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.BookMap
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.BookRepository
import com.fredy.mysavings.Feature.Domain.Repository.RecordRepository
import com.fredy.mysavings.Feature.Domain.Util.Mappers.toBookSortedMaps
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetAllRecords(
    private val recordRepository: RecordRepository,
    private val authRepository: AuthRepository,
    private val bookRepository: BookRepository
) {
    operator fun invoke(): Flow<Resource<List<BookMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

            val books = bookRepository.getUserBooks(userId).first()

            recordRepository.getRecordMaps(userId).map {
                it.toBookSortedMaps(books)
            }.collect { data ->
                Log.i(
                    "getAllRecords.data: $data"
                )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Log.e(
                "getAllRecords.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}