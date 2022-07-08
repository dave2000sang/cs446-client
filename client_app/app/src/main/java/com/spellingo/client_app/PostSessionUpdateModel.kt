package com.spellingo.client_app

import android.app.Application
import java.lang.Integer.max
import java.lang.Integer.min

/**
 * Update model for quick word fetches after session
 */
class PostSessionUpdateModel(
    application: Application,
    private val wordList: List<Word>
) : UpdateModel(application) {
    private val attemptCeil = 10 //TODO centralized place?
    private var numToDownload = 0
    private val sessionNum = 10 //TODO link with settings
    private val retries = 20

    override suspend fun purgeReusedWords(): Int {
        val wordDao = wordDb.wordDao()
        val histDao = histDb.historyDao()
        val histList = histDao.getExisting(wordList.map {it.id})
        // Get words from session that have been attempted lots of times previously
        val purgeSet = histList.filter { it.total > attemptCeil }.map { it.id }.toHashSet()
        if(purgeSet.size <= 0) return 0
        // Delete those high attempted words from cache
        wordDao.delete(*wordList.filter {
            purgeSet.contains(it.id)
        }.toTypedArray())
        // Request to download same size of words as just purged
        numToDownload = purgeSet.size
        return purgeSet.size
    }

    override suspend fun downloadWords(locale: String): Int {
        val wordDao = wordDb.wordDao()
        val inCache = wordDao.getAllWords().size
        var downloaded = 0
        // Only retry if we don't have enough words for a session
        for(retryIdx in 0 until retries) {
            downloaded += tryFetchWords(max(numToDownload, sessionNum - (inCache + downloaded)), locale)
            if(inCache + downloaded >= sessionNum) break
        }
        return downloaded
    }
}