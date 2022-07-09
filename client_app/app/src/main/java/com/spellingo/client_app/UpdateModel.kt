package com.spellingo.client_app

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import org.json.JSONException
import org.json.JSONObject

/**
 * Model for updating/populating the local word database
 * @param application ApplicationContext for database creation
 */
abstract class UpdateModel(private val application: Application) {
    private val locale = "uk" //TODO replace with setting
    private val connectivity = ContextCompat.getSystemService(
        application.applicationContext,
        ConnectivityManager::class.java
    )
    protected val wordDb = WordDatabase.getInstance(application)
    protected val histDb = HistoryDatabase.getInstance(application)

    /**
     * Try to fetch words from server
     * @param limit number of words to request from server
     * @param locale English locale
     * @return number of words fetched
     */
    protected suspend fun tryFetchWords(limit: Int, locale: String): Int {
        // Disallow any bugs where limit is negative
        if(limit <= 0) return 0

        val wordList = mutableListOf<Word>()

        // HTTP request
        val response = HttpRequest().getWords(limit, locale)

        try {
            // Parse JSON object
            val responseJson = JSONObject(response)
            val listWords = responseJson.getJSONArray("results")
            for(wordIdx in 0 until listWords.length()) {
                val wordObj = listWords.getJSONObject(wordIdx)
                val id = wordObj.getString("word")
                val definition = wordObj.getString("definition")
                val origin = wordObj.getString("origin")
                val part = wordObj.getString("part")
                val audio = wordObj.getString("audio")
                val usage = wordObj.getString("usage")
                // Check for empty fields in response
                if(id.isEmpty() || definition.isEmpty() || origin.isEmpty()
                    || part.isEmpty() || audio.isEmpty() || usage.isEmpty()) {
                    throw Exception("Empty word field")
                }
                // Create new Word and History entries
                wordList.add(Word(id, definition, usage, origin, part, audio, locale))
            }
        }
        catch(e: Exception) {
            System.err.println(e.toString())
            return 0
        }

        // Word and History database accessors
        val wordDao = wordDb.wordDao()
        val histDao = histDb.historyDao()

        // Get existing requested words from word history
        val histExisting = histDao.getExisting(wordList.map { it.id } ).map {
            it.id
        }.toHashSet()

        // Get existing words in cache that match id and locale
        val cacheLocalMap = wordDao.getExisting(wordList.map { it.id }).associate {
            it.id to it.locale
        }

        // Remove fetched words if they're in cache with the same locale OR OTHERWISE if they are in history
        wordList.removeAll {
            if(cacheLocalMap.containsKey(it.id)) {
                cacheLocalMap[it.id] == locale
            }
            else {
                histExisting.contains(it.id)
            }
        }

        // Using DAO properties, History ignores dup keys and Word replaces them.
        // This means that words in cache of a different locale are allowed to be downloaded
        if(wordList.isNotEmpty()) {
            wordDao.insert(*wordList.toTypedArray())
            histDao.insert(*wordList.map {
                History(it.id, 0, 0)
            }.toTypedArray())
        }
        println(wordList) // DEBUG

        return wordList.size
    }

    /**
     * Returns whether connection is metered
     * @return True if and only if connection isn't metered (can download)
     */
    protected open fun canDownload(): Boolean {
        if(connectivity == null) {
            return false
        }
        val currentNetwork = connectivity.activeNetwork
        val caps = connectivity.getNetworkCapabilities(currentNetwork)
        return if(caps == null) {
            // default to metered dumb check
            !connectivity.isActiveNetworkMetered
        }
        else {
            // Allow Wifi and Ethernet connections
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }
    }

    /**
     * Download words from server
     * @param locale English locale
     * @return number of words downloaded
     */
    protected abstract suspend fun downloadWords(locale: String): Int

    /**
     * Purge some words from local cache
     * @return number of words purged
     */
    protected abstract suspend fun purgeReusedWords(): Int

    /**
     * Fetch words from the server
     */
    suspend fun generateWords() {
        if(canDownload()) {
            purgeReusedWords()
            downloadWords(locale)
        }
    }
}