package com.fredy.mysavings.DI

import android.content.Context
import android.util.Log
import com.fredy.mysavings.Data.APIs.ApiCredentials
import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyApi
import com.fredy.mysavings.Data.Database.Entity.UserData
import com.fredy.mysavings.Repository.AccountRepository
import com.fredy.mysavings.Repository.AccountRepositoryImpl
import com.fredy.mysavings.Repository.AuthRepository
import com.fredy.mysavings.Repository.AuthRepositoryImpl
import com.fredy.mysavings.Repository.CategoryRepository
import com.fredy.mysavings.Repository.CategoryRepositoryImpl
import com.fredy.mysavings.Repository.CurrencyRepository
import com.fredy.mysavings.Repository.CurrencyRepositoryImpl
import com.fredy.mysavings.Repository.RecordRepository
import com.fredy.mysavings.Repository.RecordRepositoryImpl
import com.fredy.mysavings.Repository.UserRepository
import com.fredy.mysavings.Repository.UserRepositoryImpl
import com.fredy.mysavings.Util.TAG
import com.fredy.mytest.APIs.TextCorrectionModule.TypeWiseApi
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


//interface AppModule {
//    val currencyApi: CurrencyApi
//    val currencyRepository: CurrencyRepository
//    val provideDispatchers: DispatcherProvider
//    val recordRepository: RecordRepository
//    val accountRepository: AccountRepository
//    val categoryRepository: CategoryRepository
//    val savingsDatabase: SavingsDatabase
//    fun provideAppContext(appContext: Context)
//}
@Module
@InstallIn(SingletonComponent::class)
object AppModuleImpl/*: AppModule*/ {
    @Provides
    fun providesCurrentUserData(auth: FirebaseAuth): UserData? = auth.currentUser?.run {
        UserData(
            firebaseUserId = uid,
            username = displayName,
            email = email,
            profilePictureUrl = photoUrl.toString()
        )
    }


    @Provides
    @Singleton
    fun providesSignInClient(@ApplicationContext appContext: Context): SignInClient {
        return Identity.getSignInClient(
            appContext
        )
    }

    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesAuthRepositoryImpl(
        oneTapClient: SignInClient,
        firebaseAuth: FirebaseAuth
    ): AuthRepository {
        return AuthRepositoryImpl(
            oneTapClient, firebaseAuth
        )
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor().setLevel(
                HttpLoggingInterceptor.Level.BODY
            )
        ).readTimeout(
            15, TimeUnit.SECONDS
        ).connectTimeout(
            15, TimeUnit.SECONDS
        ).build()
    }

    @Singleton
    @Provides
    fun provideCurrencyApi(
        okHttpClient: OkHttpClient,
    ): CurrencyApi {
        return Retrofit.Builder().baseUrl(
            ApiCredentials.CurrencyModels.BASE_URL
        ).addConverterFactory(
            GsonConverterFactory.create()
        ).client(okHttpClient).build().create(
            CurrencyApi::class.java
        )
    }

    @Singleton
    @Provides
    fun provideTextCorrectionApi(
        okHttpClient: OkHttpClient,
    ): TypeWiseApi {
        return Retrofit.Builder().baseUrl(
            ApiCredentials.TextCorrectionModule.BASE_URL
        ).addConverterFactory(
            GsonConverterFactory.create()
        ).client(okHttpClient).build().create(
            TypeWiseApi::class.java
        )
    }


    @Provides
    @Singleton
    fun currencyRepository(currencyApi: CurrencyApi): CurrencyRepository {
        return CurrencyRepositoryImpl(currencyApi)
    }

//    @Provides
//    @Singleton
//    fun textCorrectionRepository(textCorrectionApi: TypeWiseApi): CurrencyRepository {
//        return TextCorrectionRepositoryImpl(textCorrectionApi)
//    }


    @Provides
    @Singleton
    fun recordRepository(): RecordRepository {
        Log.e(TAG, "recordRepository: ")
        return RecordRepositoryImpl()
    }

    @Provides
    @Singleton
    fun accountRepository(): AccountRepository {
        Log.e(TAG, "accountRepository: ")
        return AccountRepositoryImpl()
    }

    @Provides
    @Singleton
    fun categoryRepository(
    ): CategoryRepository {
        Log.e(TAG, "categoryRepository: ")
        return CategoryRepositoryImpl()
    }

    @Provides
    @Singleton
    fun userRepository(
    ): UserRepository {
        Log.e(TAG, "userRepository: ")
        return UserRepositoryImpl()
    }

}