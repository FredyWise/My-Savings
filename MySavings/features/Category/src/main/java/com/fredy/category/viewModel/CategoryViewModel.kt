package com.fredy.category.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.category.domain.useCases.CategoryUseCases
import com.fredy.domain.enums.SortType
import com.fredy.domain.model.Category
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.util.resource.ResourceError
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
) : ViewModel() {

    private val _sortType = MutableStateFlow(
        SortType.ASCENDING
    )

    private val _state = MutableStateFlow(
        CategoryState()
    )

    private val _categoryMapResource = categoryUseCases.getCategoryMapOrderedByName().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _records = _state.flatMapLatest {
        categoryUseCases.getUserCategoryRecordsOrderedByDateTime(
            it.category.categoryId,
            _sortType.value
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val categoryMapResource = _state.onEach {
        _state.update {
            it.copy(
                isSearching = true
            )
        }
    }.combine(_categoryMapResource) { state, categoryMapResource ->
        if (state.searchQuery.isBlank()) {
            categoryMapResource
        } else {
            if (categoryMapResource is Resource.Success) {
                categoryMapResource.copy(
                    data = categoryMapResource.data.map { categoryMap ->
                        categoryMap.copy(categories = categoryMap.categories.filter {
                            it.doesMatchSearchQuery(state.searchQuery)
                        })
                    }
                )
            } else {
                categoryMapResource
            }
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

    private val categoryResource = _state.combine(_categoryMapResource) { state, categoryMapResource ->
        when (categoryMapResource) {
            is Resource.Error -> Resource.Error(categoryMapResource.error)
            is Resource.Loading -> Resource.Loading()
            is Resource.Success -> {
                Resource.Success<List<Category>,DataError.Local>(
                    categoryMapResource.data.flatMap { categoryMap -> categoryMap.categories }
                )
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    val state = combine(
        _state, _sortType, categoryMapResource,categoryResource, _records
    ) { state, sortType, categoryMapResource,categoryResource, records ->
        state.copy(
            categoryMapsResource = categoryMapResource,
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
                    categoryUseCases.updateRecordItemWithDeletedCategory(event.category)
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

