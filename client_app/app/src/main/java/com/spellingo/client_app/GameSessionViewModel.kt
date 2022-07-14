package com.spellingo.client_app

import android.app.Application
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import kotlinx.coroutines.launch
import java.lang.Integer.min

/**
 * Application-aware ViewModel for the game session screen
 */
class GameSessionViewModel(application: Application) : AndroidViewModel(application) {
    private val wordModel = WordModel(application)
    private val pronunciationModel = PronunciationModel()
    private val histModel = HistoryChangeModel(application)
    private val sessionModel = SessionModel(application)
    private val _wordLiveData = MutableLiveData<Word>()
    private val _categoryLiveData = MutableLiveData<List<String>>()
    private val listOfCorrectWords = mutableListOf<String>()
    private val listOfInCorrectWords = mutableListOf<String>()
    private var hintNum = 0
    private var hintSeq = listOf<Int>()
    private var hintCeil = 2
    private var hintBuilder = StringBuilder("")
    private val applicationCopy = application // avoid ViewModel override shenanigans
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    var previousDestination = 0
    //TODO modify these from the category selection screen
    var category = "standard"
    var difficulty = Difficulty.OTHER
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
    val categoryLiveData: LiveData<List<String>>
        get() = _categoryLiveData
    val showStats: Boolean
        get() = sharedPreferences.getBoolean("show_statistics", true)

    /**
     * Load the first word of a new session
     */
    fun startSession() {
        viewModelScope.launch {
            try {
                val curWord = wordModel.getNewSessionWords(category, difficulty)
                // sessionModel.getNewSession()
                _wordLiveData.postValue(curWord)
                _categoryLiveData.postValue(wordModel.getCategories())
            }
            catch(e: Exception) {
                System.err.println(e.printStackTrace())
                System.err.println(e.toString())
            }
        }
    }

    /**
     * Get next word, refresh state, and get number of remaining words in session
     * @return number of words left in session
     */
    fun nextWord(): Int {
        val returnNum = wordModel.numSessionWords()
        val curWord = wordModel.getWord() ?: return 0
        _wordLiveData.value = curWord
        hintNum = 0
        return returnNum
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
        if(hintNum == 0) { // first time getting hint for this word
            // randomly order indices to get hint char
            hintBuilder = StringBuilder("_ ".repeat(curText.length))
            hintSeq = curText.indices.toList().shuffled()
        }
        else if(hintNum - 1 < min(hintCeil, curText.length / 2)) {
            hintBuilder.setCharAt(hintSeq[hintNum - 1] * 2, curText[hintSeq[hintNum - 1]])
        }
        hintNum++
        return hintBuilder.toString()
    }

    /**
     * Update results list
     * @param word word that was just played
     * @param result whether the spelling was correct
     */
    fun updateResults(word: String, result: Boolean) {
//        sessionModel.addToCurrentSession(word, result)
        viewModelScope.launch {
            histModel.update(word, result)
        }
    }

    /**
     * Run post session updates
     */
    fun postSessionUpdate() {
        val postSessionUpdateModel = PostSessionUpdateModel(applicationCopy, wordModel.sessionWords)
        viewModelScope.launch {
            postSessionUpdateModel.generateWords()
        }
    }

    /**
     * Save current session to local dB, called by onDestroyView
     */
    fun saveSession() {
        viewModelScope.launch {
            sessionModel.saveCurrentSession()
        }
    }

    fun getListOfWords (): List<Word> {
        return wordModel.sessionWords
    }

    fun addCorrectWord (word: String) {
        listOfCorrectWords.add(word)
    }

    fun addInCorrectWord (word: String) {
        listOfInCorrectWords.add(word)
    }

    fun getCorrectWordList (): MutableList<String> {
        return listOfCorrectWords
    }

    fun getInCorrectWordList (): MutableList<String> {
        return listOfInCorrectWords
    }

    fun emptyCorrectWordList () {
        listOfCorrectWords.clear()
    }

    fun emptyInCorrectWordList ()  {
        listOfInCorrectWords.clear()
    }

    /**
     * Cleanup model objects
     */
    override fun onCleared() {
        pronunciationModel.cleanup()
    }
}
