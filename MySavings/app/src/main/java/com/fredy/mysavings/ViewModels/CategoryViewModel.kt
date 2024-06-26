package com.fredy.mysavings.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.RoomDatabase.Entity.Category
import com.fredy.mysavings.Data.RoomDatabase.Enum.RecordType
import com.fredy.mysavings.Data.RoomDatabase.Enum.SortType
import com.fredy.mysavings.ViewModels.Event.CategoryEvent
import com.fredy.mysavings.R
import com.fredy.mysavings.Repository.CategoryRepositoryImpl
import com.fredy.mysavings.DI.AppModuleImpl
import com.fredy.mysavings.Repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
): ViewModel() {
    private val _sortType = MutableStateFlow(
        SortType.ASCENDING
    )

    private val _categories = categoryRepository.getUserCategoriesOrderedByName().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _state = MutableStateFlow(
        CategoryState()
    )

    val state = combine(
        _state, _sortType, _categories,
    ) { state, sortType, categories ->
        state.copy(categories = categories.groupBy {
            it.categoryType
        }.toSortedMap().map {
            CategoryMap(
                categoryType = it.key,
                categories = it.value
            )
        }, sortType = sortType
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CategoryState()
    )


    fun onEvent(event: CategoryEvent) {
        when (event) {
            is CategoryEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        categoryId = event.category.categoryId,
                        categoryName = event.category.categoryName,
                        categoryType = event.category.categoryType,
                        categoryIconDescription = event.category.categoryIconDescription,
                        categoryIcon = event.category.categoryIcon,
                        isAddingCategory = true
                    )
                }
            }

            is CategoryEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingCategory = false
                    )
                }
            }

            is CategoryEvent.DeleteCategory -> {
                viewModelScope.launch {
                    categoryRepository.deleteCategory(
                        event.category
                    )
                }
            }

            is CategoryEvent.SaveCategory -> {
                val categoryId = state.value.categoryId
                val categoryName = state.value.categoryName
                val categoryType = state.value.categoryType
                val categoryIcon = state.value.categoryIcon
                val categoryIconDescription = state.value.categoryIconDescription

                if (categoryName.isBlank() || categoryIcon == 0 || categoryIconDescription.isBlank()) {
                    return
                }

                val category = Category(
                    categoryId = categoryId,
                    categoryName = categoryName,
                    categoryType = categoryType,
                    categoryIconDescription = categoryIconDescription,
                    categoryIcon = categoryIcon,
                )
                viewModelScope.launch {
                    categoryRepository.upsertCategory(
                        Category(
                            categoryId = 1,
                            categoryName = RecordType.Transfer.name,
                            categoryType = RecordType.Transfer,
                            categoryIconDescription = RecordType.Transfer.name,
                            categoryIcon = R.drawable.ic_exchange,
                        )
                    )
                    categoryRepository.upsertCategory(
                        category
                    )
                }
                _state.update { CategoryState() }
            }

            is CategoryEvent.CategoryName -> {
                _state.update {
                    it.copy(
                        categoryName = event.categoryName
                    )
                }
            }

            is CategoryEvent.CategoryTypes -> {
                _state.update {
                    it.copy(
                        categoryType = event.categoryType
                    )
                }
            }

            is CategoryEvent.CategoryIcon -> {
                _state.update {
                    it.copy(
                        categoryIcon = event.icon,
                        categoryIconDescription = event.iconDescription
                    )
                }
            }


            is CategoryEvent.SortCategory -> {
                _sortType.value = event.sortType
            }
        }
    }
}

data class CategoryState(
    val categories: List<CategoryMap> = listOf(),
    val categoryId: Int = 0,
    val categoryName: String = "",
    val categoryType: RecordType = RecordType.Expense,
    val categoryIcon: Int = 0,
    val categoryIconDescription: String = "",
    val isAddingCategory: Boolean = false,
    val sortType: SortType = SortType.ASCENDING
)

data class CategoryMap(
    val categoryType: RecordType = RecordType.Expense,
    val categories: List<Category> = emptyList()
)