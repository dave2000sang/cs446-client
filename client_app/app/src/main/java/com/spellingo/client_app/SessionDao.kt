package com.spellingo.client_app

import androidx.room.*

/**
 * Data Access Object for Word in Room database
 */
@Dao
interface SessionDao {
    /**
     * Store session in database
     * @param session - session(s) to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg session: Session)

    /**
     * Get the next available id
     * @return next available id
     */
    @Query("SELECT IFNULL(" +
            "(SELECT id + 1 FROM session ORDER BY id DESC LIMIT 1)," +
            "0" +
            ")")
    suspend fun getNextId(): Int

    /**
     * Get session by id
     * @param id session id
     * @return specified session
     */
    @Query("SELECT * FROM session WHERE id=:id")
    suspend fun getSession(id: Int): Session

    /**
     * Get session dates and id
     * @return list of all session identifying data as [SessionDate]'s
     */
    @Query("SELECT id, date, category, difficulty FROM session ORDER BY id DESC")
    suspend fun getAllDates(): List<SessionDate>
}
