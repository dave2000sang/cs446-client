package com.spellingo.client_app

import androidx.lifecycle.MutableLiveData

/**
 * Strategy for getting words for a game session
 */
interface GameSessionStrategy {
    /**
     * Get words for a session
     * @param wordLiveData word return placeholder
     * @param category requested word category
     * @param difficulty requested word difficulty
     */
    suspend fun getSessionWords(wordLiveData: MutableLiveData<Word?>, category: String, difficulty: Difficulty)

    /**
     * Get next session word
     * @param wordLiveData word return placeholder
     * @return remaining words in session
     */
    fun nextWord(wordLiveData: MutableLiveData<Word?>): Int

    /**
     * Track the outcome of the user's spelling
     * @param word word attempted
     * @param category word category
     * @param result true if and only if user spelled [word] correctly
     */
    suspend fun updateResults(word: String, category: String, result: Boolean)

    /**
     * Run word update logic after session has ended
     * @param category word category
     * @param difficulty word difficulty
     */
    suspend fun postSessionUpdate(category: String, difficulty: Difficulty)
}