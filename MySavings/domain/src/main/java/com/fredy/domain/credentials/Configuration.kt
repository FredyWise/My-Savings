package com.fredy.domain.credentials

import com.fredy.domain.BuildConfig

sealed class Configuration {

    object FirebaseModel {
        const val USER_ENTITY = "user"
        const val RECORD_ENTITY = "record"
        const val WALLET_ENTITY = "wallet"
        const val CATEGORY_ENTITY = "category"
        const val CURRENCY_ENTITY = "currency"
    }

    object WebClient {
        val ID = BuildConfig.WEB_CLIENT_ID
    }

    object AppData {
        val AppVersion = BuildConfig.VERSION_NAME
        val AppName = BuildConfig.APP_NAME
    }

    object Debug {
        val DEBUG = BuildConfig.DEBUG
    }

    object UIConfig {
        const val MessageLimit = 20
    }

}
