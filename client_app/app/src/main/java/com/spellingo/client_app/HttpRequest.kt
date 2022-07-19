package com.spellingo.client_app

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Common HTTP request logic
 */
class HttpRequest() {
    private val ipAddr = "172.105.103.199"
    private val baseUrl = "https://$ipAddr:96"

    /**
     * Get list of available categories
     * @return JSON list of word categories
     */
    suspend fun getCategories(): String {
        return sendGetRequest("/categories", "")
    }

    /**
     * Refresh local word list
     * @param limit total number of words to request
     * @param locale word locale
     * @param category word category
     * @param difficulty word difficulty
     * @return JSON list of new words
     */
    suspend fun getWords(limit: Int, locale: Locale, category: String, difficulty: Difficulty): String {
        val localeString = locale.name.lowercase()
        val difficultyString = difficulty.name.lowercase()
        val query = "limit=$limit&locale=$localeString&category=$category&difficulty=$difficultyString"
        return sendGetRequest("/words", query)
    }

    /**
     * Get word of the day from server
     * @return word of the day
     */
    suspend fun getWotd(): String {
        return sendGetRequest("/word_of_the_day", "")
    }

    /**
     * Create a HTTP GET request at a specified endpoint. Currently doesn't support request body
     * @param endpoint API endpoint to request from
     * @param query query string for GET
     * @return response body as a String
     */
    private suspend fun sendGetRequest(endpoint: String, query: String): String {
        val method = "GET"
        var data = ""
        val suffix = if(query.isEmpty()) "" else "?$query"
        var conn: HttpsURLConnection? = null
        withContext(IO) {
            try {
                val url: URL = URL("$baseUrl$endpoint$suffix")
                conn = url.openConnection() as HttpsURLConnection
                // Using IP address instead of domain breaks default hostname verifier
                conn!!.setHostnameVerifier { hostname, _ ->
                    hostname.equals(ipAddr)
                }
                conn!!.requestMethod = method
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