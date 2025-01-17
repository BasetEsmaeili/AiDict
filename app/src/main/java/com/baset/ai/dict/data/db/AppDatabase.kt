package com.baset.ai.dict.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CardEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDAO(): CardDAO
}
