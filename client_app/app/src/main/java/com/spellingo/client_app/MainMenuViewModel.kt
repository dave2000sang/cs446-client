package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainMenuViewModel(application: Application) : AndroidViewModel(application) {
    private val model = UpdateModel(application)
    fun startApp() {
        viewModelScope.launch {
            model.generateWords()
        }
    }
}