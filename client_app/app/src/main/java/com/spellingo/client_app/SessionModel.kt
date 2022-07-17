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

//    val sessionWordMap = HashMap<String, Boolean>()
    var correct = 0
    var currentId = 0
    var currentSession: Session? = null

    /**
     * Fetch some words from the database to use in a session and return a word
     */
    suspend fun getNewSession(category: String, difficulty: Difficulty) {
        println("DEBUG Fetching new session") // DEBUG
//        sessionWordMap.clear()
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
    suspend fun addToCurrentSession(word: String, result: Boolean) {
        if(currentSession == null) return
        currentSession!!.sessionWords += ";$word"
        currentSession!!.sessionGuess += ";$result"
        println("DEBUG Added $word as $result") //DEBUG
        println("DEBUG session currently " + currentSession?.sessionWords + " " + currentSession?.sessionGuess)
        sessionDb.sessionDao().insert(currentSession!!.copy(
            sessionWords = currentSession!!.sessionWords.substring(1),
            sessionGuess = currentSession!!.sessionGuess.substring(1)
        ))
    }
}
