package com.fredy.book.viewModel

import com.fredy.domain.model.AccountWithAmountType
import com.fredy.domain.model.BookMap
import com.fredy.domain.model.CategoryWithAmount
import com.fredy.domain.model.Record

data class ResourceData(
    val categoriesWithAmountResource: Resource<List<CategoryWithAmount>> = Resource.Loading(),
    val accountsWithAmountResource: Resource<List<AccountWithAmountType>> = Resource.Loading(),
    val recordsWithinTimeResource: Resource<List<Record>> = Resource.Loading(),
    val recordMapsResource: Resource<List<BookMap>> = Resource.Loading(),
)