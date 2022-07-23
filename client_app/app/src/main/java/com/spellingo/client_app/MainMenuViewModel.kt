package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainMenuViewModel(application: Application) : AndroidViewModel(application) {
    private val model: UpdateModel = InitialUpdateModel(application)
    private var _startupLock = false

    /**
     * Whether startApp() has already been called
     */
    val startupLock: Boolean
        get() = _startupLock

    /**
     * One-shot startup since cannot rely on init
     */
    fun startApp() {
        if(!_startupLock) {
            _startupLock = true
            viewModelScope.launch {
                try {
                    model.generateWords()
                }
                catch(e: Exception) {
                    System.err.println(e.printStackTrace())
                    System.err.println(e.toString())
                }
            }
        }
    }
}