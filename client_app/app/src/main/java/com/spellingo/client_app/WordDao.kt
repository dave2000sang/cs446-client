package com.spellingo.client_app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Data Access Object for Word in Room database
 */
@Dao
interface WordDao {
    /**
     * Get N random words from database
     * @param n number of words to fetch
     */
    @Query("SELECT * FROM word ORDER BY RANDOM() LIMIT :n")
    suspend fun getRandomN(n: Int): List<Word>

    /**
     * Update score for a specific word
     * @param id the word to search
     * @param success user's performance, must be 0 or 1
     */
    @Query("UPDATE word SET total = total + 1, score = score + :success WHERE id = :id")
    suspend fun updateStats(id: String, success: Int)

    /**
     * Add words to database
     * @param words word(s) to insert
     */
    @Insert
    suspend fun insert(vararg words: Word)

    /**
     * Remove words from database
     * @param words word(s) to remove
     */
    @Delete
    suspend fun delete(vararg words: Word)

    /**
     * Clear database
     */
    @Query("DELETE FROM word")
    suspend fun clear()
}