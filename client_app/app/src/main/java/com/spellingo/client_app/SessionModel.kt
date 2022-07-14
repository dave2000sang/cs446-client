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
    fun getNewSession() {
        println("DEBUG Fetching new session") // DEBUG
        sessionWordMap.clear()
        correct = 0
    }

    /**
     * Add the current game word to the running session
     */
    fun addToCurrentSession(word: String, result: Boolean) {
        sessionWordMap[word] = result
        println("DEBUG Added $word as $result") //DEBUG
    }

    /**
     * Load a fetched session
     */
    fun mapSessionToHashMap(session: Session): HashMap<String, Boolean> {
        val returnMap = HashMap<String, Boolean>()
        val words = session.sessionWords.split(";")
        val guesses = session.sessionGuess.split(";")
        for (i in words.indices) {
            returnMap[words[i]] = guesses[i] == "true"
        }
        println("DEBUG Loading session") // DEBUG
        return returnMap
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
        println("DEBUG Current session saved") // DEBUG
    }
}
