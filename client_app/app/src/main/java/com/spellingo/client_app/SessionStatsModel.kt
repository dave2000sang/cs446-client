package com.spellingo.client_app

import android.app.Application

class SessionStatsModel(application: Application) {
    private val sessionDb = SessionDatabase.getInstance(application)

    /**
     * Get list of session identifiers
     */
    suspend fun getSessionDates(): List<SessionDate> {
        return sessionDb.sessionDao().getAllDates()
    }

    /**
     * Load a fetched session
     */
    suspend fun getSessionStats(id: Int): List<Pair<String, String>> {
        val session = sessionDb.sessionDao().getSession(id)
        val words = session.sessionWords.split(";")
        val guesses = session.sessionGuess.split(";")
        return words zip guesses
    }
}