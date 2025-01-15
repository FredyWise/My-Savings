package com.fredy.analysis.di

import com.fredy.analysis.data.AnalysisRepositoryImpl
import com.fredy.analysis.domain.AnalysisRepository
import com.fredy.analysis.domain.useCases.AnalysisUseCases
import com.fredy.analysis.domain.useCases.DeleteRecordItem
import com.fredy.analysis.domain.useCases.GetUserCategoriesWithAmountFromSpecificTime
import com.fredy.analysis.domain.useCases.GetUserRecordsFromSpecificTime
import com.fredy.analysis.domain.useCases.GetUserTotalAmountByTypeFromSpecificTime
import com.fredy.analysis.domain.useCases.GetUserTotalRecordBalance
import com.fredy.analysis.domain.useCases.GetUserWalletsWithAmountFromSpecificTime
import com.fredy.currency.domain.useCases.CurrencyUseCases
import com.fredy.domain.repository.CategoryRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.domain.repository.WalletRepository


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalysisModule {

    @Provides
    @Singleton
    fun provideAnalysisRepository(
        recordRepository: RecordRepository
//        recordDataSource: RecordDataSource,
//        recordDao: RecordDao,
    ): AnalysisRepository = AnalysisRepositoryImpl(
        recordRepository
//        recordDataSource,
//        recordDao,
    )

    @Provides
    @Singleton
    fun provideAnalysisUseCases(
        userRepository: UserRepository,
        analysisRepository: AnalysisRepository,
        walletRepository: WalletRepository,
        categoryRepository: CategoryRepository,
        currencyUseCases: CurrencyUseCases,
    ): AnalysisUseCases {
        val analysisUseCases = AnalysisUseCases(
            deleteRecordItem = DeleteRecordItem(analysisRepository),
            getUserRecordsFromSpecificTime = GetUserRecordsFromSpecificTime(
                analysisRepository,
                userRepository,
                currencyUseCases
            ),
            getUserCategoriesWithAmountFromSpecificTime = GetUserCategoriesWithAmountFromSpecificTime(
                analysisRepository, categoryRepository, userRepository, currencyUseCases
            ),
            getUserWalletsWithAmountFromSpecificTime = GetUserWalletsWithAmountFromSpecificTime(
                analysisRepository, walletRepository, userRepository, currencyUseCases
            ),
            getUserTotalAmountByTypeFromSpecificTime = GetUserTotalAmountByTypeFromSpecificTime(
                analysisRepository, userRepository, currencyUseCases
            ),
            getUserTotalRecordBalance = GetUserTotalRecordBalance(
                analysisRepository,
                userRepository,
                currencyUseCases
            ),
        )
        return analysisUseCases
    }


}