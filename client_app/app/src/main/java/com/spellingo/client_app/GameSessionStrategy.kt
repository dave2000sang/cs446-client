package com.spellingo.client_app

import androidx.lifecycle.MutableLiveData

interface GameSessionStrategy {
    suspend fun getSessionWords(wordLiveData: MutableLiveData<Word>, category: String, difficulty: Difficulty)
    fun nextWord(wordLiveData: MutableLiveData<Word>): Int
    suspend fun updateResults(word: String, result: Boolean)
    suspend fun postSessionUpdate(category: String, difficulty: Difficulty)
}