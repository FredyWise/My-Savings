package com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response

import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.AdditionalData.CapitalInfo
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.AdditionalData.CoatOfArms
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.AdditionalData.Demonyms
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.AdditionalData.Languages
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.AdditionalData.Name
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.AdditionalData.PostalCode
import com.fredy.mysavings.Feature.Data.APIs.CountryModels.Response.AdditionalData.Translations

data class CountriesItem(
    val altSpellings: List<String>,
    val area: Double,
    val borders: List<String>,
    val capital: List<String>,
    val capitalInfo: CapitalInfo,
    val car: TranslationHelper,
    val cca2: String,
    val cca3: String,
    val ccn3: String,
    val cioc: String,
    val coatOfArms: CoatOfArms,
    val continents: List<String>,
    val currencies: Currencies,
    val demonyms: Demonyms,
    val fifa: String,
    val flag: String,
    val flags: Flags,
    val gini: TranslationHelper,
    val idd: TranslationHelper,
    val independent: Boolean,
    val landlocked: Boolean,
    val languages: Languages,
    val latlng: List<Double>,
    val maps: TranslationHelper,
    val name: Name,
    val population: Int,
    val postalCode: PostalCode,
    val region: String,
    val startOfWeek: String,
    val status: String,
    val subregion: String,
    val timezones: List<String>,
    val tld: List<String>,
    val translations: Translations,
    val unMember: Boolean
)