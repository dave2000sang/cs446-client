package com.spellingo.client_app

import android.content.Context
import androidx.room.*

/**
 * Room database for Word's
 */
@Database(
    entities = [Word::class],
    version = 1,
    //TODO either figure out migrations or remove the below commented code
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ]
)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    /*
    Singleton design pattern for WordDatabase instance
    https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin
    https://github.com/android/architecture-components-samples.git
     */
    companion object {
        @Volatile private var INSTANCE: WordDatabase? = null

        /*
        Double check locking
        1. Do a fast NULL check. If INSTANCE not NULL, return
        2. If NULL, acquire lock. INSTANCE may not be NULL due to race cond
        3. If not NULL, return. Otherwise create DB and assign INSTANCE to it
         */
        fun getInstance(context: Context): WordDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                WordDatabase::class.java, "Words.db")
                .build()
    }
}