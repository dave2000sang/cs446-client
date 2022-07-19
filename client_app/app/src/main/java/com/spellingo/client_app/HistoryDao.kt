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
     * @return total number of correct words
     */
    @Query("SELECT SUM(correct) FROM history")
    suspend fun getNumCorrect(): Int

    /**
     * Get total number of attempts
     * @return total number of attempts
     */
    @Query("SELECT SUM(total) FROM history")
    suspend fun getNumTotal(): Int

    /**
     * Get total number of correct words spelled for a category
     * @param category word category
     * @return total number of correct words
     */
    @Query("SELECT SUM(correct) FROM history WHERE category=:category")
    suspend fun getNumCorrect(category: String): Int

    /**
     * Get total number of attempts for a category
     * @param category word category
     * @return total number of attempts
     */
    @Query("SELECT SUM(total) FROM history WHERE category=:category")
    suspend fun getNumTotal(category: String): Int

    /**
     * Get total number of correct words spelled for a difficulty
     * @param difficulty word difficulty
     * @return total number of correct words
     */
    @Query("SELECT SUM(correct) FROM history WHERE difficulty=:difficulty")
    suspend fun getNumCorrect(difficulty: Difficulty): Int

    /**
     * Get total number of attempts for a category
     * @param difficulty word difficulty
     * @return total number of attempts
     */
    @Query("SELECT SUM(total) FROM history WHERE difficulty=:difficulty")
    suspend fun getNumTotal(difficulty: Difficulty): Int

    /**
     * Get all categories
     * @return list of all categories as strings
     */
    @Query("SELECT DISTINCT category FROM history")
    suspend fun getCategories(): List<String>

    /**
     * Count number of words in database
     * @return total number of words tracked
     */
    @Query("SELECT COUNT(*) FROM history")
    suspend fun countWords(): Int

    /**
     * Get top played words
     * @param n number of words to fetch
     * @return list of top played words
     */
    @Query("SELECT * FROM history ORDER BY total DESC LIMIT :n")
    suspend fun getTopWords(n: Int): List<History>

    /**
     * Update score for a specific word
     * @param id the word to search
     * @param success whether the word was spelled correctly, must be 0 or 1
     */
    @Query("UPDATE history SET total = total + 1, correct = correct + :success WHERE id=:id AND locale=:locale AND category=:category")
    suspend fun updateStats(id: String, locale: Locale, category: String, success: Int)

    /**
     * Check whether a word exists in database
     * @param word word to search for
     * @param locale locale for pronunciation and definition
     * @param category word category
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