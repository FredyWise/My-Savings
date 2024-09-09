package com.fredy.core.credentials


sealed class Configuration {

    object FirebaseModel{
        const val USER_ENTITY = "user"

    }

    object WebClient{
        const val ID = BuildConfig.WEB_CLIENT_ID
    }

    object AppData{
        const val AppVersion = BuildConfig.VERSION_NAME
    }

    object Debug{
        const val DEBUG = true
    }

    object UIConfig{
        const val MessageLimit = 20
    }

}
