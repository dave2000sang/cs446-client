package com.spellingo.client_app

import android.app.Application
import androidx.preference.PreferenceManager

/**
 * Model for Word fetching
 */
class WordModel(application: Application) {
    private val wordDb = WordDatabase.getInstance(application)
    private var listOfWords = mutableListOf<Word>()
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    val sessionWords: List<Word>
        get() = listOfWords

    /**
     * Fetch some words from the database to use in a session and return a word
     * @param category word category
     * @param difficulty word difficulty
     * @return first word of new session
     */
    suspend fun getNewSessionWords(category: String, difficulty: Difficulty): Word {
        val totalSessionWords = sharedPreferences.getInt("number_words_per_sessions", 10)
        val localeString = sharedPreferences.getString("locale_preferences", "us")
        val locale = Locale.getByName(localeString!!)
        val listRes = wordDb.wordDao().getRandomN(totalSessionWords, locale, category, difficulty)
        listOfWords.clear()
        listOfWords.addAll(listRes)
        return listOfWords.removeAt(0)
    }

    /**
     * Selects a word from session's word list
     * @return next word of current session
     */
    fun getWord(): Word? {
        if(listOfWords.size == 0) {
            return null
        }
        return listOfWords.removeAt(0)
    }

    /**
     * Get number of words left in current session
     * @return number of remaining words
     */
    fun numSessionWords(): Int {
        return listOfWords.size
    }
}