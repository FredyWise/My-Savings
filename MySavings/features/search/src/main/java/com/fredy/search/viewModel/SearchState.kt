package com.fredy.search.viewModel

import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.model.BookMap

data class SearchState(
    val trueRecordsResource: Resource<List<BookMap>, DataError.Local> = Resource.Loading(),
    val isSearching: Boolean = false,
    val searchQuery: String = "",
)