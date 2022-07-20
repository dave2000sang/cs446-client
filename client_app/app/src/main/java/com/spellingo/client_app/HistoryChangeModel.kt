package com.spellingo.client_app

import android.app.Application

/**
 * Model to update a [HistoryDatabase] entry
 */
class HistoryChangeModel(application: Application) {
    private val histDb = HistoryDatabase.getInstance(application)

    /**
     * Update the result of a specific word
     * @param word the word to update
     * @param locale word locale
     * @param category word category
     * @param result whether spelling was correct
     */
    suspend fun update(word: String, locale: Locale, category: String, result: Boolean) {
        val dao = histDb.historyDao()
        val resultInt = if(result) 1 else 0
        dao.updateStats(word, locale, category, resultInt)
    }
}