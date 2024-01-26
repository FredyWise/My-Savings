package com.fredy.mysavings.DI

import com.fredy.mysavings.Data.Repository.AccountRepository
import com.fredy.mysavings.Data.Repository.AuthRepository
import com.fredy.mysavings.Data.Repository.CategoryRepository
import com.fredy.mysavings.Data.Repository.CurrencyRepository
import com.fredy.mysavings.Data.Repository.RecordRepository
import com.fredy.mysavings.Data.Repository.UserRepository
import com.fredy.mysavings.MySavingsApp
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        ApiModule::class,
        DataModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent {

    fun inject(app: MySavingsApp)
//    fun provideAuthRepository(): AuthRepository
    fun provideCurrencyRepository(): CurrencyRepository
    fun provideRecordRepository(): RecordRepository
    fun provideAccountRepository(): AccountRepository
    fun provideCategoryRepository(): CategoryRepository
    fun provideUserRepository(): UserRepository


}