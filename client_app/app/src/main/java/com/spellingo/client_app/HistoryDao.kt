package com.spellingo.client_app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Data Access Object for History in Room database
 */
@Dao
interface HistoryDao {
    /**
     * Get total number of correct words spelled
     */
    @Query("SELECT SUM(correct) FROM history")
    suspend fun getNumCorrect(): Int

    /**
     * Get total number of attempts
     */
    @Query("SELECT SUM(total) FROM history")
    suspend fun getNumTotal(): Int

    /**
     * Get all words in database
     */
    @Query("SELECT * FROM history")
    suspend fun getAllWords(): List<History>

    /**
     * Update score for a specific word
     * @param id the word to search
     * @param success whether the word was spelled correctly, must be 0 or 1
     */
    @Query("UPDATE history SET total = total + 1, correct = correct + :success WHERE id = :id")
    suspend fun updateStats(id: String, success: Int)

    /**
     * Add words to database
     * @param words word history(s) to insert
     */
    @Insert
    suspend fun insert(vararg words: History)

    /**
     * Clear database
     */
    @Query("DELETE FROM history")
    suspend fun clear()
}