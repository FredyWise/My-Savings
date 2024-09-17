package com.fredy.domain.credentials

import com.fredy.domain.BuildConfig

sealed class Configuration {

    object FirebaseModel {
        const val USER_ENTITY = "user"

    }

    object WebClient {
         val ID = BuildConfig.WEB_CLIENT_ID
    }

    object AppData {
         val AppVersion = BuildConfig.VERSION_NAME
    }

    object Debug {
        const val DEBUG = true
    }

    object UIConfig {
        const val MessageLimit = 20
    }

}
