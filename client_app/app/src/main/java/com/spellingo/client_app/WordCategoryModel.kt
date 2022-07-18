package com.spellingo.client_app

import android.app.Application

class WordCategoryModel(application: Application) {
    private val wordDb = WordDatabase.getInstance(application)

    /**
     * Get word categories
     * @return list of word categories supported in cache (excluding standard)
     */
    suspend fun getCategories(): List<String> {
        return wordDb.wordDao().getCategories().filter {
            it != "standard"
        }.sorted()
    }
}