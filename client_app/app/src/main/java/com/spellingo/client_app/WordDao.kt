package com.spellingo.client_app

import androidx.room.*

/**
 * Data Access Object for Word in Room database
 */
@Dao
interface WordDao {
    /**
     * Get N random words from database
     * @param n number of words to fetch
     * @param locale desired English locale
     * @return list of words
     */
    @Query("SELECT * FROM word WHERE locale=:locale AND category=:category AND difficulty=:difficulty ORDER BY RANDOM() LIMIT :n")
    suspend fun getRandomN(n: Int, locale: Locale, category: String, difficulty: Difficulty): List<Word>

    /**
     * Get all words in database
     * TODO check if this is needed anymore
     */
    @Query("SELECT * FROM word")
    suspend fun getAllWords(): List<Word>

    /**
     * Count number of words in database
     */
    @Query("SELECT COUNT(*) FROM word")
    suspend fun countWords(): Int

    /**
     * Get all words in database matching parameters
     */
    @Query("SELECT * FROM word WHERE locale=:locale AND category=:category AND difficulty=:difficulty")
    suspend fun getKeyedWords(locale: Locale, category: String, difficulty: Difficulty): List<Word>

    /**
     * Get all categories
     */
    @Query("SELECT DISTINCT category FROM word")
    suspend fun getCategories(): List<String>

    /**
     * Delete a category's words
     */
    @Query("DELETE FROM word WHERE category=:category")
    suspend fun deleteCategory(category: String)

    /**
     * Add words to database
     * @param words word(s) to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
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