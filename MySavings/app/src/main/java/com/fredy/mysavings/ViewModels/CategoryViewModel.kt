package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Data.Database.Model.Category
import com.fredy.mysavings.Data.Enum.RecordType
import com.fredy.mysavings.Data.Enum.SortType
import com.fredy.mysavings.Data.Repository.CategoryRepository
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.transferIcon
import com.fredy.mysavings.ViewModels.Event.CategoryEvent
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
    private val categoryRepository: CategoryRepository,
    private val recordRepository: RecordRepository
): ViewModel() {

    private val _sortType = MutableStateFlow(
        SortType.ASCENDING
    )

    private val _categoryMap = categoryRepository.getCategoryMapOrderedByName().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _state = MutableStateFlow(
        CategoryState()
    )

    private val _records = _state.flatMapLatest {
        recordRepository.getUserCategoryRecordsOrderedByDateTime(
            it.category.categoryId,
            _sortType.value
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val categoryMaps = _state.onEach {
        _state.update {
            it.copy(
                isSearching = true
            )
        }
    }.combine(_categoryMap) { state, categoryMaps ->
        if (state.searchQuery.isBlank()) {
            categoryMaps
        } else {
            categoryMaps.map { categoryMap ->
                categoryMap.copy(categories = categoryMap.categories.filter {
                    it.doesMatchSearchQuery(state.searchQuery)
                })
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
        emptyList()
    )

    val state = combine(
        _state, _sortType, categoryMaps, _records
    ) { state, sortType, categoryMaps, records ->
        state.copy(
            categoryMaps = categoryMaps,
            recordMapsResource = records,
            sortType = sortType,
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
                            categoryId = "1",
                            categoryName = RecordType.Transfer.name,
                            categoryType = RecordType.Transfer,
                            categoryIcon = transferIcon.image,
                            categoryIconDescription = transferIcon.description,
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

data class CategoryState(
    val categoryMaps: List<CategoryMap> = emptyList(),
    val recordMapsResource: Resource<List<RecordMap>> = Resource.Loading(),
    val category: Category = Category(),
    val categoryId: String = "",
    val categoryName: String = "",
    val categoryType: RecordType = RecordType.Expense,
    val categoryIcon: Int = 0,
    val categoryIconDescription: String = "",
    val isAddingCategory: Boolean = false,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val sortType: SortType = SortType.ASCENDING
)

data class CategoryMap(
    val categoryType: RecordType = RecordType.Expense,
    val categories: List<Category> = emptyList()
)