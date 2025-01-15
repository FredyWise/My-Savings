package com.fredy.book.viewModel

import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import com.fredy.domain.model.AccountWithAmountType
import com.fredy.domain.model.BookMap
import com.fredy.domain.model.CategoryWithAmount
import com.fredy.domain.model.Record

data class ResourceData(

    val recordMapsResource: Resource<List<BookMap>, DataError.Local> = Resource.Loading(),

    )
