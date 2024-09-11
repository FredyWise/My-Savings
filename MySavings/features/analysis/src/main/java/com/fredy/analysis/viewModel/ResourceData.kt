package com.fredy.analysis.viewModel

import com.fredy.core.util.resource.DataError
import com.fredy.core.util.resource.Resource
import com.fredy.domain.model.AccountWithAmountType
import com.fredy.domain.model.BookMap
import com.fredy.domain.model.CategoryWithAmount
import com.fredy.domain.model.Record

data class ResourceData(
    val categoriesWithAmountResource: Resource<List<CategoryWithAmount>, DataError.Local> = Resource.Loading(),
    val accountsWithAmountResource: Resource<List<AccountWithAmountType>, DataError.Local> = Resource.Loading(),
    val recordsWithinTimeResource: Resource<List<Record>, DataError.Local> = Resource.Loading(),
    val recordMapsResource: Resource<List<BookMap>, DataError.Local> = Resource.Loading(),
)