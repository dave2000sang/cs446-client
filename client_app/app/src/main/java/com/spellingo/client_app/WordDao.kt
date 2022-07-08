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
     */
    @Query("SELECT * FROM word WHERE locale=:locale ORDER BY RANDOM() LIMIT :n")
    suspend fun getRandomN(n: Int, locale: String): List<Word>

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