package com.spellingo.client_app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
     * Get total number of correct words spelled for a category
     */
    @Query("SELECT SUM(correct) FROM history WHERE category=:category")
    suspend fun getNumCorrect(category: String): Int

    /**
     * Get total number of attempts for a category
     */
    @Query("SELECT SUM(total) FROM history WHERE category=:category")
    suspend fun getNumTotal(category: String): Int

    /**
     * Get total number of correct words spelled for a difficulty
     */
    @Query("SELECT SUM(correct) FROM history WHERE difficulty=:difficulty")
    suspend fun getNumCorrect(difficulty: Difficulty): Int

    /**
     * Get total number of attempts for a category
     */
    @Query("SELECT SUM(total) FROM history WHERE difficulty=:difficulty")
    suspend fun getNumTotal(difficulty: Difficulty): Int

    /**
     * Get all categories
     */
    @Query("SELECT DISTINCT category FROM history")
    suspend fun getCategories(): List<String>

    /**
     * Count number of words in database
     */
    @Query("SELECT COUNT(*) FROM history")
    suspend fun countWords(): Int

    /**
     * Get top played words
     * TODO see if needed
     * @param n number of words to fetch
     */
    @Query("SELECT * FROM history ORDER BY total DESC LIMIT :n")
    suspend fun getTopWords(n: Int): List<History>

    /**
     * Update score for a specific word
     * @param id the word to search
     * @param success whether the word was spelled correctly, must be 0 or 1
     */
    @Query("UPDATE history SET total = total + 1, correct = correct + :success WHERE id = :id")
    suspend fun updateStats(id: String, success: Int)

    /**
     * Check whether a word exists in database
     * @param word word to search for
     * @return WordKey if exists, null otherwise
     */
    @Query("SELECT * FROM history WHERE id=:id AND locale=:locale AND category=:category")
    suspend fun getExisting(id: String, locale: Locale, category: String): History?

    /**
     * Add words to database
     * @param words word history(s) to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg words: History)
}