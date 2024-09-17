package com.fredy.domain.useCases.CurrencyUseCases

import com.fredy.domain.model.Currency
import com.fredy.domain.repository.CurrencyRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.util.isCacheValid
import com.fredy.domain.util.resource.DataError
import com.fredy.domain.util.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber

class GetCurrencies(
    private val currencyRepository: CurrencyRepository,
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<Resource<List<Currency>, DataError.Local>> {
        return flow<Resource<List<Currency>, DataError.Local>> {
            emit(Resource.Loading())
            Timber.i("getCurrencies: start")
            val currentUser = userRepository.getCurrentUser()
            currentUser?.let {
                val userId = currentUser.firebaseUserId
                val cachedRates =
                    currencyRepository.getRateResponse()!!
                withContext(Dispatchers.IO) {
                    currencyRepository.getCachedCurrencies(userId)
                }.collect { cachedData ->
                    Timber.i("getCurrencies: $cachedData")

                    val result =
                        if (cachedData.isNotEmpty() && isCacheValid(cachedRates.cachedTime)) {
                            cachedData
                        } else {
                            val newCurrencies =
                                currencyRepository.getNewCurrencies(cachedRates.rates, userId)
                            newCurrencies?.let {
                                currencyRepository.updateCurrencies(newCurrencies)
                            }
                            newCurrencies
                        }?.sortedBy { it.name }!!
                    Timber.i("getCurrencies: $result")
                    emit(Resource.Success(result))
                }
            }
        }.catch { e ->
            Timber.e(
                "Failed to get currencies: $e"
            )
            emit(Resource.Error(DataError.Local.UNKNOWN))
        }
    }


}