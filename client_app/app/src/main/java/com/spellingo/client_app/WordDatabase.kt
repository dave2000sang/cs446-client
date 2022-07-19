package com.spellingo.client_app

import android.content.Context
import androidx.room.*

/**
 * Room database for Words
 */
@Database(entities = [Word::class], version = 1)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    /*
    Singleton design pattern, as suggested by Android documentation and official sample code
    https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin
    https://github.com/android/architecture-components-samples.git
    https://developer.android.com/training/data-storage/room#database
     */
    companion object {
        @Volatile private var INSTANCE: WordDatabase? = null

        /**
         * Get single instance of [WordDatabase]
         * @param context application context needed for Room database
         * @return database
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