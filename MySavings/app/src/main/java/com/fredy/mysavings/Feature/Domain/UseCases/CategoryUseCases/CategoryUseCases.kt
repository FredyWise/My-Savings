package com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases

import com.fredy.mysavings.Util.Log
import co.yml.charts.common.extensions.isNotNull
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.Repository.AuthRepository
import com.fredy.mysavings.Feature.Domain.Repository.CategoryRepository
import com.fredy.mysavings.Util.Mappers.toCategoryMaps
import com.fredy.mysavings.Util.DefaultData.deletedCategory
import com.fredy.mysavings.Util.DefaultData.transferCategory
import com.fredy.mysavings.Util.Resource

import com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

data class CategoryUseCases(
    val upsertCategory: UpsertCategory,
    val deleteCategory: DeleteCategory,
    val getCategory: GetCategory,
    val getCategoryMapOrderedByName: GetCategoryMapOrderedByName
)

class UpsertCategory(
    private val repository: CategoryRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(category: Category): String {
        val currentUser = authRepository.getCurrentUser()!!
        val currentUserId = if (currentUser.isNotNull()) currentUser.firebaseUserId else ""
        return repository.upsertCategory(category.copy(userIdFk = currentUserId))
    }
}

class DeleteCategory(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        repository.deleteCategory(category)
    }
}

class GetCategory(
    private val repository: CategoryRepository
) {
    operator fun invoke(categoryId: String): Flow<Category> {
        return repository.getCategory(categoryId)
    }
}


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
            }.map { categories -> categories.filter { it.categoryId != deletedCategory.categoryId + userId || it.categoryId != transferCategory.categoryId + userId } }.collect { categories ->
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
