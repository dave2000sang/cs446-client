package com.spellingo.client_app

import android.app.Application
import android.media.MediaPlayer
import android.widget.Toast
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import kotlinx.coroutines.launch
import java.lang.Integer.min

/**
 * Application-aware ViewModel for the game session screen
 */
class GameSessionViewModel(application: Application) : DynamicViewModel(application) {
    private val sessionChangeModel = SessionChangeModel(application)
    private val pronunciationModel = PronunciationModel(application)
    private val _wordLiveData = MutableLiveData<Word?>()
    private var _strategyChoice = GameStrategy.STANDARD
    private var strategy: GameSessionStrategy = StandardGameStrategy(application)
    private var hintNum = 0
    private var hintSeq = listOf<Int>()
    private var hintCeil = 2
    private var hintBuilder = StringBuilder("")
    private val applicationCopy = application // avoid ViewModel override shenanigans
    private var _category = "standard"
    private var _difficulty = Difficulty.OTHER
    private var _suddenDeath = SuddenDeathMode.STANDARD

    /**
     * Current [Word], filtered to hide the word's spelling in example and definition
     */
    val wordLiveData: LiveData<Word?>
        get() = _wordLiveData.map { word ->
            if(word == null) {
                null
            }
            else {
                val id = word.id
                val newUsage = word.usage.replace("[$id]", "_____")
                val newDefinition = word.definition.replace(id, "_____")
                word.copy(usage = newUsage, definition = newDefinition)
            }
        }

    /**
     * MediaPlayer for word's pronunciation
     */
    val pronunciationLiveData: LiveData<MediaPlayer?> = _wordLiveData.switchMap { word ->
        if(word == null) {
            MutableLiveData(null)
        }
        else {
            val url = word.audio
            pronunciationModel.getPlayer(url)
        }
    }

    /**
     * Submit button state is stored in ViewModel to survive configuration changes
     */
    val submitLiveData = MutableLiveData<String?>()

    /**
     * Submit color state is stored in ViewModel to survive configuration changes
     */
    val colorLiveData = MutableLiveData<String?>()

    /**
     * [GameStrategy] for deciding what mode of play we use (e.g. word of the day)
     */
    val strategyChoice: GameStrategy
        get() = _strategyChoice

    /**
     * Current session's word and game difficulty
     */
    val difficulty: Difficulty
        get() = _difficulty

    /**
     * Current session's word category
     */
    val category: String
        get() = _category

    /**
     * Current session's [SuddenDeathMode]
     */
    val suddenDeath: SuddenDeathMode
        get() = _suddenDeath

    /**
     * Set strategy for ViewModel functions
     * Pushes to [strategyChoice]
     * @param selected GameStrategy to use
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
     * Pushes to [category], [difficulty], [suddenDeath], [wordLiveData]
     * @param category word category
     * @param difficulty word difficulty
     * @param suddenDeath whether current session is playing sudden death
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
                    sessionChangeModel.getNewSession(category, difficulty)
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
     * Pushes to [wordLiveData]
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
                strategy.updateResults(word, category, attempt == word)
                sessionChangeModel.addToCurrentSession(word, attempt)
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
     * @return list of word-attempt pairs
     */
    fun getSessionResults(): List<Pair<String, String>> {
        return sessionChangeModel.listOfWords
    }

    /**
     * Reset LiveData
     */
    override fun resetLiveData() {
        _wordLiveData.value = null
        submitLiveData.value = null
        colorLiveData.value = null
    }

    /**
     * Cleanup model objects
     */
    override fun onCleared() {
        pronunciationModel.cleanup()
    }
}
