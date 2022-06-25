package com.spellingo.client_app

import android.app.Application
import java.util.*

/**
 * Model for Session fetching
 * @param application ApplicationContext for database creation
 */
class SessionModel(application: Application) {
    private val sessionDb = SessionDatabase.getInstance(application)

    /**
     * Fetch some words from the database to use in a session and return a word
     */
    suspend fun getNewSession(): Session {
        return Session(
            sessionDb.sessionDao().getNextId(),
            Date().toString(),
            "",
            ""
        )
    }

    fun getSessionTotal(session: Session) {

    }
}
