package com.fredy.mysavings.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredy.mysavings.Feature.Data.Database.Model.BookMap
import com.fredy.mysavings.Feature.Data.Database.Model.RecordMap
import com.fredy.mysavings.Feature.Domain.UseCases.RecordUseCases.RecordUseCases
import com.fredy.mysavings.Util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val recordUseCases: RecordUseCases,
) : ViewModel() {

    private val _trueRecordsResource = recordUseCases.getAllRecords().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Resource.Success(emptyList())
    )

    private val _state = MutableStateFlow(
        SearchState()
    )

    private val trueRecordsResource = _state.onEach {
        _state.update {
            it.copy(
                isSearching = true
            )
        }
    }.combine(_trueRecordsResource) { state, trueRecordsResource ->
        when (trueRecordsResource) {
            is Resource.Success -> {
                val searchQuery = state.searchQuery
                if (searchQuery.isBlank()) {
                    Resource.Success(trueRecordsResource.data!!.filter { it.recordMaps.isNotEmpty() })
                } else {
                    Resource.Success(trueRecordsResource.data!!.filter {
                        it.doesMatchSearchQuery(searchQuery)
                    }.filter { it.recordMaps.isNotEmpty() })
                }
            }

            is Resource.Error -> Resource.Error(trueRecordsResource.message.toString())
            is Resource.Loading -> Resource.Loading()
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
        _state,
        trueRecordsResource,
    ) { state, trueRecordsResource ->
        state.copy(
            trueRecordsResource = trueRecordsResource,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        SearchState()
    )


    fun onSearch(searchQuery: String) {
        _state.update {
            it.copy(searchQuery = searchQuery)
        }
    }


}

data class SearchState(
    val trueRecordsResource: Resource<List<BookMap>> = Resource.Loading(),
    val isSearching: Boolean = false,
    val searchQuery: String = "",
)
