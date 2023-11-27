package com.fredy.mysavings.DI

import android.content.Context
import android.util.Log
import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyApi
import com.fredy.mysavings.Data.GoogleAuth.GoogleAuthUiClient
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
import com.fredy.mysavings.Util.CURRENCY_CONVERTER_URL
import com.fredy.mysavings.Util.DispatcherProvider
import com.fredy.mysavings.Util.TAG
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
    @Singleton
    fun providesGoogleAuthUiClient(@ApplicationContext appContext: Context): GoogleAuthUiClient {
        return GoogleAuthUiClient(
            context = appContext,
            oneTapClient = Identity.getSignInClient(
                appContext
            )
        )
    }

    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesAuthRepositoryImpl(
        googleAuthUiClient: GoogleAuthUiClient,
        firebaseAuth: FirebaseAuth
    ): AuthRepository {
        return AuthRepositoryImpl(
            googleAuthUiClient, firebaseAuth
        )
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideCurrencyApi(
        okHttpClient: OkHttpClient,
    ): CurrencyApi {
        return Retrofit.Builder()
            .baseUrl(CURRENCY_CONVERTER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(CurrencyApi::class.java)
    }
//    @Provides
//    @Singleton
//    fun currencyApi(): CurrencyApi {
//        return Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
//            GsonConverterFactory.create()
//        ).build().create(CurrencyApi::class.java)
//    }

    @Provides
    @Singleton
    fun currencyRepository(currencyApi: CurrencyApi): CurrencyRepository {
        return CurrencyRepositoryImpl(currencyApi)
    }

//    @Provides
//    @Singleton
//    fun provideDispatchers(): DispatcherProvider {
//        return object: DispatcherProvider {
//            override val main: CoroutineDispatcher
//                get() = Dispatchers.Main
//            override val io: CoroutineDispatcher
//                get() = Dispatchers.IO
//            override val default: CoroutineDispatcher
//                get() = Dispatchers.Default
//            override val unconfined: CoroutineDispatcher
//                get() = Dispatchers.Unconfined
//        }
//    }


    @Provides
    @Singleton
    fun recordRepository(
        firebaseAuth: FirebaseAuth,
        googleAuthUiClient: GoogleAuthUiClient
    ): RecordRepository {
        Log.e(TAG, "recordRepository: ")
        return RecordRepositoryImpl(
            firebaseAuth, googleAuthUiClient
        )
    }

    @Provides
    @Singleton
    fun accountRepository(
        firebaseAuth: FirebaseAuth,
        googleAuthUiClient: GoogleAuthUiClient
    ): AccountRepository {
        Log.e(TAG, "accountRepository: ")
        return AccountRepositoryImpl(
            firebaseAuth, googleAuthUiClient
        )
    }

    @Provides
    @Singleton
    fun categoryRepository(
        firebaseAuth: FirebaseAuth,
        googleAuthUiClient: GoogleAuthUiClient
    ): CategoryRepository {
        Log.e(TAG, "categoryRepository: ")
        return CategoryRepositoryImpl(
            firebaseAuth, googleAuthUiClient
        )
    }

    @Provides
    @Singleton
    fun userRepository(
        firebaseAuth: FirebaseAuth,
        googleAuthUiClient: GoogleAuthUiClient
    ): UserRepository {
        Log.e(TAG, "userRepository: ")
        return UserRepositoryImpl(
            firebaseAuth, googleAuthUiClient
        )
    }

}