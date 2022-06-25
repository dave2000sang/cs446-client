package com.spellingo.client_app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for Sessions
 */
@Database(entities = [Session::class], version = 2)
abstract class SessionDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao

    /*
    Singleton design pattern for WordDatabase instance
    https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin
    https://github.com/android/architecture-components-samples.git
     */
    companion object {
        @Volatile private var INSTANCE: SessionDatabase? = null

        /*
        Double check locking
        1. Do a fast NULL check. If INSTANCE not NULL, return
        2. If NULL, acquire lock. INSTANCE may not be NULL due to race cond
        3. If not NULL, return. Otherwise create DB and assign INSTANCE to it
         */
        fun getInstance(context: Context): SessionDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                SessionDatabase::class.java, "Sessions.db")
                .build()
    }
}
