package com.app.dreamiaselite.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users WHERE email = :email")
    suspend fun deleteUser(email: String)

    @Query("UPDATE users SET username = :username, targetYear = :targetYear, avatarUrl = :avatarUrl WHERE email = :email")
    suspend fun updateProfile(email: String, username: String, targetYear: Int, avatarUrl: String?)
}
