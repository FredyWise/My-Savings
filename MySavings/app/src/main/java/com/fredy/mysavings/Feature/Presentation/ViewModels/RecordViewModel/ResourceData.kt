package com.fredy.mysavings.Feature.Presentation.ViewModels.RecordViewModel

import com.fredy.mysavings.Feature.Domain.Model.AccountWithAmountType
import com.fredy.mysavings.Feature.Domain.Model.BookMap
import com.fredy.mysavings.Feature.Domain.Model.CategoryWithAmount
import com.fredy.mysavings.Feature.Domain.Model.Record
import com.fredy.mysavings.Feature.Domain.Util.Resource

data class ResourceData(
    val categoriesWithAmountResource: Resource<List<CategoryWithAmount>> = Resource.Loading(),
    val accountsWithAmountResource: Resource<List<AccountWithAmountType>> = Resource.Loading(),
    val recordsWithinTimeResource: Resource<List<Record>> = Resource.Loading(),
    val recordMapsResource: Resource<List<BookMap>> = Resource.Loading(),
)