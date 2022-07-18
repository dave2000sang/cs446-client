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
     */
    @Query("SELECT IFNULL(" +
            "(SELECT id + 1 FROM session ORDER BY id DESC LIMIT 1)," +
            "0" +
            ")")
    suspend fun getNextId(): Int

    /**
     * Get the 'n' most recent sessions stored locally
     */
    @Query("SELECT * FROM session ORDER BY id DESC LIMIT :n")
    suspend fun getSessions(n: Int): List<Session>

    /**
     * Get session by id
     */
    @Query("SELECT * FROM session WHERE id=:id")
    suspend fun getSession(id: Int): Session

    /**
     * Get session dates and id
     */
    @Query("SELECT id, date, category, difficulty FROM session")
    suspend fun getAllDates(): List<SessionDate>

    /**
     * Remove session from database using id
     * @param id session(s) to remove
     */
    @Query("DELETE FROM session WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Remove sessions from database
     * @param sessions session(s) to remove
     */
    @Delete
    suspend fun delete(vararg sessions: Session)

    /**
     * Clear entire database
     */
    @Query("DELETE FROM session")
    suspend fun clear()
}
