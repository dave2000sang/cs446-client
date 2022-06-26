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

    /**
     * Cursed database population for the demo
     * FIXME replace with server HTTP requests
     */
    suspend fun generateWords() {
//        val response = "{\"results\":[{\"audio\":\"verb\",\"definition\":\"adjust\",\"origin\":\"Iadjustmapants\",\"part\":\"English\",\"score\":\"adjust01\",\"total\":\"0\",\"usage\":\"to change\"},{\"audio\":\"adjective\",\"definition\":\"alive\",\"origin\":\"I wish tobealive\",\"part\":\"Philosophy\",\"score\":\"alive001\",\"total\":\"0\",\"usage\":\"notdedge\"},{\"audio\":\"verb\",\"definition\":\"undertake\",\"origin\":\"Theundertaker!\",\"part\":\"WWE\",\"score\":\"undert01\",\"total\":\"0\",\"usage\":\"togodo\"},{\"audio\":\"Earthsciences\",\"definition\":\"magnitude\",\"origin\":\"extremity\",\"part\":\"Magnitude5quake\",\"score\":\"noun\",\"total\":\"magnit01\",\"usage\":\"scale\"},{\"audio\":\"noun\",\"definition\":\"end\",\"origin\":\"it.end()\",\"part\":\"C++\",\"score\":\"end00001\",\"total\":\"0\",\"usage\":\"Iterator'slastposition\"},{\"audio\":\"verb\",\"definition\":\"gossip\",\"origin\":\"OMGarey'allgossipping?\",\"part\":\"Highschool\",\"score\":\"gossip01\",\"total\":\"0\",\"usage\":\"talkdrama\"},{\"audio\":\"noun\",\"definition\":\"seal\",\"origin\":\"ARFARFARF\",\"part\":\"Northpole\",\"score\":\"seal0001\",\"total\":\"0\",\"usage\":\"adorablehydrodynamicfatboi\"},{\"audio\":\"verb\",\"definition\":\"ferry\",\"origin\":\"CharonferriedmeacrossRiverStyx\",\"part\":\"Greekmythology\",\"score\":\"ferry001\",\"total\":\"0\",\"usage\":\"totransport\"},{\"audio\":\"GenshinImpact\",\"definition\":\"penetrate\",\"origin\":\"bypass\",\"part\":\"YoucannotpenetratemyZhonglishield\",\"score\":\"verb\",\"total\":\"penetr06\",\"usage\":\"topierce\"}]}"
        val wordList = mutableListOf<Word>()

        //dao
        val dao = wordDb.wordDao()
        dao.clear()

        try {
            val responseJson = JSONObject(response)
            val listWords = responseJson.getJSONArray("results")
            for(wordIdx in 0..listWords.length()) {
                val wordObj = listWords.getJSONObject(wordIdx)
                val id = wordObj.getString("audio")
                println(id)
            }
        }
        catch(e: JSONException) {
            println(e.stackTrace)
        }

        dao.insert(*wordList.toTypedArray())
    }
}