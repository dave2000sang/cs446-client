package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Persistent state and data bindings for [SessionHistoryFragment]
 */
class SessionHistoryViewModel(application: Application) : DynamicViewModel(application) {
    private val sessionStatsModel = SessionStatsModel(application)
    private val _listOfSessions = MutableLiveData<List<SessionDate>?>()
    private val _guessLiveData = MutableLiveData<List<Pair<String, String>>?>()

    /**
     * List of session identifiers and metadata
     */
    val listOfSessions: LiveData<List<SessionDate>?>
        get() = _listOfSessions

    /**
     * List of pairs of correct and attempted spellings for a requested session (see [getSession])
     */
    val guessLiveData: LiveData<List<Pair<String, String>>?>
        get() = _guessLiveData

    /**
     * Get all session identifying information
     * Pushes to [listOfSessions]
     */
    fun loadSessionData() {
        viewModelScope.launch {
            try {
                val sessions = sessionStatsModel.getSessionDates()
                _listOfSessions.postValue(sessions)
            } catch (e: Exception) {
                System.err.println(e.toString())
            }
        }
    }

    /**
     * Get session word list and attempts
     * Pushes to [guessLiveData]
     * @param session session information
     */
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

    /**
     * Clear guess live data
     * Pushes to [guessLiveData]
     */
    fun clearGuesses() {
        _guessLiveData.value = null
    }

    /**
     * Reset LiveData
     * Pushes to [guessLiveData] and [listOfSessions]
     */
    override fun resetLiveData() {
        _guessLiveData.value = null
        _listOfSessions.value = null
    }
}