package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    private val histModel = HistoryStatsModel(application)
    private val sessionModel = SessionModel(application)
    private val sessionDb = SessionDatabase.getInstance(application)
    private val _ratioLiveData = MutableLiveData<Pair<Int, Int>>()
    // Pair(correct, total) attempts for all words in history
    val ratioLiveData: LiveData<Pair<Int, Int>>
        get() = _ratioLiveData
    private val _listOfSessions = MutableLiveData<List<HashMap<String, Boolean>>>()
    val listOfSessions: LiveData<List<HashMap<String, Boolean>>>
        get() = _listOfSessions

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

    fun loadSessionData() {
        viewModelScope.launch {
            try {
                val sessions = sessionDb.sessionDao().getAllSessions()
                println("DEBUG number of sessions: ${sessions.size}") // DEBUG
                val mappedSessions = sessions.map {
                    sessionModel.mapSessionToHashMap(it)
                }
                _listOfSessions.postValue(mappedSessions)
            } catch (e: Exception) {
                System.err.println(e.toString())
            }
        }
    }
}