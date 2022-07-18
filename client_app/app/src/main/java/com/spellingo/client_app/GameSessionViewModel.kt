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
    private var _strategyChoice = GameStrategy.STANDARD
    private var strategy: GameSessionStrategy = StandardGameStrategy(application)
    private var hintNum = 0
    private var hintSeq = listOf<Int>()
    private var hintCeil = 2
    private var hintBuilder = StringBuilder("")
    private val applicationCopy = application // avoid ViewModel override shenanigans
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    private var _category = "standard"
    private var _difficulty = Difficulty.OTHER
    private var _suddenDeath = SuddenDeathMode.STANDARD
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
    val strategyChoice: GameStrategy
        get() = _strategyChoice
    val difficulty: Difficulty
        get() = _difficulty
    val category: String
        get() = _category
    val suddenDeath: SuddenDeathMode
        get() = _suddenDeath
    var previousDestination = 0

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
    fun startSession(category: String, difficulty: Difficulty, suddenDeath: SuddenDeathMode) {
        this._category = category
        this._difficulty = difficulty
        this._suddenDeath = suddenDeath
        viewModelScope.launch {
            try {
                // Get new words for session
                strategy.getSessionWords(_wordLiveData, category, difficulty)
                // Create new session if we're not continuing a sudden death session
                if(suddenDeath != SuddenDeathMode.SD_CONTINUE) {
                    sessionModel.getNewSession(category, difficulty)
                }
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
     * @param attempt the attempted spelling
     */
    fun updateResults(word: String, attempt: String) {
        viewModelScope.launch {
            try {
                strategy.updateResults(word, attempt == word)
                sessionModel.addToCurrentSession(word, attempt)
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
                strategy.postSessionUpdate(_category, _difficulty)
            }
            catch(e: Exception) {
                System.err.println(e.printStackTrace())
                System.err.println(e.toString())
            }
        }
    }

    /**
     * Get session words and the attempted spelling
     * TODO Nathan should use this in PostGameStatisticsFragment
     * @return list of word-attempt pairs
     */
    fun getSessionResults(): List<Pair<String, String>> {
        return sessionModel.listOfWords
    }

    /**
     * Cleanup model objects
     */
    override fun onCleared() {
        pronunciationModel.cleanup()
    }
}
