package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.MutableLiveData

/**
 * Concrete [GameStrategy] for Word of the Day, fetching words from the server
 */
class WordOfTheDayStrategy(application: Application) : GameSessionStrategy {
    private val wordOfTheDayModel = WordOfTheDayModel(application)

    /**
     * Get words for a session from the word server
     * @param wordLiveData word return placeholder
     * @param category requested word category
     * @param difficulty requested word difficulty
     */
    override suspend fun getSessionWords(
        wordLiveData: MutableLiveData<Word?>,
        category: String,
        difficulty: Difficulty
    ) {
        // Get word from server
        val wotd = wordOfTheDayModel.getWord(category)
        wordLiveData.postValue(wotd)
    }

    /**
     * Get next session word
     * @param wordLiveData word return placeholder
     * @return remaining words in session
     */
    override fun nextWord(wordLiveData: MutableLiveData<Word?>): Int {
        // Only one word is played, so there is no next word
        return 0
    }

    /**
     * Track the outcome of the user's spelling
     * @param word word attempted
     * @param category word category
     * @param result true if and only if user spelled [word] correctly
     */
    override suspend fun updateResults(word: String, category: String, result: Boolean) {
        // Commit to global history
        wordOfTheDayModel.saveResult(word, category, result)
    }

    /**
     * Run word update logic after session
     * @param category word category
     * @param difficulty word difficulty
     */
    override suspend fun postSessionUpdate(category: String, difficulty: Difficulty) {
        // Do nothing, we don't affect local cache
    }
}