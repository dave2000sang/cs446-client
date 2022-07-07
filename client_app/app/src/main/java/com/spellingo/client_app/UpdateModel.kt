package com.spellingo.client_app

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat.getSystemService
import org.json.JSONException
import org.json.JSONObject

/**
 * Model for updating/populating the local word database
 * @param application ApplicationContext for database creation
 */
class UpdateModel(application: Application) {
    private val RETRIES = 10
    private val localNumber = 10 //TODO setting? MUST be greater than 0
    private val wordDb = WordDatabase.getInstance(application)
    private val httpRequest = HttpRequest.getInstance()
    private val histDb = HistoryDatabase.getInstance(application)
    private val connectivity = getSystemService(application.applicationContext,
        ConnectivityManager::class.java)
    private val locale = "uk" //TODO replace with setting

    /**
     * Try to fetch words from server
     * @param num number of words to request from server
     * @return number of duplicate words received
     */
    private suspend fun tryFetchWords(num: Int): Int {
        val wordList = mutableListOf<Word>()
        val histList = mutableListOf<History>()

        // HTTP request
        val response = httpRequest.getWords(num, locale)

        // Word and History database accessors
        val wordDao = wordDb.wordDao()
        val histDao = histDb.historyDao()

        //TODO error handling for empty fields (e.g. audio)

        try {
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
                wordList.add(Word(id, definition, usage, origin, part, audio, locale))
                histList.add(History(id, 0, 0))
            }
        }
        catch(e: JSONException) {
            System.err.println(e.toString())
            return num
        }

        // Get existing requested words from word history
        val existing = histDao.getExisting(histList.map{it.id})

        // Using DAO properties, History ignores dup keys and Word replaces them.
        // This correct behaviour since locale changes can make smarter logic tricky.

        if(wordList.size > 0 && histList.size > 0) {
            wordDao.insert(*wordList.toTypedArray())
            histDao.insert(*histList.toTypedArray())
        }

        return existing.size
    }

    /**
     * Fetch words from the server
     */
    suspend fun generateWords() {
        if(connectivity == null) {
            return
        }
        val currentNetwork = connectivity.activeNetwork
        val caps = connectivity.getNetworkCapabilities(currentNetwork)
        val doDownload = if(caps == null) {
            // default to metered dumb check
            !connectivity.isActiveNetworkMetered
        }
        else {
            // Allow Wifi and Ethernet connections
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }
        if(doDownload) {
            var remainingNumber = localNumber
            var retryIdx = 0
            while(remainingNumber > 0 && retryIdx < RETRIES) {
                remainingNumber = tryFetchWords(remainingNumber)
                retryIdx++
            }
        }
    }
}