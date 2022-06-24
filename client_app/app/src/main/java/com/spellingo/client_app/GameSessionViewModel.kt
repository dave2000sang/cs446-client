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
    private var wordLiveData = MutableLiveData<Word>()

    /**
     * Load the first word of a new session
     */
    fun getWord(): LiveData<Word> {
        viewModelScope.launch {
            val curWord = wordModel.getNewSessionWords()
            wordLiveData.postValue(curWord)
        }
        return wordLiveData
    }

    /**
     * Get pronunciation of word
     */
    fun getPronunciation(): LiveData<MediaPlayer> {
        return wordLiveData.switchMap { word ->
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
    }

    /**
     * Get next word, refresh state, and get number of remaining words in session
     * @return number of words left in session
     */
    fun nextWord(): Int {
        val curWord = wordModel.getWord() ?: return 0
        wordLiveData.postValue(curWord)
        return wordModel.numSessionWords()
    }
}