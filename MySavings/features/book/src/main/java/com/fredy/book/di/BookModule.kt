package com.fredy.book.di

import com.fredy.book.data.BookSpecificSpecificRepositoryImpl
import com.fredy.book.domain.BookSpecificRepository
import com.fredy.book.domain.useCases.book.BookUseCases
import com.fredy.book.domain.useCases.book.DeleteBook
import com.fredy.book.domain.useCases.book.GetBook
import com.fredy.book.domain.useCases.book.GetUserBooks
import com.fredy.book.domain.useCases.book.UpdateRecordItemWithDeletedBook
import com.fredy.book.domain.useCases.book.UpsertBook
import com.fredy.data.database.dao.BookDao
import com.fredy.data.database.firestoreDataSource.BookDataSource
import com.fredy.domain.repository.BookRepository
import com.fredy.domain.repository.RecordRepository

import com.fredy.domain.repository.UserRepository

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BookModule {

    @Provides
    @Singleton
    fun BookSpecificRepository(
        firestore: FirebaseFirestore,
        bookDataSource: BookDataSource,
        bookDao: BookDao,
        bookRepository: BookRepository
    ): BookSpecificRepository = BookSpecificSpecificRepositoryImpl(
        bookDataSource, bookDao, firestore,bookRepository
    )

    @Provides
    @Singleton
    fun provideBookUseCases(
        userRepository: UserRepository,
        recordRepository: RecordRepository,
        bookSpecificRepository: BookSpecificRepository
    ): BookUseCases = BookUseCases(
        upsertBook = UpsertBook(bookSpecificRepository, userRepository),
        deleteBook = DeleteBook(bookSpecificRepository),
        getBook = GetBook(bookSpecificRepository),
        getUserBooks = GetUserBooks(
            bookSpecificRepository,
            userRepository
        ),
        updateRecordItemWithDeletedBook = UpdateRecordItemWithDeletedBook(recordRepository,userRepository)
    )

}