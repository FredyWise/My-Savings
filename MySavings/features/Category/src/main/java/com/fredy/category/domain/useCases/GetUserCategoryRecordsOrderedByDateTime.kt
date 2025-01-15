package com.fredy.category.domain.useCases

import com.fredy.category.domain.CategoryRepository
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.enums.SortType
import com.fredy.domain.mappers.recordUIMapper.toRecordSortedMaps
import com.fredy.domain.model.RecordMap
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class GetUserCategoryRecordsOrderedByDateTime(
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(
        categoryId: String,
        sortType: SortType
    ): Flow<Resource<List<RecordMap>, DataError.Local>> {
        return flow<Resource<List<RecordMap>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val userId = currentUser?.firebaseUserId ?: ""
            Timber.i(
                "getUserCategoryRecordsOrderedByDateTime: $categoryId",

                )

            categoryRepository.getUserCategoryRecordsOrderedByDateTime(
                userId, categoryId, sortType
            ).collect { data ->
                Timber.i(
                    "getUserCategoryRecordsOrderedByDateTime.Data: $data"
                )
                emit(Resource.Success(data.toRecordSortedMaps()))
            }
        }.catch { e ->
            Timber.e(
                "getUserCategoryRecordsOrderedByDateTime.Error: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }
}