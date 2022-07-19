package com.spellingo.client_app

import android.app.Application

/**
 * Model to fetch statistics from [SessionDatabase]
 */
class SessionStatsModel(application: Application) {
    private val sessionDb = SessionDatabase.getInstance(application)

    /**
     * Get list of session identifiers
     * @return list of session information as [SessionDate]
     */
    suspend fun getSessionDates(): List<SessionDate> {
        return sessionDb.sessionDao().getAllDates()
    }

    /**
     * Load a fetched session
     * @param id session id
     * @return list of pairs of (word, attempted spelling)
     */
    suspend fun getSessionStats(id: Int): List<Pair<String, String>> {
        val session = sessionDb.sessionDao().getSession(id)
        val words = session.sessionWords.split(";")
        val guesses = session.sessionGuess.split(";")
        return words zip guesses
    }
}