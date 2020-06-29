package com.phong.teamcnpm.teambuilding.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
  @Query("SELECT * FROM CURRENT_EVENT")
  fun observerEvent(): Flow<List<EventEntity>>

  @Query("SELECT * FROM CURRENT_EVENT")
  suspend fun getEvents(): List<EventEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvents(users: List<EventEntity>)

  @Query("DELETE FROM CURRENT_EVENT")
  suspend fun deleteEvent()
}