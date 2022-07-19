package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager

/**
 * Concrete [GameStrategy] for standard mode of play, fetching words from local cache
 */
class StandardGameStrategy(private val application: Application) : GameSessionStrategy {
    private val wordModel = WordModel(application)
    private val histModel = HistoryChangeModel(application)
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    /**
     * Get words for a session from local cache
     * @param wordLiveData word return placeholder
     * @param category requested word category
     * @param difficulty requested word difficulty
     */
    override suspend fun getSessionWords(wordLiveData: MutableLiveData<Word?>, category: String, difficulty: Difficulty) {
        val curWord = wordModel.getNewSessionWords(category, difficulty)
        wordLiveData.postValue(curWord)
    }

    /**
     * Get next session word
     * @param wordLiveData word return placeholder
     * @return remaining words in session
     */
    override fun nextWord(wordLiveData: MutableLiveData<Word?>): Int {
        val returnNum = wordModel.numSessionWords()
        val curWord = wordModel.getWord() ?: return 0
        wordLiveData.value = curWord
        return returnNum
    }

    /**
     * Track the outcome of the user's spelling
     * @param word word attempted
     * @param category word category
     * @param result true if and only if user spelled [word] correctly
     */
    override suspend fun updateResults(word: String, category: String, result: Boolean) {
        val locale = sharedPreferences.getString("locale_preferences", "us")!!
        histModel.update(word, Locale.getByName(locale), category, result)
    }

    /**
     * Run word update logic after session has ended to conditionally refresh local cache
     * @param category word category
     * @param difficulty word difficulty
     */
    override suspend fun postSessionUpdate(category: String, difficulty: Difficulty) {
        val postSessionUpdateModel = PostSessionUpdateModel(application, wordModel.sessionWords, category, difficulty)
        postSessionUpdateModel.generateWords()
    }
}