package com.example.weatherapp

import WeatherApiResponse
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_PLACE_NAME = "placeName"
        const val EXTRA_PLACE_LAT = "placeLat"
        const val EXTRA_PLACE_LNG = "placeLng"
    }

    private lateinit var weatherData: WeatherApiResponse
    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: HourlyWeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val name = intent.extras!![EXTRA_PLACE_NAME] as String

//        Toast.makeText(applicationContext, name, Toast.LENGTH_LONG).show()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = name
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL ,false)
        recyclerView.layoutManager = layoutManager

        val textScrolling = findViewById<TextView>(R.id.text_scrolling)

        val weatherApiClient = WeatherApiClient("f7e942927369dbd7b31e7a69df30b3fd")

        lifecycleScope.launch {
            try {
                weatherData = weatherApiClient.getWeatherData(
                    intent.extras!![EXTRA_PLACE_LAT] as Double,
                    intent.extras!![EXTRA_PLACE_LNG] as Double
                )
                textScrolling.text = weatherData.toString()

                val timezoneOffset = weatherData.timezoneOffset
                val sunrise = weatherData.daily?.get(0)?.sunrise
                val sunset = weatherData.daily?.get(0)?.sunset
                val dt = weatherData.hourly?.map {it.dt}?.slice(0..23)
                val temp = weatherData.hourly?.map {it.temp}?.slice(0..23)
                val clouds = weatherData.hourly?.map {it.clouds}?.slice(0..23)
                val desc = weatherData.hourly?.map {it.weather.description}?.slice(0..23)
                val id = weatherData.hourly?.map {it.weather.id}?.slice(0..23)

//                Toast.makeText(this@DetailActivity, dt?.size ?: 0, Toast.LENGTH_LONG).show()

                val adapter = HourlyWeatherAdapter(timezoneOffset, sunrise, sunset, dt, temp, clouds, id, desc)
                recyclerView.adapter = adapter

            } catch (e: Exception) {
                textScrolling.text = e.toString()
            }
        }
    }
}
