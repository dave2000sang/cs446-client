package com.spellingo.client_app

import android.app.Application
import java.util.*

/**
 * Model for Session fetching
 */
class SessionChangeModel(application: Application) {
    private val sessionDb = SessionDatabase.getInstance(application)
    private var correct = 0
    private var currentId = 0
    private var currentSession: Session? = null
    private val _listOfWords = mutableListOf<Pair<String, String>>()

    /**
     * List of session words played so far
     */
    val listOfWords: List<Pair<String, String>>
        get() = _listOfWords

    /**
     * Fetch some words from the database to use in a session and return a word
     * @param category category for current session's words
     * @param difficulty difficulty for current session's words
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
     * @param word word string
     * @param attempt user's attempted spelling of [word]
     */
    suspend fun addToCurrentSession(word: String, attempt: String) {
        /*
        We're committing to database on every word attempted for cases where in-memory tracking
        is lost. For example, if the app abnormally aborts during a session, we'll still have the
        session data in history for the statistics page.
         */
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
