package com.fredy.search.di

import com.fredy.domain.repository.BookRepository
import com.fredy.domain.repository.RecordRepository
import com.fredy.domain.repository.UserRepository
import com.fredy.search.data.SearchRepositoryImpl
import com.fredy.search.domain.SearchRepository
import com.fredy.search.domain.useCases.GetAllRecords
import com.fredy.search.domain.useCases.SearchUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {
    @Provides
    @Singleton
    fun provideSearchRepository(
        recordRepository: RecordRepository,
        bookRepository: BookRepository
    ): SearchRepository {
        return SearchRepositoryImpl(recordRepository, bookRepository)
    }


    @Provides
    @Singleton
    fun provideSearchUseCases(
        searchRepository: SearchRepository,
        userRepository: UserRepository,
    ): SearchUseCases {
        return SearchUseCases(
            getAllBooks = GetAllRecords(searchRepository, userRepository)
        )
    }
}