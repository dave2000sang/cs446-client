package com.spellingo.client_app

import android.app.Application
import androidx.preference.PreferenceManager
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
    private val attemptCeil = 10
    private var numToDownload = 0
    private val retries = 20
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    var category = "standard" //TODO replace with ViewModel info
    var difficulty = Difficulty.OTHER //TODO replace with ViewModel info

    // See UpdateModel for signature
    override suspend fun purgeReusedWords() {
        val wordDao = wordDb.wordDao()
        val histDao = histDb.historyDao()
        // Get existing words of the finished session from history
        val histList = mutableListOf<History>()
        for(word in wordList) {
            val hist = histDao.getExisting(word.id, word.locale, word.category)
            if(hist != null) {
                histList.add(hist)
            }
        }

        // Get words from session that have been attempted lots of times previously
        val purgeSet = histList.filter { it.total > attemptCeil }.map { it.id }.toHashSet()
        if(purgeSet.size <= 0) return

        // Delete those high attempted words from cache
        wordDao.delete(*wordList.filter {
            purgeSet.contains(it.id)
        }.toTypedArray())

        // Request to download same size of words as just purged
        numToDownload = purgeSet.size
    }

    // See UpdateModel for signature
    override suspend fun downloadWords() {
        val sessionNum = sharedPreferences.getInt("number_words_per_sessions", 10)
        val localeString = sharedPreferences.getString("locale_preferences", "us")
        val locale = Locale.getByName(localeString!!)
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
    }
}