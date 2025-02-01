package com.example.linecoinproject.api

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

class SimpleApi {
    fun getApi(apiUrl: String): StringBuilder {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            val url: URL = URI.create(apiUrl).toURL()
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

            //Request method: GET
            connection.requestMethod = "GET"

            // Response code
            val responseCode: Int = connection.responseCode
            println("Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read and print the response data
                val reader: BufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String?
                val response = StringBuilder()

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                reader.close()

                println("Response Data: $response")
                return response
            } else {
                println("Error: Unable to fetch data from the API")
            }

            // Close the connection
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()

        }
        return StringBuilder()
    }

}