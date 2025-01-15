package com.fredy.search.domain.useCases

import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.model.BookMap
import com.fredy.domain.repository.UserRepository
import com.fredy.search.domain.SearchRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class GetAllRecords(
    private val searchRepository: SearchRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Resource<List<BookMap>, DataError.Local>> {
        return flow<Resource<List<BookMap>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val userId = currentUser?.firebaseUserId ?: ""


            searchRepository.getBookMaps(userId).collect { data ->
                Timber.i(
                    "getAllRecords.data: $data"
                )
                emit(Resource.Success(data))
            }
        }.catch { e ->
            Timber.e(
                "getAllRecords.Error: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }
}