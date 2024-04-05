package com.fredy.mysavings.Feature.Data.Database.Dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fredy.mysavings.Feature.Data.Database.Model.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert
    suspend fun upsertUser(userData: UserData)

    @Delete
    suspend fun deleteUser(userData: UserData)

    @Query("SELECT * FROM UserData WHERE firebaseUserId = :firebaseUserId")
    fun getUser(firebaseUserId: String): Flow<UserData>

    @Query("SELECT * FROM UserData ORDER BY username ASC")
    fun getAllUsersOrderedByName(): Flow<List<UserData>>

    @Query("SELECT * FROM UserData WHERE username LIKE :query OR emailOrPhone LIKE :query")
    fun searchUsers(query: String): Flow<List<UserData>>

    @Query("DELETE FROM UserData")
    suspend fun deleteAllUsers()
}