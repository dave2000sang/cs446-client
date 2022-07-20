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
     * Count number of words in database
     * @return number of words in cache
     */
    @Query("SELECT COUNT(*) FROM word")
    suspend fun countWords(): Int

    /**
     * Get all words in database matching parameters
     * @param locale locale for definition and pronunciation
     * @param category word category
     * @param difficulty word difficulty
     * @return list of words matching specified parameters
     */
    @Query("SELECT * FROM word WHERE locale=:locale AND category=:category AND difficulty=:difficulty")
    suspend fun getKeyedWords(locale: Locale, category: String, difficulty: Difficulty): List<Word>

    /**
     * Get all categories
     * @return list of all stored categories
     */
    @Query("SELECT DISTINCT category FROM word")
    suspend fun getCategories(): List<String>

    /**
     * Delete a category's words
     * @param category word category
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
}