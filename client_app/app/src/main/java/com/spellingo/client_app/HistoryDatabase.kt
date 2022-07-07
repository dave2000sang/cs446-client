package com.spellingo.client_app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for History's
 */
@Database(entities = [History::class], version = 1)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    /*
    Singleton design pattern for WordDatabase instance
    https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin
    https://github.com/android/architecture-components-samples.git
     */
    companion object {
        @Volatile private var INSTANCE: HistoryDatabase? = null

        /*
        Double check locking
        1. Do a fast NULL check. If INSTANCE not NULL, return
        2. If NULL, acquire lock. INSTANCE may not be NULL due to race cond
        3. If not NULL, return. Otherwise create DB and assign INSTANCE to it
         */
        fun getInstance(context: Context): HistoryDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                HistoryDatabase::class.java, "History.db")
                .build()
    }
}