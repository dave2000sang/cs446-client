package com.spellingo.client_app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Data Access Object for Word in Room database
 */
@Dao
interface SessionDao {
    /**
     * Store session in database
     * @param session - session(s) to insert
     */
    @Insert
    suspend fun insert(vararg session: Session)

    /**
     * Get the next available id
     */
    @Query("SELECT id + 1 FROM session ORDER BY id DESC LIMIT 1")
    suspend fun getNextId(): Int

    /**
     * Get the 'n' most recent sessions stored locally
     */
    @Query("SELECT * FROM session ORDER BY id DESC LIMIT :n")
    suspend fun getSessions(n: Int): List<Session>

    /**
     * Get all sessions
     */
    @Query("SELECT * FROM session")
    suspend fun getAllSessions(): List<Session>

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
