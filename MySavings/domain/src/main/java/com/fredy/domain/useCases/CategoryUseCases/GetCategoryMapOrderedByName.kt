package com.fredy.domain.useCases.CategoryUseCases

import com.fredy.core.util.resource.DataError
import com.fredy.core.util.resource.Resource
import com.fredy.domain.repository.CategoryRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.util.DefaultData
import com.fredy.mysavings.Feature.Domain.Util.Mappers.toCategoryMaps
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber

class GetCategoryMapOrderedByName(
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Resource<List<CategoryMap>, DataError.Local>> {
        return flow<Resource<List<CategoryMap>, DataError.Local>> {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val userId = currentUser?.firebaseUserId ?: ""

            withContext(Dispatchers.IO) {
                categoryRepository.getUserCategories(userId)
            }.map { categories ->
                categories
                    .filter { it.categoryId != DefaultData.deletedCategory.categoryId + userId && it.categoryId != DefaultData.transferCategory.categoryId + userId }
                    .toCategoryMaps()
            }.collect { categories ->
                Timber.i("getCategoryMapOrderedByName.Data: $categories")
                emit(Resource.Success(categories))
            }
        }.catch { e ->
            Timber.e(
                "getCategoryMapOrderedByName.Error: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }
}