package com.spellingo.client_app

import android.app.Application

/**
 * Model for Word fetching
 * @param application ApplicationContext for database creation
 */
class WordModel(application: Application) {
    private val wordDb = WordDatabase.getInstance(application)
    //TODO change to a user setting. MUST be greater than 0!
    private val totalSessionWords = 5
    private var listOfWords = mutableListOf<Word>()

    /**
     * Fetch some words from the database to use in a session and return a word
     */
    suspend fun getNewSessionWords(): Word {
        val listRes = wordDb.wordDao().getRandomN(totalSessionWords)
        listOfWords.clear()
        listOfWords.addAll(listRes)
        return listOfWords.removeAt(0)
    }

    /**
     * Selects a word from session's word list
     * @return Word information
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