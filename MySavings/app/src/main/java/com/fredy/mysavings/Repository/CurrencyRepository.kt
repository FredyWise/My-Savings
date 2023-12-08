package com.fredy.mysavings.Repository

import android.util.Log
import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyApi
import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyResponse
import com.fredy.mysavings.Util.Resource
import com.fredy.mysavings.Util.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface CurrencyRepository {
     fun getRates(base: String): Flow<Resource<CurrencyResponse>>
}

class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi
) : CurrencyRepository {
    override fun getRates(base: String): Flow<Resource<CurrencyResponse>> {
        return flow {
            emit(Resource.Loading())
            val response = api.getRates(base)
            Log.e(
                TAG,
                "convert: test success"+response,

                )
            val result = response.body()
            Log.e(
                TAG,
                "convert: test success"+result,

                )
            emit(Resource.Success(result!!))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }
}


