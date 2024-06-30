package com.fredy.mysavings.Feature.Presentation.ViewModels.SearchViewModel

import com.fredy.mysavings.Feature.Domain.Model.BookMap
import com.fredy.mysavings.Feature.Domain.Util.Resource

data class SearchState(
    val trueRecordsResource: Resource<List<BookMap>> = Resource.Loading(),
    val isSearching: Boolean = false,
    val searchQuery: String = "",
)