package com.fredy.mysavings.Feature.Domain.UseCases.BookUseCases

import com.fredy.mysavings.BaseUseCaseTest
import com.fredy.mysavings.Feature.Domain.Model.Book
import com.fredy.mysavings.Feature.Domain.Util.Resource
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class BookUseCasesTest : BaseUseCaseTest() {

    private lateinit var upsertBook: UpsertBook
    private lateinit var deleteBook: DeleteBook
    private lateinit var getBook: GetBook
    private lateinit var getUserBooks: GetUserBooks

    @Before
    fun setUp() {
        upsertBook = UpsertBook(fakeBookRepository, fakeUserRepository)
        deleteBook = DeleteBook(fakeBookRepository)
        getBook = GetBook(fakeBookRepository)
        getUserBooks = GetUserBooks(fakeBookRepository, fakeUserRepository)
    }

    @Test
    fun `Upsert New Book`() = runBlocking {
        val bookId = "testing"
        val book = Book(
            bookId = bookId,
            bookName = "Book a",
            bookIcon = 0,
            bookIconDescription = "Icon a"
        )

        val result = upsertBook(book)

        assertEquals(bookId, result)
        val insertedBook =
            fakeBookRepository.getBook(bookId = bookId).lastOrNull()
        assertEquals(book.copy(userIdFk = currentUserId), insertedBook)
    }

    @Test
    fun `Upsert Existing Book`() = runBlocking {
        val bookId = "testing"
        val oldBook = Book(
            bookId = bookId,
            userIdFk = currentUserId,
            bookName = "Book a",
            bookIcon = 0,
            bookIconDescription = "Icon a"
        )

        fakeBookRepository.upsertBook(oldBook)

        val book = oldBook.copy(
            bookName = "Book b",
            bookIconDescription = "Icon b"
        )

        val result = upsertBook(book)

        assertEquals(bookId, result)
        val insertedBook =
            fakeBookRepository.getBook(bookId = bookId).lastOrNull()
        assertNotEquals(oldBook, insertedBook)
        assertEquals(book, insertedBook)
    }

    @Test
    fun `Delete Existing Book`() {
        runBlocking {
            val bookId = "testing"
            val book = Book(
                bookId = bookId,
                userIdFk = currentUserId,
                bookName = "Book a",
                bookIcon = 0,
                bookIconDescription = "Icon a"
            )

            fakeBookRepository.upsertBook(book)

            deleteBook(book)
            assertFailsWith<NullPointerException> {
                fakeBookRepository.getBook(bookId = bookId).first()
            }
        }
    }

    @Test
    fun `Delete Non-Existent Book`() {
        runBlocking {
            val bookId = "testing"
            val book = Book(
                bookId = bookId,
                userIdFk = currentUserId,
                bookName = "Book a",
                bookIcon = 0,
                bookIconDescription = "Icon a"
            )

            deleteBook(book)

            assertFailsWith<NullPointerException> {
                fakeBookRepository.getBook(bookId = bookId).first()
            }
        }
    }

    @Test
    fun `Retrieve Existing Book`() = runBlocking {
        val bookId = "testing"
        val book = Book(
            bookId = bookId,
            userIdFk = currentUserId,
            bookName = "Book a",
            bookIcon = 0,
            bookIconDescription = "Icon a"
        )

        fakeBookRepository.upsertBook(book)

        val retrievedBook = getBook(bookId = bookId).first()

        assertEquals(book, retrievedBook)
    }

    @Test
    fun `Retrieve Non-Existent Book`() {
        runBlocking {
            val nonExistentBookId = "nonExistent"

            assertFailsWith<Exception> {
                getBook(nonExistentBookId).first()
            }
        }
    }

    @Test
    fun `Retrieve User Books`() = runBlocking {
        val bookMapFlow = getUserBooks()
        val bookMapResource = bookMapFlow.last()

        assertTrue(bookMapResource is Resource.Success)
        val bookMaps = (bookMapResource as Resource.Success).data!!
        assertEquals(
            fakeBookRepository.getUserBooks(currentUserId).first().size,
            bookMaps.size)
    }
}
