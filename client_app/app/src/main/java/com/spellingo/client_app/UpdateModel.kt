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
    private val wordDb = WordDatabase.getInstance(application)
    private val httpRequest = HttpRequest.getInstance()
    private val histDb = HistoryDatabase.getInstance(application)
    private val connectivity = getSystemService(application.applicationContext,
        ConnectivityManager::class.java)

    private suspend fun tryFetchWords(num: Int): Int {
        val wordList = mutableListOf<Word>()

        // HTTP request
        val response = httpRequest.getWords()

        //dao
        val dao = wordDb.wordDao()
        dao.clear()

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
                wordList.add(Word(id, definition, usage, origin, part, audio, 0, 0))
            }
        }
        catch(e: JSONException) {
            System.err.println(e.toString())
        }

        dao.insert(*wordList.toTypedArray())

        return 0
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
            tryFetchWords(10)
        }
    }
}