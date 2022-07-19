package com.spellingo.client_app

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Model for creating and starting word pronunciation's [MediaPlayer]
 */
class PronunciationModel(private val application: Application) {
    private val mediaPlayer = MediaPlayer()
    private val mediaData = MutableLiveData<MediaPlayer>()

    /**
     * Get media player for audio
     * @param url URL of audio file
     * @return LiveData of media player when it's prepared
     */
    fun getPlayer(url: String): LiveData<MediaPlayer?> {
        try {
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
        }
        catch(e: Exception) {
            Toast.makeText(application, "Failed to get audio file", Toast.LENGTH_SHORT).show()
            System.err.println(e.toString())
        }
        return mediaData
    }

    /**
     * Deallocate media player, call in Activity's onDestroy()
     */
    fun cleanup() {
        mediaPlayer.release()
    }
}