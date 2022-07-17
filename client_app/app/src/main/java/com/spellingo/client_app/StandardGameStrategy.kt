package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.MutableLiveData
import java.util.Locale

class StandardGameStrategy(private val application: Application) : GameSessionStrategy {
    private val wordModel = WordModel(application)
    private val histModel = HistoryChangeModel(application)

    override suspend fun getSessionWords(wordLiveData: MutableLiveData<Word>, category: String, difficulty: Difficulty) {
        val curWord = wordModel.getNewSessionWords(category, difficulty)
        wordLiveData.postValue(curWord)
    }

    override fun nextWord(wordLiveData: MutableLiveData<Word>): Int {
        val returnNum = wordModel.numSessionWords()
        val curWord = wordModel.getWord() ?: return 0
        wordLiveData.value = curWord
        return returnNum
    }

    override suspend fun updateResults(word: String, result: Boolean) {
        histModel.update(word, result)
    }

    override suspend fun postSessionUpdate(category: String, difficulty: Difficulty) {
        val postSessionUpdateModel = PostSessionUpdateModel(application, wordModel.sessionWords, category, difficulty)
        postSessionUpdateModel.generateWords()
    }
}