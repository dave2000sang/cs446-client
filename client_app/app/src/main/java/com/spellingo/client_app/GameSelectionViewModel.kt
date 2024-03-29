package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GameSelectionViewModel(application: Application) : DynamicViewModel(application) {
    private val categoryModel = WordCategoryModel(application)
    private val _categoryLiveData = MutableLiveData<List<String>?>()

    /**
     * List of categories to select from
     */
    val categoryLiveData: LiveData<List<String>?>
        get() = _categoryLiveData

    /**
     * Get categories for selection screen
     *  [categoryLiveData]
     * @param isStandard true if and only if we want standard category's difficulties
     */
    fun getCategories(isStandard: Boolean) {
        viewModelScope.launch {
            try {
                if(isStandard) {
                    _categoryLiveData.postValue(listOf(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD).map { it.toString().lowercase() })
                }
                else {
                    _categoryLiveData.postValue(categoryModel.getCategories().map { it.lowercase() })
                }
            }
            catch(e: Exception) {
                System.err.println(e.printStackTrace())
                System.err.println(e.toString())
            }
        }
    }

    /**
     * Reset all LiveData
     * Pushes to [categoryLiveData]
     */
    override fun resetLiveData() {
        _categoryLiveData.value = null
    }
}