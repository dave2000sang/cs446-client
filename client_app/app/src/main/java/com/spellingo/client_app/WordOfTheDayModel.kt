package com.spellingo.client_app

import android.app.Application
import androidx.preference.PreferenceManager
import org.json.JSONObject

class WordOfTheDayModel(application: Application) {
    private val wotdCategory = "Word of the Day"
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    private val histDb = HistoryDatabase.getInstance(application)

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
        return Word(id, locale, wotdCategory, definition, usage, origin, part, audio, Difficulty.OTHER, phonetic)
    }

    suspend fun saveResult(word: String, result: Boolean) {
        val localeString = sharedPreferences.getString("locale_preferences", "us")
        val locale = Locale.getByName(localeString!!)
        val successInt = if(result) 1 else 0
        histDb.historyDao().insert(History(word, locale, wotdCategory, successInt, 1))
    }
}