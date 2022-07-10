package com.spellingo.client_app

import android.app.Application
import java.lang.Integer.max
import java.lang.Integer.min

/**
 * Update model for quick word fetches after session
 * @param wordList list of words from the most recent session
 */
class PostSessionUpdateModel(
    application: Application,
    private val wordList: List<Word>
) : UpdateModel(application) {
    private val attemptCeil = 10 //TODO centralized place?
    private var numToDownload = 0
    private val sessionNum = 10 //TODO link with settings
    private val retries = 20
    var locale = Locale.UK //TODO replace with ViewModel info
    var category = Category.STANDARD //TODO replace with ViewModel info
    var difficulty = Difficulty.MEDIUM //TODO replace with ViewModel info

    // See UpdateModel for signature
    override suspend fun purgeReusedWords(): Int {
        val wordDao = wordDb.wordDao()
        val histDao = histDb.historyDao()
        // Get existing words of the finished session from history
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

    // See UpdateModel for signature
    override suspend fun downloadWords(): Int {
        val wordDao = wordDb.wordDao()
        val inCache = wordDao.getAllWords().size
        var downloaded = 0
        // Only retry if we don't have enough words for a session
        for(retryIdx in 0 until retries) {
            downloaded += tryFetchWords(
                max(numToDownload, sessionNum - (inCache + downloaded)),
                locale,
                category,
                difficulty
            )
            if(inCache + downloaded >= sessionNum) break
        }
        return downloaded
    }
}