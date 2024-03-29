package com.spellingo.client_app

import android.app.Application
import androidx.preference.PreferenceManager
import org.json.JSONObject

/**
 * Model for fetching Word of the Day
 */
class WordOfTheDayModel(application: Application) {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    private val histDb = HistoryDatabase.getInstance(application)

    /**
     * Get Word of the Day from server
     * @param category word category
     * @return word of the day
     */
    suspend fun getWord(category: String): Word {
        val localeString = sharedPreferences.getString("locale_preferences", "us")
        val locale = Locale.getByName(localeString!!)
        val response = HttpRequest().getWotd()
        // Parse JSON object
        val responseJson = JSONObject(response)
        val wordObj = responseJson.getJSONObject("results")
        val id = wordObj.getString("word")
        val definition = wordObj.getString("definition")
        val origin = wordObj.getString("origin")
        val part = wordObj.getString("part")
        val audio = wordObj.getString("audio")
        val usage = wordObj.getString("usage")
        val phonetic = wordObj.getString("phoneticSpelling")
        // Check for empty fields in response
        if((id.isEmpty() || definition.isEmpty()) || origin.isEmpty() || part.isEmpty() ||
            audio.isEmpty() || usage.isEmpty())
        {
            throw Exception("Empty word field")
        }
        return Word(id, locale, category, definition, usage, origin, part, audio, Difficulty.OTHER, phonetic)
    }

    /**
     * Save result in history database
     * @param word new word to enter
     * @param category word category
     * @param result true if and only if the word was spelled correctly
     */
    suspend fun saveResult(word: String, category: String, result: Boolean) {
        val localeString = sharedPreferences.getString("locale_preferences", "us")
        val locale = Locale.getByName(localeString!!)
        val successInt = if(result) 1 else 0
        histDb.historyDao().insert(History(word, locale, category, Difficulty.OTHER, successInt, 1))
    }
}