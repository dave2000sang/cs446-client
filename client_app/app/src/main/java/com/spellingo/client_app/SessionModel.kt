package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.collections.HashMap

/**
 * Model for Session fetching
 * @param application ApplicationContext for database creation
 */
class SessionModel(application: Application) {
    private val sessionDb = SessionDatabase.getInstance(application)

    val sessionWordMap = HashMap<String, Boolean>()
    var correct = 0

    /**
     * Fetch some words from the database to use in a session and return a word
     */
    suspend fun getNewSession() {
        sessionWordMap.clear()
        correct = 0
    }

    /**
     * Add the current game word to the running session
     */
    fun addToCurrentSession(word: String, result: Boolean) {
        sessionWordMap[word] = result
    }

    /**
     * Load a fetched session
     */
    fun loadSessionToModel(session: Session) {
        sessionWordMap.clear()
        val words = session.sessionWords.split(";")
        val guesses = session.sessionGuess.split(";")
        for (i in words.indices) {
            sessionWordMap[words[i]] = (guesses[i] == "1")
            correct += guesses[i].toInt()
        }
    }

    /**
     * Save the current session to local db
     */
    suspend fun saveCurrentSession() {
        var words = ""
        var guesses = ""
        for ((word, guess) in sessionWordMap) {
            words += ";$word"
            guesses += ";$guess"
        }
        val newSession = Session(
            sessionDb.sessionDao().getNextId(),
            Date().toString(),
            words.substring(1),
            guesses.substring(1)
        )
        sessionDb.sessionDao().insert(newSession)
    }
}