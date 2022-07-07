package com.spellingo.client_app

import android.app.Application

class HistoryChangeModel(application: Application) {
    private val histDb = HistoryDatabase.getInstance(application)

    /**
     * Update the result of a specific word
     * @param word the word to update
     * @param result whether spelling was correct
     */
    suspend fun update(word: String, result: Boolean) {
        val dao = histDb.historyDao()
        val resultInt = if(result) 1 else 0
        dao.updateStats(word, resultInt)
    }

    /**
     * Insert words
     */
}