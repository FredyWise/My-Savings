package com.fredy.currency.data.mappers

import com.fredy.data.database.dto.RatesCache as DataRatesCache
import com.fredy.domain.model.RatesCache as DomainRatesCache

fun DomainRatesCache.toDataRatesCache(): DataRatesCache {
    return DataRatesCache(
        cacheId,
        base,
        date,
        rates,
        success,
        timestamp,
        cachedTime,
    )
}

fun DataRatesCache.toDomainRatesCache(): DomainRatesCache {
    return DomainRatesCache(
        cacheId,
        base,
        date,
        rates,
        success,
        timestamp,
        cachedTime,
    )
}