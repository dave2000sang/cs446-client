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
    @Query("SELECT * FROM word WHERE locale=:locale ORDER BY RANDOM() LIMIT :n")
    suspend fun getRandomN(n: Int, locale: Locale): List<Word>

    /**
     * Get words that match locale
     * @param words words to look for in cache
     * @return words found in cache
     */
    @Query("SELECT * FROM word WHERE id IN (:words)")
    suspend fun getExisting(words: List<String>): List<Word>

    /**
     * Get all words in database
     * TODO check if this is needed anymore
     */
    @Query("SELECT * FROM word")
    suspend fun getAllWords(): List<Word>

    /**
     * Get all words in database matching parameters
     */
    @Query("SELECT * FROM word WHERE locale=:locale")
    suspend fun getCategoryWords(locale: Locale): List<Word>
    //TODO uncomment below after we support everything
//    suspend fun getCategoryWords(locale: Locale, category: Category, difficulty: Difficulty): List<Word>

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