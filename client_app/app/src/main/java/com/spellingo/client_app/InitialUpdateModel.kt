package com.spellingo.client_app

import android.app.Application

/**
 * Update model for application startup, which allows for slower but larger operations
 */
class InitialUpdateModel(application: Application) : UpdateModel(application) {
    private val purgeAttemptsCeil = 5 //TODO store in more centralized place
    private val purgeCountCeil = 100 //TODO store in more centralized place
    private val purgeAmount = 10 //TODO store in more centralized place
    private val minCache = 10 //TODO store somewhere? (min num words in cache desired)
    private val limit = 10 //TODO store somewhere? (download chunk size, be generous)
    private val retries = 5

    // See UpdateModel for signature
    override suspend fun purgeReusedWords(): Int {
        val histDao = histDb.historyDao()
        val wordDao = wordDb.wordDao()
        val wordCount = histDao.getAllWords().size
        if(wordCount <= 0) return 0
        val attempts = histDao.getNumTotal()

        // Purge condition checks if average attempts is high or cache is too large
        if(attempts / wordCount >= purgeAttemptsCeil || wordCount >= purgeCountCeil) {
            val topWords = histDao.getTopWords(purgeAmount)
            val wordArray = topWords.map {
                Word(it.id, "", "", "", "", "", "")
            }.toTypedArray()
            // Remove most played words from cache
            wordDao.delete(*wordArray)
            println("DEBUG purge $wordArray") // DEBUG
            return wordArray.size
        }
        return 0
    }

    // See UpdateModel for signature
    override suspend fun downloadWords(locale: String): Int {
        val wordDao = wordDb.wordDao()
        val inCache = wordDao.getAllWords().size
        // If we meet our minimum cache number, don't download
        if(inCache >= minCache) return 0
        var downloaded = 0
        // Try up to retries times to download words to cache
        for(retryIdx in 0 until retries) {
            downloaded += tryFetchWords(limit, locale)
            if(inCache + downloaded >= minCache) break
        }
        println("DEBUG downloadWords $downloaded") // DEBUG
        return downloaded
    }
}