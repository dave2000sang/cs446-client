package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

/**
 * Application-aware ViewModel for the game session screen
 */
class GameSessionViewModel(application: Application) : AndroidViewModel(application) {
    private val wordModel = WordModel(application)
    private val pronunciationModel = PronunciationModel()
    private val histModel = HistoryChangeModel(application)
    private val _wordLiveData = MutableLiveData<Word>()
    private var hintNum = 0
    private var hintSeq = listOf<Int>()
    private var hintCeil = 2 //TODO replace with global preference?
    var previousDestination = 0
//    private var results: MutableList<Pair<String, Int>> = mutableListOf()
    val wordLiveData: LiveData<Word>
        get() = _wordLiveData.map { word ->
            val id = word.id
            val newUsage = word.usage.replace("[$id]", "_____")
            word.copy(usage = newUsage)
        }
    val pronunciationLiveData = _wordLiveData.switchMap { word ->
        val url = word.audio
        pronunciationModel.getPlayer(url)
    }

    /**
     * Load the first word of a new session
     */
    fun startSession() {
        viewModelScope.launch {
            try {
                val curWord = wordModel.getNewSessionWords()
                _wordLiveData.postValue(curWord)
            }
            catch(e: Exception) {
                System.err.println(e.toString())
            }
        }
    }

    /**
     * Get next word, refresh state, and get number of remaining words in session
     * @return number of words left in session
     */
    fun nextWord(): Int {
        val curWord = wordModel.getWord() ?: return 0
        _wordLiveData.value = curWord
        hintNum = 0
        return wordModel.numSessionWords()
    }

    /**
     * Get hint text
     * @return hint
     */
    fun getHint(): String {
        if(_wordLiveData.value == null) {
            return ""
        }
        val curText = _wordLiveData.value!!.id
        val hintText = StringBuilder("_ ".repeat(curText.length))
        if(hintNum == 0) { // first time getting hint for this word
            // randomly order indices to get hint char
            hintSeq = curText.indices.toList().shuffled()
        }
        for(i in 0 until listOf(hintNum, hintCeil, curText.length).min()) {
            // reveal char at random indices
            hintText.setCharAt(hintSeq[i] * 2, curText[hintSeq[i]])
        }
        hintNum++
        return hintText.toString()
    }

    /**
     * Update results list
     * @param word word that was just played
     * @param result whether the spelling was correct
     */
    fun updateResults(word: String, result: Boolean) {
        viewModelScope.launch {
            histModel.update(word, result)
        }
    }

    /**
     * Cleanup model objects
     */
    override fun onCleared() {
        pronunciationModel.cleanup()
    }
}