package com.fredy.mysavings.Feature.Presentation.ViewModels.CategoryViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Feature.Data.Enum.SortType
import com.fredy.mysavings.Feature.Domain.Model.Category
import com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases.CategoryUseCases
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Feature.Domain.Util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryUseCases: CategoryUseCases,
    private val recordUseCases: RecordUseCases
): ViewModel() {

    private val _sortType = MutableStateFlow(
        SortType.ASCENDING
    )

    private val _state = MutableStateFlow(
        CategoryState()
    )

    private val _categoryResource = categoryUseCases.getCategoryMapOrderedByName().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _records = _state.flatMapLatest {
        recordUseCases.getUserCategoryRecordsOrderedByDateTime(
            it.category.categoryId,
            _sortType.value
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val categoryResource = _state.onEach {
        _state.update {
            it.copy(
                isSearching = true
            )
        }
    }.combine(_categoryResource) { state, categoryResource ->
        if (state.searchQuery.isBlank()) {
            categoryResource
        } else {
            Resource.Success(categoryResource.data!!.map { categoryMap ->
                categoryMap.copy(categories = categoryMap.categories.filter {
                    it.doesMatchSearchQuery(state.searchQuery)
                })
            })
        }
    }.onEach {
        _state.update {
            it.copy(
                isSearching = false
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    val state = combine(
        _state, _sortType, categoryResource, _records
    ) { state, sortType, categoryResource, records ->
        state.copy(
            categoryResource = categoryResource,
            recordMapsResource = records,
            sortType = sortType,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
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
                    categoryUseCases.deleteCategory(
                        event.category
                    )
                    recordUseCases.updateRecordItemWithDeletedCategory(event.category)
                    event.onDeleteEffect()
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
                    categoryUseCases.upsertCategory(
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

            is CategoryEvent.GetCategoryDetail -> {
                _state.update {
                    it.copy(
                        category = event.category
                    )
                }
            }

            is CategoryEvent.SearchCategory -> {
                _state.update {
                    it.copy(
                        searchQuery = event.searchQuery
                    )
                }
            }

            is CategoryEvent.SortCategory -> {
                _sortType.value = event.sortType
            }

        }
    }
}
