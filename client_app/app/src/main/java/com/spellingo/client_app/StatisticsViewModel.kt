package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Persistent state and data bindings for [StatisticsFragment]
 */
class StatisticsViewModel(application: Application) : DynamicViewModel(application) {
    private val histModel = HistoryStatsModel(application)
    private val _ratioLiveData = MutableLiveData<List<Pair<String, Int>>?>()
    private val _categoryList = mutableListOf<String>()
    private var _statisticsChartType = StatisticsChartType.ALL

    /**
     * Pair of correct spellings and total attempts for all words in history
     */
    val ratioLiveData: LiveData<List<Pair<String, Int>>?>
        get() = _ratioLiveData

    /**
     * List of categories
     */
    val categoryList: List<String>
        get() = _categoryList

    /**
     * Type of chart
     */
    val statisticsChartType: StatisticsChartType
        get() = _statisticsChartType

    private fun correctPairToRatio(pair: Pair<Int, Int>): List<Pair<String, Int>> {
        return listOf(
            Pair("Correct", pair.first),
            Pair("Incorrect", pair.second - pair.first)
        ).filterNot {
            it.second == 0
        }
    }

    /**
     * Get statistics for all attempts
     * Pushes to [ratioLiveData]
     */
    fun requestGlobalStats() {
        viewModelScope.launch {
            try {
                val pair = histModel.getTotalStats()
                val ratioList = correctPairToRatio(pair)
                _statisticsChartType = StatisticsChartType.ALL
                _ratioLiveData.postValue(ratioList)
            } catch (e: Exception) {
                System.err.println(e.printStackTrace())
                System.err.println(e.toString())
            }
        }
    }

    /**
     * Get play counts for all categories
     * Pushes to [ratioLiveData]
     */
    fun requestCategoryBreakdown() {
        viewModelScope.launch {
            try {
                val categories = histModel.getOverallCategoryStats().map { pair ->
                    Pair(pair.first.replaceFirstChar { it.uppercase() }, pair.second)
                }.filterNot {
                    it.second == 0
                }
                _categoryList.clear()
                _categoryList.addAll(categories.map { it.first })
                _statisticsChartType = StatisticsChartType.CATEGORY
                _ratioLiveData.postValue(categories)
            }
            catch (e: Exception) {
                System.err.println(e.printStackTrace())
                System.err.println(e.toString())
            }
        }
    }

    /**
     * Get play counts for all difficulties
     * Pushes to [ratioLiveData]
     */
    fun requestDifficultyBreakdown() {
        viewModelScope.launch {
            try {
                val difficulties = histModel.getOverallDifficultyStats().map { pair ->
                    Pair(pair.first.toString().lowercase().replaceFirstChar { it.uppercase() }, pair.second)
                }.filterNot {
                    it.second == 0
                }
                _statisticsChartType = StatisticsChartType.DIFFICULTY
                _ratioLiveData.postValue(difficulties)
            }
            catch (e: Exception) {
                System.err.println(e.printStackTrace())
                System.err.println(e.toString())
            }
        }
    }

    /**
     * Get play counts for a specific category
     * Pushes to [ratioLiveData]
     * @param category category to request for
     */
    fun requestCategoryStats(category: String) {
        viewModelScope.launch {
            try {
                val pair = histModel.getSpecificCategoryStats(category)
                val ratioList = correctPairToRatio(pair)
                _statisticsChartType = StatisticsChartType.SELECTED_CATEGORY
                _ratioLiveData.postValue(ratioList)
            }
            catch (e: Exception) {
                System.err.println(e.printStackTrace())
                System.err.println(e.toString())
            }
        }
    }

    /**
     * Get play counts for a specific difficulty
     * Pushes to [ratioLiveData]
     * @param difficulty Difficulty to request
     */
    fun requestDifficultyStats(difficulty: Difficulty) {
        viewModelScope.launch {
            try {
                val pair = histModel.getSpecificDifficultyStats(difficulty)
                val ratioList = correctPairToRatio(pair)
                _statisticsChartType = StatisticsChartType.SELECTED_DIFFICULTY
                _ratioLiveData.postValue(ratioList)
            }
            catch (e: Exception) {
                System.err.println(e.printStackTrace())
                System.err.println(e.toString())
            }
        }
    }

    /**
     * Reset all LiveData
     * Pushes to [ratioLiveData]
     */
    override fun resetLiveData() {
        _ratioLiveData.value = null
    }
}