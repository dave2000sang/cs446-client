package com.spellingo.client_app

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class HttpRequest() {
    private val ipAddr = "172.105.103.199"
    private val baseUrl = "https://$ipAddr:96"

    // Singleton pattern
    companion object {
        @Volatile private var INSTANCE: HttpRequest? = null

        fun getInstance(): HttpRequest =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HttpRequest().also { INSTANCE = it }
            }
    }

    /**
     * Refresh local word list
     * @return JSON list of new words
     */
    suspend fun getWords(): String {
        return sendRequest("/words", "GET")
    }

    /**
     * Create a HTTP request at a specified endpoint. Currently doesn't support request body
     * @param endpoint API endpoint to request from
     * @param method HTTP method
     * @return response body as a String
     */
    private suspend fun sendRequest(endpoint: String, method: String): String {
        var data = ""
        val url: URL = URL("$baseUrl$endpoint")
        var conn: HttpsURLConnection? = null
        withContext(IO) {
            try {
                conn = url.openConnection() as HttpsURLConnection;
                // Using IP address instead of domain breaks default hostname verifier
                conn!!.setHostnameVerifier { hostname, _ ->
                    hostname.equals(ipAddr)
                }
                conn!!.requestMethod = method;
                data = conn!!.inputStream.bufferedReader().use { it.readText() }
            }
            catch(e: Exception) {
                System.err.println(e.toString())
            }
            finally {
                conn?.disconnect()
            }
        }
        return data
    }
}