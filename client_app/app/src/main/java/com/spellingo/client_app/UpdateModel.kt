package com.spellingo.client_app

import android.app.Application
import org.json.JSONException
import org.json.JSONObject

/**
 * Model for updating/populating the local word database
 * @param application ApplicationContext for database creation
 */
class UpdateModel(application: Application) {
    private val wordDb = WordDatabase.getInstance(application)
    private val httpRequest = HttpRequest.getInstance()

    /**
     * Fetch words from the server
     */
    suspend fun generateWords() {
        val wordList = mutableListOf<Word>()

        // HTTP request
        val response = httpRequest.getWords()

        //dao
        val dao = wordDb.wordDao()
        dao.clear()

        try {
            val responseJson = JSONObject(response)
            val listWords = responseJson.getJSONArray("results")
            for(wordIdx in 0 until listWords.length()) {
                val wordObj = listWords.getJSONObject(wordIdx)
                val id = wordObj.getString("id")
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
    }
}