package com.fredy.mysavings.DI


import android.content.Context

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.fredy.mysavings.Data.Database.Model.UserData
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor().setLevel(
            HttpLoggingInterceptor.Level.BODY
        )
    ).readTimeout(
        15,
        TimeUnit.SECONDS
    ).connectTimeout(15, TimeUnit.SECONDS).build()

    @Provides
    @Singleton
    fun providesSignInClient(@ApplicationContext appContext: Context): SignInClient =
        Identity.getSignInClient(
            appContext
        )

    @Provides
    fun providesCurrentUserData(firebaseAuth: FirebaseAuth): UserData? =
        firebaseAuth.currentUser?.run {
            UserData(
                firebaseUserId = uid,
                username = displayName,
                emailOrPhone = email,
                profilePictureUrl = photoUrl.toString()
            )
        }


}
