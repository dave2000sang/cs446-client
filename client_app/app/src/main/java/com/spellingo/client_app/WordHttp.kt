package com.spellingo.client_app

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class WordHttp {
    private val baseUrl = "http://172.105.103.199:96"
    suspend fun getWords(): String {
        val ret = StringBuffer()
        val url: URL = URL("$baseUrl/words")
        var conn: HttpURLConnection? = null
        withContext(IO) {
            try {
                conn = url.openConnection() as HttpURLConnection;
                conn!!.requestMethod = "GET";
                val data = conn!!.inputStream.bufferedReader().use { it.readText() }
                ret.append(data)
            }
            catch(e: Exception) {
                for(trace in e.stackTrace) {
                    System.err.println(trace)
                }
            }
            finally {
                conn?.disconnect()
            }
        }
        return ret.toString()
    }
}