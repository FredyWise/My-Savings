package com.fredy.data.mappers

import com.fredy.data.database.dto.RatesCache as DataRatesCache
import com.fredy.domain.model.RatesCache as DomainRatesCache

fun DomainRatesCache.toDataRatesCache(): DataRatesCache {
    return DataRatesCache(
        cacheId,
        base,
        date,
        rates.toRates(),
        success,
        timestamp,
        cachedTime,
    )
}
fun DataRatesCache.toDataRatesCache(): DomainRatesCache {
    return DomainRatesCache(
        cacheId,
        base,
        date,
        rates.toList(),
        success,
        timestamp,
        cachedTime,
    )
}