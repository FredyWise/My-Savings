package com.fredy.mysavings.Feature.Domain.UseCases.CategoryUseCases

import com.fredy.mysavings.BaseUseCaseTest
import com.fredy.mysavings.Feature.Data.Database.Model.Category
import com.fredy.mysavings.Feature.Data.Enum.RecordType
import com.fredy.mysavings.Util.Resource
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

class CategoryUseCasesTest : BaseUseCaseTest() {

    private lateinit var upsertCategory: UpsertCategory
    private lateinit var deleteCategory: DeleteCategory
    private lateinit var getCategory: GetCategory
    private lateinit var getCategoryMapOrderedByName: GetCategoryMapOrderedByName

    @Before
    fun setUp() {
        upsertCategory = UpsertCategory(fakeCategoryRepository, fakeAuthRepository)
        deleteCategory = DeleteCategory(fakeCategoryRepository)
        getCategory = GetCategory(fakeCategoryRepository)
        getCategoryMapOrderedByName =
            GetCategoryMapOrderedByName(fakeCategoryRepository, fakeAuthRepository)
    }

    @Test
    fun `Upsert New Category`() = runBlocking {
        val categoryId = "testing"
        val category = Category(
            categoryId = categoryId,
            userIdFk = currentUserId,
            categoryName = "Category a",
            categoryIcon = 0,
            categoryIconDescription = "Icon a"
        )

        val result = upsertCategory(category)

        assertEquals(categoryId, result)
        val insertedCategory =
            fakeCategoryRepository.getCategory(categoryId = categoryId).lastOrNull()
        assertEquals(category, insertedCategory)
    }

    @Test
    fun `Upsert Existing Category`() = runBlocking {
        val categoryId = "testing"
        val oldCategory = Category(
            categoryId = categoryId,
            userIdFk = currentUserId,
            categoryName = "Category a",
            categoryIcon = 0,
            categoryIconDescription = "Icon a"
        )

        fakeCategoryRepository.upsertCategory(oldCategory)

        val category = Category(
            categoryId = categoryId,
            userIdFk = currentUserId,
            categoryName = "Category b",
            categoryIcon = 0,
            categoryIconDescription = "Icon b"
        )

        val result = upsertCategory(category)

        assertEquals(categoryId, result)
        val insertedCategory =
            fakeCategoryRepository.getCategory(categoryId = categoryId).lastOrNull()
        assertNotEquals(oldCategory, insertedCategory)
        assertEquals(category, insertedCategory)
    }

    @Test
    fun `Delete Existing Category`() {
        runBlocking {
            val categoryId = "testing"
            val category = Category(
                categoryId = categoryId,
                userIdFk = currentUserId,
                categoryName = "Category a",
                categoryIcon = 0,
                categoryIconDescription = "Icon a"
            )

            fakeCategoryRepository.upsertCategory(category)

            deleteCategory(category)
            assertFailsWith<NullPointerException> {
                fakeCategoryRepository.getCategory(categoryId = categoryId).first()
            }
        }
    }

    @Test
    fun `Delete Non-Existent Category`() {
        runBlocking {
            val categoryId = "testing"
            val category = Category(
                categoryId = categoryId,
                userIdFk = currentUserId,
                categoryName = "Category a",
                categoryIcon = 0,
                categoryIconDescription = "Icon a"
            )

            deleteCategory(category)

            assertFailsWith<NullPointerException> {
                fakeCategoryRepository.getCategory(categoryId = categoryId).first()
            }
        }
    }

    @Test
    fun `Retrieve Existing Category`() = runBlocking {
        val categoryId = "testing"
        val category = Category(
            categoryId = categoryId,
            userIdFk = currentUserId,
            categoryName = "Category a",
            categoryIcon = 0,
            categoryIconDescription = "Icon a"
        )

        fakeCategoryRepository.upsertCategory(category)

        val retrievedCategory = getCategory(categoryId = categoryId).first()

        assertEquals(category, retrievedCategory)
    }

    @Test
    fun `Retrieve Non-Existent Category`() {
        runBlocking {
            val nonExistentCategoryId = "nonExistent"

            assertFailsWith<Exception> {
                getCategory(nonExistentCategoryId).first()
            }
        }
    }

    @Test
    fun `Retrieve Category Map Ordered By Name`() = runBlocking {
        val categoryMapFlow = getCategoryMapOrderedByName()
        val categoryMapResource = categoryMapFlow.last()

        assertTrue(categoryMapResource is Resource.Success)
        val categoryMaps = (categoryMapResource as Resource.Success).data!!
        assertEquals(
            fakeCategoryRepository.getUserCategories(currentUserId).first().size,
            categoryMaps.sumOf { it.categories.size })
        assertEquals(listOf(RecordType.Income, RecordType.Expense).size, categoryMaps.size)
    }
}

