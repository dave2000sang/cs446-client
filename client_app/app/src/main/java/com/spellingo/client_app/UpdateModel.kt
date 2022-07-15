package com.spellingo.client_app

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import org.json.JSONException
import org.json.JSONObject

/**
 * Model for updating/populating the local word database
 * @param application ApplicationContext for database creation
 */
abstract class UpdateModel(private val application: Application) {
    private val connectivity = ContextCompat.getSystemService(
        application.applicationContext,
        ConnectivityManager::class.java
    )
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    protected val wordDb = WordDatabase.getInstance(application)
    protected val histDb = HistoryDatabase.getInstance(application)

    /**
     * Try to fetch words from server
     * @param limit number of words to request from server
     * @param locale English locale
     * @param category word category
     * @param difficulty word difficulty for standard category
     * @return number of words fetched
     */
    protected suspend fun tryFetchWords(limit: Int, locale: Locale, category: String, difficulty: Difficulty): Int {
        // Disallow any bugs where limit is negative
        if(limit <= 0) return 0

        val wordList = mutableListOf<Word>()

        // HTTP request
        val response = HttpRequest().getWords(limit, locale, category, difficulty)

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
                val phonetic = wordObj.getString("phoneticSpelling")
                // Check for empty fields in response
                if((id.isEmpty() || definition.isEmpty()) ||
                    (category == "standard" && (origin.isEmpty() || part.isEmpty() ||
                            audio.isEmpty() || usage.isEmpty())))

                {
                    throw Exception("Empty word field")
                }
                // Create new Word and History entries
                wordList.add(Word(id, locale, category, definition, usage, origin, part, audio, difficulty, phonetic))
            }
        }
        catch(e: Exception) {
            System.err.println(e.printStackTrace())
            System.err.println(e.toString())
            return 0
        }

        // Word and History database accessors
        val wordDao = wordDb.wordDao()
        val histDao = histDb.historyDao()

        // Get existing requested words from word history
        val newWordList = mutableListOf<Word>()
        for(word in wordList) {
            if(histDao.getExisting(word.id, word.locale, word.category) == null) {
                newWordList.add(word)
            }
        }

        if(newWordList.isNotEmpty()) {
            wordDao.insert(*newWordList.toTypedArray())
            histDao.insert(*newWordList.map {
                History(it.id, it.locale, it.category, 0, 0)
            }.toTypedArray())
        }
        println(newWordList) // DEBUG

        return newWordList.size
    }

    /**
     * Returns whether connection is metered
     * @return True if and only if connection isn't metered (can download)
     */
    protected open fun canDownload(): Boolean {
        if(connectivity == null) {
            return false
        }

        val wifiOnly = sharedPreferences.getBoolean("download_on_wifi_only", true)
        if(!wifiOnly) {
            // allow downloads through non-wifi connections
            return true
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
     */
    protected abstract suspend fun downloadWords()

    /**
     * Purge some words from local cache
     */
    protected abstract suspend fun purgeReusedWords()

    /**
     * Fetch words from the server
     */
    suspend fun generateWords() {
        if(canDownload()) {
            purgeReusedWords()
            downloadWords()
        }
    }
}