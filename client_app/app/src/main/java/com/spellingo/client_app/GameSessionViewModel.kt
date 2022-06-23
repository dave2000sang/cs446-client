package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * Application-aware ViewModel for the game session screen
 */
class GameSessionViewModel(application: Application) : AndroidViewModel(application) {
    private val wordModel = WordModel(application)

    /**
     * Load the first word of a new session
     */
    fun getNewSessionWord(): LiveData<Word> {
        val result = MutableLiveData<Word>()
        viewModelScope.launch {
            val curWord = wordModel.getNewSessionWord()
            result.postValue(curWord)
        }
        return result
    }

    /**
     * Load the next word
     */
    fun getWord(): Word {
        return wordModel.getWord()
    }
}