package com.spellingo.client_app

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import kotlinx.coroutines.launch
import java.lang.Integer.min

/**
 * Application-aware ViewModel for the game session screen
 */
class GameSessionViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionModel = SessionModel(application)
    private val pronunciationModel = PronunciationModel(application)
    private val _wordLiveData = MutableLiveData<Word>()
    private val listOfCorrectWords = mutableListOf<String>()
    private val listOfInCorrectWords = mutableListOf<String>()
    private var _strategyChoice = GameStrategy.STANDARD
    private var strategy: GameSessionStrategy = StandardGameStrategy(application)
    private var hintNum = 0
    private var hintSeq = listOf<Int>()
    private var hintCeil = 2
    private var hintBuilder = StringBuilder("")
    private val applicationCopy = application // avoid ViewModel override shenanigans
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    var previousDestination = 0
    private var category = "standard"
    private var _difficulty = Difficulty.OTHER
//    private var results: MutableList<Pair<String, Int>> = mutableListOf()
    val wordLiveData: LiveData<Word>
        get() = _wordLiveData.map { word ->
            val id = word.id
            val newUsage = word.usage.replace("[$id]", "_____")
            val newDefinition = word.definition.replace(id, "_____")
            word.copy(usage = newUsage, definition = newDefinition)
        }
    val pronunciationLiveData = _wordLiveData.switchMap { word ->
        val url = word.audio
        pronunciationModel.getPlayer(url)
    }
    val submitLiveData = MutableLiveData<String>()
    val colorLiveData = MutableLiveData<String>()
    val showStats: Boolean
        get() = sharedPreferences.getBoolean("show_statistics", true)
    val listOfWords = mutableListOf<Word>()
    val strategyChoice: GameStrategy
        get() = _strategyChoice
    val difficulty: Difficulty
        get() = _difficulty

    /**
     * Set strategy for ViewModel functions
     * @param GameStrategy to use
     */
    fun updateStrategy(selected: GameStrategy) {
        if(selected == _strategyChoice) return
        this.strategy = when(selected) {
            GameStrategy.STANDARD -> StandardGameStrategy(applicationCopy)
            GameStrategy.WOTD -> WordOfTheDayStrategy(applicationCopy)
        }
        _strategyChoice = selected
    }

    /**
     * Load the first word of a new session
     */
    fun startSession(category: String, difficulty: Difficulty, suddenDeathMode: SuddenDeathMode) {
        listOfWords.clear()
        this.category = category
        this._difficulty = difficulty
        viewModelScope.launch {
            try {
                strategy.getSessionWords(_wordLiveData, category, difficulty)
                sessionModel.getNewSession()
            }
            catch(e: Exception) {
                Toast.makeText(applicationCopy, "Failed to get words", Toast.LENGTH_SHORT).show()
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
        hintNum = 0
        return strategy.nextWord(_wordLiveData)
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
        viewModelScope.launch {
            try {
                strategy.updateResults(word, result)
                sessionModel.addToCurrentSession(word, result)
            }
            catch(e: Exception) {
                System.err.println(e.printStackTrace())
                System.err.println(e.toString())
            }
        }
    }

    /**
     * Run post session updates
     */
    fun postSessionUpdate() {
        viewModelScope.launch {
            try {
                strategy.postSessionUpdate(category, _difficulty)
            }
            catch(e: Exception) {
                System.err.println(e.printStackTrace())
                System.err.println(e.toString())
            }
        }
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
