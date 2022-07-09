package com.spellingo.client_app

import android.app.Application

class HistoryStatsModel(application: Application) {
    private val histDb = HistoryDatabase.getInstance(application)

    /**
     * Get numbers for correct attempts and total attempts for all words in history
     * @return pair of (correct, total) integers representing attempts
     */
    suspend fun getTotalStats(): Pair<Int, Int> {
        val dao = histDb.historyDao()
        val wordCount = dao.getAllWords().size
        if(wordCount <= 0) return Pair(0, 0)
        val correct = dao.getNumCorrect()
        val total = dao.getNumTotal()
        return Pair(correct, total)
    }

    /**
     * Get all history entries
     */
    suspend fun getAllHistory(): List<History> {
        return histDb.historyDao().getAllWords()
    }
}