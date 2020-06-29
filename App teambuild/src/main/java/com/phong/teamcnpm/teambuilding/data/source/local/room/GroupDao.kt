package com.phong.teamcnpm.teambuilding.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
  @Query("SELECT * FROM CURRENT_GROUP")
  fun observeGroup(): Flow<List<GroupEntity>>

  @Query("SELECT * FROM CURRENT_GROUP")
  suspend fun getGroup(): List<GroupEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGroup(group: GroupEntity)

  @Query("DELETE FROM CURRENT_GROUP")
  suspend fun deleteGroup()
}