package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GameSelectionViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryModel = WordCategoryModel(application)
    private val _categoryLiveData = MutableLiveData<List<String>>()
    val categoryLiveData: LiveData<List<String>>
        get() = _categoryLiveData

    /**
     * Get categories for selection screen
     */
    fun getCategories(isStandard: Boolean) {
        viewModelScope.launch {
            try {
                if(isStandard) {
                    _categoryLiveData.postValue(listOf("Easy", "Medium", "Hard"))
                }
                else {
                    _categoryLiveData.postValue(categoryModel.getCategories())
                }
            }
            catch(e: Exception) {
                System.err.println(e.printStackTrace())
                System.err.println(e.toString())
            }
        }
    }

}