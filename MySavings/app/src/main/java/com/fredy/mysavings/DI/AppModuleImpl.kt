package com.fredy.mysavings.DI

import android.content.Context
import com.fredy.mysavings.Data.APIs.ApiCredentials
import com.fredy.mysavings.Data.APIs.CurrencyModels.CurrencyApi
import com.fredy.mysavings.Data.Database.Entity.UserData
import com.fredy.mysavings.Data.Repository.AccountRepository
import com.fredy.mysavings.Data.Repository.AccountRepositoryImpl
import com.fredy.mysavings.Data.Repository.AuthRepository
import com.fredy.mysavings.Data.Repository.AuthRepositoryImpl
import com.fredy.mysavings.Data.Repository.CategoryRepository
import com.fredy.mysavings.Data.Repository.CategoryRepositoryImpl
import com.fredy.mysavings.Data.Repository.CurrencyRepository
import com.fredy.mysavings.Data.Repository.CurrencyRepositoryImpl
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Data.Repository.RecordRepositoryImpl
import com.fredy.mysavings.Data.Repository.UserRepository
import com.fredy.mysavings.Data.Repository.UserRepositoryImpl
import com.fredy.mytest.APIs.TextCorrectionModule.TypeWiseApi
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModuleImpl {
    // Core dependencies

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
        15, TimeUnit.SECONDS
    ).connectTimeout(
        15, TimeUnit.SECONDS
    ).build()

    // API providers

//    @Provides
//    @Singleton
//    fun textCorrectionRepository(textCorrectionApi: TypeWiseApi): CurrencyRepository {
//        return TextCorrectionRepositoryImpl(textCorrectionApi)
//    }

    @Provides
    @Singleton
    fun provideCurrencyApi(okHttpClient: OkHttpClient): CurrencyApi = Retrofit.Builder().baseUrl(
        ApiCredentials.CurrencyModels.BASE_URL
    ).addConverterFactory(GsonConverterFactory.create()).client(
        okHttpClient
    ).build().create(CurrencyApi::class.java)

    @Provides
    @Singleton
    fun provideTextCorrectionApi(okHttpClient: OkHttpClient): TypeWiseApi = Retrofit.Builder().baseUrl(
        ApiCredentials.TextCorrectionModule.BASE_URL
    ).addConverterFactory(GsonConverterFactory.create()).client(
        okHttpClient
    ).build().create(TypeWiseApi::class.java)

    // Repositories (sorted based on dependencies)

    @Provides
    @Singleton
    fun provideAuthRepository(
        oneTapClient: SignInClient,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
    ): AuthRepository = AuthRepositoryImpl(
        oneTapClient, firestore, firebaseAuth
    )

    @Provides
    @Singleton
    fun provideCurrencyRepository(
        currencyApi: CurrencyApi,
        firestore: FirebaseFirestore
    ): CurrencyRepository = CurrencyRepositoryImpl(
        currencyApi, firestore
    )

    @Provides
    @Singleton
    fun provideRecordRepository(
        currencyRepository: CurrencyRepository,
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): RecordRepository = RecordRepositoryImpl(
        currencyRepository,
        firestore,
        firebaseAuth
    )

    @Provides
    @Singleton
    fun provideAccountRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): AccountRepository = AccountRepositoryImpl(
        firestore, firebaseAuth
    )

    @Provides
    @Singleton
    fun provideCategoryRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): CategoryRepository = CategoryRepositoryImpl(
        firestore, firebaseAuth
    )

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ): UserRepository = UserRepositoryImpl(
        firestore, firebaseAuth,
    )

    // Additional utility providers

    @Provides
    fun providesSignInClient(@ApplicationContext appContext: Context): SignInClient = Identity.getSignInClient(
        appContext
    )

    @Provides
    fun providesCurrentUserData(firebaseAuth: FirebaseAuth): UserData? = firebaseAuth.currentUser?.run {
        UserData(
            firebaseUserId = uid,
            username = displayName,
            emailOrPhone = email,
            profilePictureUrl = photoUrl.toString()
        )
    }
}
