package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    private val histModel = HistoryStatsModel(application)
    private val sessionStatsModel = SessionStatsModel(application)
    private val _ratioLiveData = MutableLiveData<Pair<Int, Int>>()
    private val _listOfSessions = MutableLiveData<List<SessionDate>>()
    private val _guessLiveData = MutableLiveData<List<Pair<String, String>>>()
    // Pair(correct, total) attempts for all words in history
    val ratioLiveData: LiveData<Pair<Int, Int>>
        get() = _ratioLiveData
    val listOfSessions: LiveData<List<SessionDate>>
        get() = _listOfSessions
    val guessLiveData: LiveData<List<Pair<String, String>>>
        get() = _guessLiveData

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
                val sessions = sessionStatsModel.getSessionDates()
                println("DEBUG number of sessions: ${sessions.size}") // DEBUG
                _listOfSessions.postValue(sessions)
            } catch (e: Exception) {
                System.err.println(e.toString())
            }
        }
    }

    fun getSession(session: SessionDate) {
        val id = session.id
        viewModelScope.launch {
            try {
                val guess = sessionStatsModel.getSessionStats(id)
                _guessLiveData.postValue(guess)

            } catch (e: Exception) {
                System.err.println(e.toString())
            }
    }
}
}