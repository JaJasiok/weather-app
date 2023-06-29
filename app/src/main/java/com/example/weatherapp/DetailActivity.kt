package com.example.weatherapp

import WeatherApiResponse
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_PLACE_NAME = "placeName"
        const val EXTRA_PLACE_LAT = "placeLat"
        const val EXTRA_PLACE_LNG = "placeLng"
    }

    private lateinit var weatherData: WeatherApiResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val name = intent.extras!![EXTRA_PLACE_NAME] as String

        Toast.makeText(applicationContext, name, Toast.LENGTH_LONG).show()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = name
        setSupportActionBar(toolbar)

        val textScrolling = findViewById<TextView>(R.id.text_scrolling)


        val weatherApiClient = WeatherApiClient("f7e942927369dbd7b31e7a69df30b3fd")

        lifecycleScope.launch {
            try {
                val weatherData = weatherApiClient.getWeatherData(
                    intent.extras!![EXTRA_PLACE_LAT] as Double,
                    intent.extras!![EXTRA_PLACE_LNG] as Double
                )
                textScrolling.text = weatherData.toString()

            } catch (e: Exception) {
                textScrolling.text = e.toString()
            }
        }
    }
}