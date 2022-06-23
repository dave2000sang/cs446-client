package com.spellingo.client_app

import android.app.Application

/**
 * Model for Word fetching
 * @param application ApplicationContext for database creation
 */
class WordModel(application: Application) {
    private val wordDb = WordDatabase.getInstance(application)
    private val totalSessionWords = 5 //FIXME change to a user setting later
    private var listOfWords = mutableListOf<Word>()
    private var sessionWordIdx = 0

    /**
     * Fetch some words from the database to use in a session and return a word
     * @return a Word from the newly loaded list
     */
    suspend fun getNewSessionWord(): Word {
        //FIXME Is dropping the old list (i.e. garbage collection) better than O(n) insertion?
        val listRes = wordDb.wordDao().getRandomN(totalSessionWords)
        listOfWords.clear()
        listOfWords.addAll(listRes)
        return getWord()
    }

    /**
     * Selects a word from session's word list
     * @return Word information
     */
    fun getWord(): Word {
        val curIdx = sessionWordIdx
        sessionWordIdx = (sessionWordIdx + 1) % totalSessionWords
        return listOfWords[curIdx]
    }
}