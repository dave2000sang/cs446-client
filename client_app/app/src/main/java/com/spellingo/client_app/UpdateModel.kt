package com.spellingo.client_app

import android.app.Application

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
        val dao = wordDb.wordDao()
        dao.clear()
        val wordList = mutableListOf<Word>()
        wordList.add(Word("adjust", "to change", "I adjust ma pants",
            "English", "verb", "adjust01", 0, 0))
        wordList.add(Word("alive", "not dedge", "I wish to be alive",
            "Philosophy", "adjective", "alive001", 0, 0))
        wordList.add(Word("undertake", "to go do", "The undertaker!",
            "WWE", "verb", "undert01", 0, 0))
        wordList.add(Word("magnitude", "scale, extremity", "Magnitude 5 quake",
            "Earth sciences", "noun", "magnit01", 0, 0))
        wordList.add(Word("end", "Iterator's last position",
            "it.end()", "C++", "noun", "end00001",0, 0))
        wordList.add(Word("gossip", "talk drama", "OMG are y'all gossipping?",
            "High school", "verb", "gossip01", 0, 0))
        wordList.add(Word("seal", "adorable hydrodynamic fat boi",
            "ARF ARF ARF", "North pole", "noun", "seal0001", 0, 0))
        wordList.add(Word("ferry", "to transport",
            "Charon ferried me across River Styx", "Greek mythology",
            "verb", "ferry001", 0, 0))
        wordList.add(Word("embarrassment", "kyaa minaide senpai ~uwu",
            "Embarrassment is kawaii", "Land of the rising sun",
            "noun", "embarr06", 0, 0))
        wordList.add(Word("penetrate", "to pierce, bypass",
            "You cannot penetrate my Zhongli shield", "Genshin Impact",
            "verb", "penetr06", 0, 0))
        dao.insert(*wordList.toTypedArray())
    }
}