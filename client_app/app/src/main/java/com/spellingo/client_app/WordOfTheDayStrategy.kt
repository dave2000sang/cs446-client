package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.MutableLiveData

class WordOfTheDayStrategy(application: Application) : GameSessionStrategy {
    private val wordOfTheDayModel = WordOfTheDayModel(application)

    override suspend fun getSessionWords(
        wordLiveData: MutableLiveData<Word>,
        category: String,
        difficulty: Difficulty
    ) {
        // Get word from server
        val wotd = wordOfTheDayModel.getWord(category)
        wordLiveData.postValue(wotd)
    }

    override fun nextWord(wordLiveData: MutableLiveData<Word>): Int {
        return 0
    }

    override suspend fun updateResults(word: String, result: Boolean) {
        // Commit to global history
        wordOfTheDayModel.saveResult(word, result)
    }

    override suspend fun postSessionUpdate(category: String, difficulty: Difficulty) {
        // Do nothing
    }
}