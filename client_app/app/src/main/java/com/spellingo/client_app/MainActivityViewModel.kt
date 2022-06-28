package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val model = UpdateModel(application)
    fun startApp() {
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