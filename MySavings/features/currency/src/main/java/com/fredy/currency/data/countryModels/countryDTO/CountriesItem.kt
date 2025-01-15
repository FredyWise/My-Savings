package com.fredy.currency.data.countryModels.countryDTO

import com.fredy.currency.data.countryModels.additionalDTO.CapitalInfo
import com.fredy.currency.data.countryModels.additionalDTO.CoatOfArms
import com.fredy.currency.data.countryModels.additionalDTO.Demonyms
import com.fredy.currency.data.countryModels.additionalDTO.Languages
import com.fredy.currency.data.countryModels.additionalDTO.Name
import com.fredy.currency.data.countryModels.additionalDTO.PostalCode
import com.fredy.currency.data.countryModels.additionalDTO.Translations

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