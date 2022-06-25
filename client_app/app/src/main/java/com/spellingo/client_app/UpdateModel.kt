package com.spellingo.client_app

import android.app.Application
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
        val response = "{\"results\":[{\"audio\":\"verb\",\"definition\":\"adjust\",\"origin\":\"Iadjustmapants\",\"part\":\"English\",\"score\":\"adjust01\",\"total\":\"0\",\"usage\":\"to change\"},{\"audio\":\"adjective\",\"definition\":\"alive\",\"origin\":\"I wish tobealive\",\"part\":\"Philosophy\",\"score\":\"alive001\",\"total\":\"0\",\"usage\":\"notdedge\"},{\"audio\":\"verb\",\"definition\":\"undertake\",\"origin\":\"Theundertaker!\",\"part\":\"WWE\",\"score\":\"undert01\",\"total\":\"0\",\"usage\":\"togodo\"},{\"audio\":\"Earthsciences\",\"definition\":\"magnitude\",\"origin\":\"extremity\",\"part\":\"Magnitude5quake\",\"score\":\"noun\",\"total\":\"magnit01\",\"usage\":\"scale\"},{\"audio\":\"noun\",\"definition\":\"end\",\"origin\":\"it.end()\",\"part\":\"C++\",\"score\":\"end00001\",\"total\":\"0\",\"usage\":\"Iterator'slastposition\"},{\"audio\":\"verb\",\"definition\":\"gossip\",\"origin\":\"OMGarey'allgossipping?\",\"part\":\"Highschool\",\"score\":\"gossip01\",\"total\":\"0\",\"usage\":\"talkdrama\"},{\"audio\":\"noun\",\"definition\":\"seal\",\"origin\":\"ARFARFARF\",\"part\":\"Northpole\",\"score\":\"seal0001\",\"total\":\"0\",\"usage\":\"adorablehydrodynamicfatboi\"},{\"audio\":\"verb\",\"definition\":\"ferry\",\"origin\":\"CharonferriedmeacrossRiverStyx\",\"part\":\"Greekmythology\",\"score\":\"ferry001\",\"total\":\"0\",\"usage\":\"totransport\"},{\"audio\":\"GenshinImpact\",\"definition\":\"penetrate\",\"origin\":\"bypass\",\"part\":\"YoucannotpenetratemyZhonglishield\",\"score\":\"verb\",\"total\":\"penetr06\",\"usage\":\"topierce\"}]}"
        try {
            
        }

        val wordList = mutableListOf<Word>()

        //dao
        val dao = wordDb.wordDao()
        dao.clear()
        dao.insert(*wordList.toTypedArray())


        try {
            val responseJson = JSONObject(response)
            val listWords: Array<Any> = arrayOf(responseJson["results"])
            println("******$listWords")
//            val wordList = mutableListOf<Word>()
//            for (word in listWords)


        }









//        val dao = wordDb.wordDao()
//        dao.clear()
//
//        wordList.add(Word("adjust", "to change", "I adjust ma pants",
//            "English", "verb", "adjust01", 0, 0))
//        wordList.add(Word("alive", "not dedge", "I wish to be alive",
//            "Philosophy", "adjective", "alive001", 0, 0))
//        wordList.add(Word("undertake", "to go do", "The undertaker!",
//            "WWE", "verb", "undert01", 0, 0))
//        wordList.add(Word("magnitude", "scale, extremity", "Magnitude 5 quake",
//            "Earth sciences", "noun", "magnit01", 0, 0))
//        wordList.add(Word("end", "Iterator's last position",
//            "it.end()", "C++", "noun", "end00001",0, 0))
//        wordList.add(Word("gossip", "talk drama", "OMG are y'all gossipping?",
//            "High school", "verb", "gossip01", 0, 0))
//        wordList.add(Word("seal", "adorable hydrodynamic fat boi",
//            "ARF ARF ARF", "North pole", "noun", "seal0001", 0, 0))
//        wordList.add(Word("ferry", "to transport",
//            "Charon ferried me across River Styx", "Greek mythology",
//            "verb", "ferry001", 0, 0))
//        wordList.add(Word("embarrassment", "kyaa minaide senpai ~uwu",
//            "Embarrassment is kawaii", "Land of the rising sun",
//            "noun", "embarr06", 0, 0))
//        wordList.add(Word("penetrate", "to pierce, bypass",
//            "You cannot penetrate my Zhongli shield", "Genshin Impact",
//            "verb", "penetr06", 0, 0))
//        dao.insert(*wordList.toTypedArray())
    }
}