package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel

/**
 * Abstract class to define support for dynamic data behaviour in MVVM
 */
abstract class DynamicViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * Fragment id stored in ViewModel to remember the last fragment visited
     */
    var previousDestination: Int = 0

    /**
     * Reset all LiveData
     */
    abstract fun resetLiveData()
}