package com.example.linecoinproject

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.linecoinproject.api.SimpleApi
import com.example.linecoinproject.databinding.MainBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL


class Main : AppCompatActivity() {
    private lateinit var binding: MainBinding
    private lateinit var simpleApi: SimpleApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val toolbar = findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(toolbar)
        val listView = findViewById<ListView>(R.id.main_listview)

        val apiUrl = "https://api.coinranking.com/v2/coins"
        val response = StringBuilder()
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


                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                reader.close()

                println("Response Data: $response")
            } else {
                println("Error: Unable to fetch data from the API")
            }

            // Close the connection
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()

        }
        println("TWO STEP AHEAD ${response}")
        listView.adapter = EachCoin(this)


    }
    private class EachCoin(context: Context): BaseAdapter(){

        private val mContext: Context
            init {
                mContext = context
            }

        override fun getCount(): Int {
            return 10
        }

        override fun getItem(p0: Int): Any {
            return "TEST STRING"
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val coin = layoutInflater.inflate(R.layout.coin, p2, false)
            return coin
        }
    }
}