package com.spellingo.client_app

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PronunciationModel {
    private val url = "https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3"
    private val mediaPlayer = MediaPlayer()
    private val mediaData = MutableLiveData<MediaPlayer>()

    /**
     * Get media player for audio
     * @param url URL of audio file
     * @return LiveData of media player when it's prepared
     */
    fun getPlayer(url: String): LiveData<MediaPlayer> {
        mediaPlayer.apply {
            reset()
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            setOnPreparedListener {
                it.start()
                mediaData.postValue(it)
            }
            prepareAsync()
        }
        return mediaData
    }

    /**
     * Deallocate media player, call in Activity's onDestroy()
     */
    fun drop() {
        mediaPlayer.release()
    }
}