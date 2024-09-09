package com.fredy.mysavings.Feature.Presentation.ViewModels.SearchViewModel

import com.fredy.domain.model.BookMap

data class SearchState(
    val trueRecordsResource: Resource<List<BookMap>> = Resource.Loading(),
    val isSearching: Boolean = false,
    val searchQuery: String = "",
)