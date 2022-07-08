package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainMenuViewModel(application: Application) : AndroidViewModel(application) {
    private val model: UpdateModel = InitialUpdateModel(application)
    private var startupLock = false

    /**
     * One-shot startup since cannot rely on init
     */
    fun startApp() {
        if(!startupLock) {
            startupLock = true
            viewModelScope.launch {
                try {
                    model.generateWords()
                }
                catch(e: Exception) {
                    System.err.println(e.toString())
                }
            }
        }
    }
}