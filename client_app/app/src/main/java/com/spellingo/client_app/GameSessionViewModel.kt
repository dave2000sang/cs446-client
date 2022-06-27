package com.spellingo.client_app

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.*
import kotlinx.coroutines.launch

/**
 * Application-aware ViewModel for the game session screen
 */
class GameSessionViewModel(application: Application) : AndroidViewModel(application) {
    private val wordModel = WordModel(application)
    private val pronunciationModel = PronunciationModel()
    private val _wordLiveData = MutableLiveData<Word>()
    val wordLiveData: LiveData<Word>
        get() = _wordLiveData
    val pronunciationLiveData = _wordLiveData.switchMap { word ->
        val filename = word.audio
        val subdir: String = when {
            filename.length >= 3 && filename.substring(0..3) == "bix" -> "bix"
            filename.length >= 2 && filename.substring(0..2) == "gg" -> "gg"
            filename.first().isDigit() || filename.first() == '_' -> "number"
            else -> filename.first().toString()
        }
        val url = "https://media.merriam-webster.com/audio/prons/en/us/mp3/$subdir/$filename.mp3"
        pronunciationModel.getPlayer(url)
    }

    /**
     * Load the first word of a new session
     */
    init {
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
        return wordModel.numSessionWords()
    }

    /**
     * Cleanup model objects
     */
    override fun onCleared() {
        pronunciationModel.cleanup()
    }
}