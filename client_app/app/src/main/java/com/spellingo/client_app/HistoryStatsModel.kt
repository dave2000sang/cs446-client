package com.spellingo.client_app

import android.app.Application

/**
 * Model to get statistics from [HistoryDatabase]
 */
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
     * @return list of difficulties and their attempts
     */
    suspend fun getOverallDifficultyStats(): List<Pair<Difficulty, Int>> {
        val dao = histDb.historyDao()
        val difficulties = listOf(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD)
        return difficulties.map { difficulty ->
            Pair(difficulty, dao.getNumTotal(difficulty))
        }
    }

    /**
     * Get stats for a category
     * @param category the category to get stats from
     * @return pair of (correct, total) integers representing attempts
     */
    suspend fun getSpecificCategoryStats(category: String): Pair<Int, Int> {
        val dao = histDb.historyDao()
        val correct = dao.getNumCorrect(category)
        val total = dao.getNumTotal(category)
        return Pair(correct, total)
    }

    /**
     * Get stats for a difficulty
     * @param difficulty the difficulty to get stats from
     * @return pair of (correct, total) integers representing attempts
     */
    suspend fun getSpecificDifficultyStats(difficulty: Difficulty): Pair<Int, Int> {
        val dao = histDb.historyDao()
        val correct = dao.getNumCorrect(difficulty)
        val total = dao.getNumTotal(difficulty)
        return Pair(correct, total)
    }
}