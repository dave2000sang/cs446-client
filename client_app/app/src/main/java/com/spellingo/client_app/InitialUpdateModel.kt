package com.spellingo.client_app

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat

class InitialUpdateModel(application: Application) : UpdateModel(application) {
    private val retries = 10
    private val localNumber = 10 //TODO setting? MUST be greater than 0
    private val locale = "uk" //TODO replace with setting
    private val purgeAttemptsCeil = 5 //TODO store in more centralized place
    private val purgeCountCeil = 100 //TODO store in more centralized place
    private val purgeAmount = 10 //TODO store in more centralized place
    private val connectivity = ContextCompat.getSystemService(
        application.applicationContext,
        ConnectivityManager::class.java
    )

    override suspend fun purgeReusedWords() {
        val histDao = histDb.historyDao()
        val wordDao = wordDb.wordDao()
        val wordCount = histDao.getAllWords().size
        if(wordCount <= 0) return
        val attempts = histDao.getNumTotal()

        // Purge condition checks if average attempts is high or cache is too large
        if(attempts / wordCount >= purgeAttemptsCeil || wordCount >= purgeCountCeil) {
            val topWords = histDao.getTopWords(purgeAmount)
            val wordArray = topWords.map {
                Word(it.id, "", "", "", "", "", "")
            }.toTypedArray()
            wordDao.delete(*wordArray)
        }
    }

    override suspend fun downloadWords() {
        if(connectivity == null) {
            return
        }
        val currentNetwork = connectivity.activeNetwork
        val caps = connectivity.getNetworkCapabilities(currentNetwork)
        val doDownload = if(caps == null) {
            // default to metered dumb check
            !connectivity.isActiveNetworkMetered
        }
        else {
            // Allow Wifi and Ethernet connections
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }
        var remainingNumber = localNumber
        var retryIdx = 0
        if(doDownload) {
            while(remainingNumber > 0 && retryIdx < retries) {
                remainingNumber = tryFetchWords(remainingNumber, locale)
                retryIdx++
            }
        }
    }
}