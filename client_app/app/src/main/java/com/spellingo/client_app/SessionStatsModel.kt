package com.spellingo.client_app

import android.app.Application

class SessionStatsModel(application: Application) {
    private val sessionDb = SessionDatabase.getInstance(application)

    /**
     * Load a fetched session
     */
    fun getSessionStats(session: Session): List<Pair<String, Boolean>> {
        val words = session.sessionWords.split(";")
        val guesses = session.sessionGuess.split(";").map { it == "true" }
        return words zip guesses
    }
}