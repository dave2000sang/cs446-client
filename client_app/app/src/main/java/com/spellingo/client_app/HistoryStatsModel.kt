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
        val correct = dao.getNumCorrect()
        val total = dao.getNumTotal()
        return Pair(correct, total)
    }

    /**
     * Get total attempts by category
     * @return list of pairs of categories and their total attempts
     */
    suspend fun getOverallCategoryStats(): List<Pair<String, Int>> {
        val dao = histDb.historyDao()
        return dao.getCategories().map { category ->
            Pair(category, dao.getNumTotal(category))
        }
    }

    /**
     * Get total attempts by difficulty for standard play
     * @return map of difficulties and their attempts
     */
    suspend fun getOverallDifficultyStats(): Map<Difficulty, Int> {
        val dao = histDb.historyDao()
        val difficulties = listOf(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD)
        return difficulties.associateWith { difficulty ->
            dao.getNumTotal(difficulty)
        }
    }

    /**
     * Get stats for category
     * @return pair of (correct, total) integers representing attempts
     */
    suspend fun getSpecificCategoryStats(category: String): Pair<Int, Int> {
        val dao = histDb.historyDao()
        val correct = dao.getNumCorrect(category)
        val total = dao.getNumTotal(category)
        return Pair(correct, total)
    }

    /**
     * Get stats for difficulty
     * @return pair of (correct, total) integers representing attempts
     */
    suspend fun getSpecificDifficultyStats(difficulty: Difficulty): Pair<Int, Int> {
        val dao = histDb.historyDao()
        val correct = dao.getNumCorrect(difficulty)
        val total = dao.getNumTotal(difficulty)
        return Pair(correct, total)
    }
}