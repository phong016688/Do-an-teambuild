package com.phong.teamcnpm.teambuilding.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM CURRENT_USER")
    fun observerCurrentUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM CURRENT_USER")
    suspend fun getCurrentUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListUser(users: List<UserEntity>)

    @Query("DELETE FROM CURRENT_USER")
    suspend fun deleteUser()
}