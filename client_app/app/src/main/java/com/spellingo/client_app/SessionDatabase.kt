package com.spellingo.client_app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for Sessions
 */
@Database(entities = [Session::class], version = 1)
abstract class SessionDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao

    /*
    Singleton design pattern, as suggested by Android documentation and official sample code
    https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin
    https://github.com/android/architecture-components-samples.git
    https://developer.android.com/training/data-storage/room#database
     */
    companion object {
        @Volatile private var INSTANCE: SessionDatabase? = null

        /**
         * Get single instance of [SessionDatabase]
         * @param context application context needed for Room database
         * @return database
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
