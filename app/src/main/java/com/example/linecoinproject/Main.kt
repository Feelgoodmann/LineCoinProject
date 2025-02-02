package com.example.linecoinproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.linecoinproject.databinding.MainBinding
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

/*
* Tanapat Chokruamchai
* feelgood2545@hotmail.com
* 061-9720088
* REF:
* Android Kotlin: Essentials to Creating a ListView (Ep 1)
* Android Kotlin: ListView Custom XML Views for Rows (Ep 2)
* https://gist.github.com/Da9el00/a29b4acca9dec698e18f88fca2eb8c96
* https://github.com/google/gson
* https://stackoverflow.com/questions/47823746/kotlin-convert-json-string-to-list-of-object-using-gson
* https://stackoverflow.com/questions/71964976/load-svg-files-from-a-api-into-an-imageview-in-android-in-kotlin
* (Coil)
*
* */
class Main : AppCompatActivity() {
    private lateinit var binding: MainBinding

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
        val gson = Gson()
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
                    println(line)
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
        var obj: String = JSONObject(response.toString()).getString("data")
        obj = JSONObject(obj).getString("coins")
        val objectList = gson.fromJson(obj, Array<Coin>::class.java).asList()
        objectList.forEach {
            println(it)
        }
        listView.adapter = EachCoin(this, objectList)


    }

    private class EachCoin(context: Context, objectList: List<Coin>) : BaseAdapter() {
        private val mContext: Context
        private val mObjectList: List<Coin>

        init {
            mContext = context
            mObjectList = objectList
        }

        override fun getCount(): Int {
            return mObjectList.size
        }

        override fun getItem(p0: Int): Any {
            return p0
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        @SuppressLint("ViewHolder")
        override fun getView(index: Int, view: View?, viewGroup: ViewGroup?): View {

            val layoutInflater = LayoutInflater.from(mContext)
            val coin = layoutInflater.inflate(R.layout.coin, viewGroup, false)
            coin.setOnClickListener {
                val openLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mObjectList[index].coinrankingUrl))
                mContext.startActivity(openLinkIntent)
            }

            val coinIconStart = coin.findViewById<ImageView>(R.id.coinIconStart)
            val coinIconEnd = coin.findViewById<ImageView>(R.id.coinIconEnd)


            val coinName = coin.findViewById<TextView>(R.id.coinName)
            coinName.text = mObjectList[index].symbol

            val coinDetail = coin.findViewById<TextView>(R.id.coinDetail)
            coinDetail.text = mObjectList[index].name

            if ((index + 1) % 5 == 0) {
                coinIconEnd.loadUrl(mObjectList[index].iconUrl)

                coinName.textAlignment = View.TEXT_ALIGNMENT_TEXT_END

                coinDetail.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
                coinDetail.text = mObjectList[index].coinrankingUrl
            } else {
                coinIconStart.loadUrl(mObjectList[index].iconUrl)
            }

            return coin
        }

        fun ImageView.loadUrl(url: String) {

            val imageLoader =
                ImageLoader.Builder(this.context).componentRegistry { add(SvgDecoder(this@loadUrl.context)) }.build()

            val request = ImageRequest.Builder(this.context).data(url).target(this).build()

            imageLoader.enqueue(request)
        }

    }

    data class Coin(
        val symbol: String,
        val name: String,
        val iconUrl: String,
        val coinrankingUrl: String,
        val marketCap: Long,
        val price: BigDecimal,
    )
}