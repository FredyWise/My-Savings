package com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Repository.UserRepository
import com.fredy.mysavings.Feature.Domain.Util.Mappers.toCategoryMaps
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryMap
import com.fredy.mysavings.Util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetCategoryMapOrderedByName(
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Resource<List<CategoryMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = userRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

            withContext(Dispatchers.IO) {
                categoryRepository.getUserCategories(userId)
            }.map { categories ->
                categories
                    .filter { it.categoryId != DefaultData.deletedCategory.categoryId + userId && it.categoryId != DefaultData.transferCategory.categoryId + userId }
                    .toCategoryMaps()
            }.collect { categories ->
                Log.i("getCategoryMapOrderedByName.Data: $categories")
                emit(Resource.Success(categories))
            }
        }.catch { e ->
            Log.e(
                "getCategoryMapOrderedByName.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}