package com.spellingo.client_app

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class HttpRequest() {
    private val ipAddr = "172.105.103.199"
    private val baseUrl = "https://$ipAddr:96"

    /**
     * Refresh local word list
     * @return JSON list of new words
     */
    suspend fun getWords(limit: Int, locale: String): String {
        val query = "limit=$limit&locale=$locale"
        return sendGetRequest("/words", query)
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
        val url: URL = URL("$baseUrl$endpoint$suffix")
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