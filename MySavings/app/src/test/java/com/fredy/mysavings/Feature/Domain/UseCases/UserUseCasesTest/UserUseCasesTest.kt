package com.fredy.mysavings.Feature.Domain.UseCases.UserUseCasesTest

import com.fredy.mysavings.BaseUseCaseTest
import com.fredy.domain.model.UserData
import com.fredy.domain.useCases.UserUseCases.DeleteUser
import com.fredy.domain.useCases.UserUseCases.GetAllUsersOrderedByName
import com.fredy.domain.useCases.UserUseCases.GetCurrentUser
import com.fredy.domain.useCases.UserUseCases.GetUser
import com.fredy.domain.useCases.UserUseCases.InsertUser
import com.fredy.domain.useCases.UserUseCases.SearchUsers
import com.fredy.domain.useCases.UserUseCases.UpdateUser
import com.fredy.domain.util.resource.Resource
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Before
import kotlin.test.Test

class UserUseCasesTest : BaseUseCaseTest() {

    private lateinit var insertUser: InsertUser
    private lateinit var updateUser: UpdateUser
    private lateinit var deleteUser: DeleteUser
    private lateinit var getUser: GetUser
    private lateinit var getCurrentUser: GetCurrentUser
    private lateinit var getAllUsersOrderedByName: GetAllUsersOrderedByName
    private lateinit var searchUsers: SearchUsers

    @Before
    fun setUp() {
        insertUser = InsertUser(fakeUserRepository)
        updateUser = UpdateUser(fakeUserRepository)
        deleteUser = DeleteUser(fakeUserRepository)
        getUser = GetUser(fakeUserRepository)
        getCurrentUser = GetCurrentUser(fakeUserRepository)
        getAllUsersOrderedByName = GetAllUsersOrderedByName(fakeUserRepository)
        searchUsers = SearchUsers(fakeUserRepository)
    }

//    @Test
//    fun `Insert New User`() = runBlocking {
//        val user = UserData(
//            firebaseUserId = "testId",
//            username = "testUser",
//            email = "testEmail@test.com"
//        )
//
//        insertUser(user)
//
//        val insertedUser = fakeUserRepository.getUser(user.firebaseUserId).firstOrNull()
//        assertEquals(user, insertedUser)
//    }
    @Test
    fun `Update Existing User`() = runBlocking {
        val user = UserData(
            firebaseUserId = "testId",
            username = "testUser",
            email = "testEmail@test.com"
        )

        fakeUserRepository.upsertUser(user)

        val updatedUser = user.copy(username = "updatedUser")
        updateUser(updatedUser)

        val retrievedUser = fakeUserRepository.getUser(user.firebaseUserId).firstOrNull()
        assertEquals(updatedUser, retrievedUser)
    }

    @Test
    fun `Delete Existing User`() = runBlocking {
        val user = UserData(
            firebaseUserId = "testId",
            username = "testUser",
            email = "testEmail@test.com"
        )

        fakeUserRepository.upsertUser(user)
        deleteUser(user)

        val retrievedUser = fakeUserRepository.getUser(user.firebaseUserId).firstOrNull()
        assertNull(retrievedUser)
    }

    @Test
    fun `Get Existing User`() = runBlocking {
        val user = UserData(
            firebaseUserId = "testId",
            username = "testUser",
            email = "testEmail@test.com"
        )

        fakeUserRepository.upsertUser(user)

        val retrievedUser = getUser(user.firebaseUserId).firstOrNull()
        assertEquals(user, retrievedUser)
    }

    @Test
    fun `Get Current User`() = runBlocking {
        val userResource = getCurrentUser().lastOrNull()

        TestCase.assertTrue(userResource is Resource.Success)
        val user = (userResource as Resource.Success).data!!
        assertEquals(fakeUserRepository.getUser(currentUserId).lastOrNull(), user)
    }

    @Test
    fun `Get All Users Ordered By Name`() = runBlocking {
        val userA = UserData(
            firebaseUserId = "testIdA",
            username = "testUserA",
            email = "testEmailA@test.com"
        )

        val userB = UserData(
            firebaseUserId = "testIdB",
            username = "testUserB",
            email = "testEmailB@test.com"
        )

        fakeUserRepository.upsertUser(userA)
        fakeUserRepository.upsertUser(userB)
        val userList = fakeUserRepository.getAllUsersOrderedByName().lastOrNull()

        val userResource = getAllUsersOrderedByName().lastOrNull()

        assertEquals(userList, userResource)
    }

    @Test
    fun `Search Users`() = runBlocking {
        val userA = UserData(
            firebaseUserId = "testIdA",
            username = "testUserA",
            email = "testEmailA@test.com"
        )

        val userB = UserData(
            firebaseUserId = "testIdB",
            username = "testUserB",
            email = "testEmailB@test.com"
        )

        fakeUserRepository.upsertUser(userA)
        fakeUserRepository.upsertUser(userB)

        val searchResult = searchUsers("testUserA").firstOrNull()
        assertEquals(listOf(userA), searchResult)
    }

}
