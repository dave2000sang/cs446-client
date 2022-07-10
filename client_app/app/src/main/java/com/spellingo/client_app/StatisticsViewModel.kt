package com.spellingo.client_app

import android.app.Application
import android.se.omapi.Session
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    private val histModel = HistoryStatsModel(application)
    private val _ratioLiveData = MutableLiveData<Pair<Int, Int>>()
    // Pair(correct, total) attempts for all words in history
    val ratioLiveData: LiveData<Pair<Int, Int>>
        get() = _ratioLiveData

    // Kai add your sessions to the list below. I left it as String for now for testing purposes.
    private var sessionList = MutableLiveData<String>()

    fun requestGlobalStats() {
        viewModelScope.launch {
            try {
                val pair = histModel.getTotalStats()
                _ratioLiveData.postValue(pair)
            } catch (e: Exception) {
                System.err.println(e.printStackTrace())
                System.err.println(e.toString())
            }
        }
    }
}