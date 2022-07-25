package com.spellingo.client_app

import android.app.Application
import org.json.JSONObject

/**
 * Update model for application startup, which allows for slower but larger operations
 */
class InitialUpdateModel(application: Application) : UpdateModel(application) {
    private val purgeAttemptsCeil = 2 // average attempts per word to trigger purge
    private val purgeAmount = 10 // number of words to purge
    private val maxCache = 100 // too many words in cache
    private val minCache = 20 // minimum words to keep in cache
    private val limit = 10 // per-request download limit
    private val retries = 5
    private val categories = mutableSetOf("standard")

    /**
     * Get collection of categories from server
     */
    private suspend fun getCategories() {
        try {
            val response = HttpRequest().getCategories()
            val responseJson = JSONObject(response)
            val listCats = responseJson.getJSONArray("results")
            for(catIdx in 0 until listCats.length()) {
                categories.add(listCats.getString(catIdx))
            }
        }
        catch(e: Exception) {
            System.err.println(e.printStackTrace())
            System.err.println(e.toString())
        }
    }

    /**
     * Run tryFetchWords() until desired number of words downloaded
     * @return number of words downloaded
     */
    private suspend fun downloadCategory(locale: Locale, category: String, difficulty: Difficulty): Int {
        val wordDao = wordDb.wordDao()
        val inCache = wordDao.getKeyedWords(locale, category, difficulty).size
        // If we meet our minimum cache number, don't download
        if(inCache >= minCache) return 0
        var downloaded = 0
        // Try up to retries times to download words to cache
        for(retryIdx in 0 until retries) {
            downloaded += tryFetchWords(limit, locale, category, difficulty)
            if(inCache + downloaded >= minCache) break
        }
        return downloaded
    }

    /**
     * Purge most played words if existing words has been used too much
     */
    override suspend fun purgeReusedWords() {
        val histDao = histDb.historyDao()
        val wordDao = wordDb.wordDao()

        // Purge unused categories
        getCategories()
        for(cat in wordDao.getCategories()) {
            if(!categories.contains(cat)) {
                wordDao.deleteCategory(cat)
            }
        }

        // Purge condition checks if average attempts is high or cache is too large
        val wordCount = histDao.countWords()
        if(wordCount <= 0) return
        val attempts = histDao.getNumTotal()

        if(attempts / wordCount >= purgeAttemptsCeil || wordCount >= maxCache) {
            val topWords = histDao.getTopWords(purgeAmount)
            val wordArray = topWords.map {
                Word(it.id, it.locale, it.category , "", "", "", "", "", Difficulty.OTHER, "")
            }.toTypedArray()
            // Remove most played words from cache
            wordDao.delete(*wordArray)
        }
    }

    /**
     * Download words from server from all categories/locales/difficulties
     */
    override suspend fun downloadWords() {
        // For each locale, download words
        for(loc in Locale.values()) {
            // For each category, download words
            for(cat in categories) {
                // Iterate over difficulty for standard category
                if(cat == "standard") {
                    for(diff in Difficulty.values()) {
                        downloadCategory(loc, "standard", diff)
                    }
                } else {
                    downloadCategory(loc, cat, Difficulty.OTHER)
                }
            }
        }
    }
}