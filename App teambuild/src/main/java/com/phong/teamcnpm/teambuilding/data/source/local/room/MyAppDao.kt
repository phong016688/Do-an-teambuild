package com.phong.teamcnpm.teambuilding.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, GroupEntity::class, EventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MyAppDao : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun eventDao(): EventDao
}