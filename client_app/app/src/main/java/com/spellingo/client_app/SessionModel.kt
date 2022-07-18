package com.spellingo.client_app

import android.app.Application
import java.util.*

/**
 * Model for Session fetching
 * @param application ApplicationContext for database creation
 */
class SessionModel(application: Application) {
    private val sessionDb = SessionDatabase.getInstance(application)

    private var correct = 0
    private var currentId = 0
    private var currentSession: Session? = null
    private val _listOfWords = mutableListOf<Pair<String, String>>()
    val listOfWords: List<Pair<String, String>>
        get() = _listOfWords

    /**
     * Fetch some words from the database to use in a session and return a word
     */
    suspend fun getNewSession(category: String, difficulty: Difficulty) {
        println("DEBUG Fetching new session") // DEBUG
        _listOfWords.clear()
        correct = 0
        currentId = sessionDb.sessionDao().getNextId()
        currentSession = Session(
            currentId,
            Date().toString(),
            category,
            difficulty,
            "",
            ""
        )
    }

    /**
     * Add the current game word to the running session
     */
    suspend fun addToCurrentSession(word: String, attempt: String) {
        if(currentSession == null) return
        val result = word == attempt
        _listOfWords.add(Pair(word, attempt))
        currentSession!!.sessionWords += ";$word"
        currentSession!!.sessionGuess += ";$attempt"
        println("DEBUG Added $word as $result") //DEBUG
        println("DEBUG session currently " + currentSession?.sessionWords + " " + currentSession?.sessionGuess)
        sessionDb.sessionDao().insert(currentSession!!.copy(
            sessionWords = currentSession!!.sessionWords.substring(1),
            sessionGuess = currentSession!!.sessionGuess.substring(1)
        ))
    }
}
