package com.fredy.mysavings.Feature.Domain.UseCases.AccountUseCases

import com.fredy.mysavings.BaseUseCaseTest
import com.fredy.mysavings.Feature.Data.Database.Model.Account
import com.fredy.mysavings.Feature.Data.Database.Model.Currency
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.ConvertCurrencyData
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.GetCurrencies
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.GetCurrencyRates
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.UpdateCurrency
import com.fredy.mysavings.Feature.Domain.UseCases.CurrencyUseCases.currencyConverter
import com.fredy.mysavings.Feature.Mappers.getRateForCurrency
import com.fredy.mysavings.Util.Resource
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class CurrencyUseCasesTest : BaseUseCaseTest() {

    private lateinit var updateCurrency: UpdateCurrency
    private lateinit var getCurrencyRates: GetCurrencyRates
    private lateinit var convertCurrencyData: ConvertCurrencyData
    private lateinit var getCurrencies: GetCurrencies

    @Before
    fun setUp() {
        updateCurrency = UpdateCurrency(fakeCurrencyRepository)
        getCurrencyRates = GetCurrencyRates(fakeCurrencyRepository)
        convertCurrencyData = ConvertCurrencyData(fakeCurrencyRepository)
        getCurrencies = GetCurrencies(fakeCurrencyRepository, fakeAuthRepository)
    }


    @Test
    fun `test update currency`() = runBlocking {
        val currency = Currency(
            currencyId = "1",
            code = "USD",
            userIdFk = currentUserId,
            name = "US Dollar",
            symbol = "$",
            value = 1.0
        )
        updateCurrency(currency)

        val updatedCurrency = fakeCurrencyRepository.getCurrencies(currentUserId).last()
        assertTrue(updatedCurrency.contains(currency))
    }
    @Test
    fun `test get currency rates`() = runBlocking {
        val ratesResource = getCurrencyRates().last()

        assertTrue(ratesResource is Resource.Success)
        val rates = (ratesResource as Resource.Success).data!!
        assertEquals(fakeCurrencyRepository.getRateResponse().rates, rates)
    }

    @Test
    fun `test convert currency data`() = runBlocking {
        val amount = 100.0
        val fromCurrency = "USD"
        val toCurrency = "EUR"

        val result = convertCurrencyData(amount, fromCurrency, toCurrency)

        val expectedAmount = amount * (fakeCurrencyRepository.getRateResponse().rates.getRateForCurrency(toCurrency)!!.toDouble() / fakeCurrencyRepository.getRateResponse().rates.getRateForCurrency(fromCurrency)!!.toDouble())
        assertEquals(expectedAmount, result.amount)
        assertEquals(toCurrency, result.currency)
    }

    @Test
    fun `test get currencies`() = runBlocking {
        val currenciesResource = getCurrencies().last()

        assertTrue(currenciesResource is Resource.Success)
        val currencies = (currenciesResource as Resource.Success).data!!
        assertTrue(fakeCurrencyRepository.getCurrencies(currentUserId).last().containsAll(currencies))
    }
}


