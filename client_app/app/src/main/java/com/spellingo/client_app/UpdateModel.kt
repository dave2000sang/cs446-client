package com.spellingo.client_app

import android.app.Application
import org.json.JSONException
import org.json.JSONObject

/**
 * Model for updating/populating the local word database
 * @param application ApplicationContext for database creation
 */
abstract class UpdateModel(private val application: Application) {
    protected val wordDb = WordDatabase.getInstance(application)
    protected val histDb = HistoryDatabase.getInstance(application)

    /**
     * Try to fetch words from server
     * @param limit number of words to request from server
     * @param locale English locale
     * @return number of duplicate words received
     */
    protected suspend fun tryFetchWords(limit: Int, locale: String): Int {
        val wordList = mutableListOf<Word>()
        val histList = mutableListOf<History>()

        // HTTP request
        val response = HttpRequest().getWords(limit, locale)

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
            return limit
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

    abstract suspend fun downloadWords()
    abstract suspend fun purgeReusedWords()

    /**
     * Fetch words from the server
     */
    suspend fun generateWords() {
        purgeReusedWords()
        downloadWords()
    }
}