package com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases

import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Feature.Domain.Util.Resource
import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel.CategoryMap
import com.fredy.mysavings.Feature.Presentation.Util.DefaultData
import com.fredy.mysavings.Util.Log
import com.fredy.mysavings.Feature.Domain.Util.Mappers.toCategoryMaps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetCategoryMapOrderedByName(
    private val categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Resource<List<CategoryMap>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getCurrentUser()!!
            val userId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""

            withContext(Dispatchers.IO) {
                categoryRepository.getUserCategories(userId)
            }.map { categories -> categories.filter { it.categoryId != DefaultData.deletedCategory.categoryId + userId || it.categoryId != DefaultData.transferCategory.categoryId + userId } }
                .collect { categories ->
                    val data = categories.toCategoryMaps()
                    Log.i("getCategoryMapOrderedByName.Data: $data")
                    emit(Resource.Success(data))
                }
        }.catch { e ->
            Log.e(
                "getCategoryMapOrderedByName.Error: $e"
            )
            emit(Resource.Error(e.message.toString()))
        }
    }
}