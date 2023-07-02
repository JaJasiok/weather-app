package com.example.weatherapp

import WeatherApiResponse
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PLACE_NAME = "placeName"
        const val EXTRA_PLACE_LAT = "placeLat"
        const val EXTRA_PLACE_LNG = "placeLng"
    }

    private lateinit var weatherData: WeatherApiResponse
    private lateinit var hourlyRecyclerView: RecyclerView
//    private lateinit var adapter: HourlyWeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val name = intent.extras!![EXTRA_PLACE_NAME] as String

//        Toast.makeText(applicationContext, name, Toast.LENGTH_LONG).show()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = name
        setSupportActionBar(toolbar)

        hourlyRecyclerView = findViewById(R.id.hourly_recycler_view)
        val layoutManager =
            LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
        hourlyRecyclerView.layoutManager = layoutManager

        val textScrolling = findViewById<TextView>(R.id.text_scrolling)

        val weatherApiClient = WeatherApiClient("f7e942927369dbd7b31e7a69df30b3fd")

        lifecycleScope.launch {
            try {
                weatherData = weatherApiClient.getWeatherData(
                    intent.extras!![EXTRA_PLACE_LAT] as Double,
                    intent.extras!![EXTRA_PLACE_LNG] as Double
                )

                val currentLayout = findViewById<LinearLayout>(R.id.current_layout)

                val dayTime: String
                val gradientName: String

                if (weatherData.current!!.dt > weatherData.current!!.sunset && weatherData.current!!.dt < weatherData.current!!.sunrise) {
                    dayTime = "dark"
                } else {
                    dayTime = "light"
                }
                if (weatherData.current!!.clouds < 25) {
                    gradientName = "${dayTime}_blue_gradient"
                } else {
                    gradientName = "${dayTime}_grey_gradient"
                }

                Toast.makeText(this@DetailActivity, gradientName, Toast.LENGTH_LONG).show()


                val gradient = getDrawableByName(this@DetailActivity, gradientName)
                currentLayout.background = gradient

                val updateTimeText = findViewById<TextView>(R.id.update_time_text)
//                updateTimeText.text = formatDate(weatherData.current!!.dt + weatherData.timezoneOffset)
                updateTimeText.text = formatDate(weatherData.current!!.dt)
//                updateTimeText.text = formatDate(1688045426)

                val tempText = findViewById<TextView>(R.id.temp_text)
                tempText.text = weatherData.current!!.temp.toInt().toString()

                val feelsLikeText = findViewById<TextView>(R.id.feels_like_text)
                feelsLikeText.text =
                    "Feels like " + weatherData.current!!.feelsLike.toInt().toString() + "°C"

                val currentWeatherImage = findViewById<ImageView>(R.id.current_weather_image)
                val icon = getIconName(
                    weatherData.current!!.weather.id,
                    weatherData.current!!.dt,
                    weatherData.current!!.sunrise,
                    weatherData.current!!.sunset,
                    weatherData.current!!.clouds
                )
                val drawable = getDrawableByName(this@DetailActivity, icon)
                currentWeatherImage.setImageDrawable(drawable)

                val timezoneOffset = weatherData.timezoneOffset
                val sunrise = weatherData.daily?.get(0)?.sunrise
                val sunset = weatherData.daily?.get(0)?.sunset
                val dt = weatherData.hourly?.map { it.dt }?.slice(1..24)
                val temp = weatherData.hourly?.map { it.temp }?.slice(1..24)
                val clouds = weatherData.hourly?.map { it.clouds }?.slice(1..24)
                val id = weatherData.hourly?.map { it.weather.id }?.slice(1..24)
                val desc = weatherData.hourly?.map { it.weather.description }?.slice(1..24)
                val rain = weatherData.hourly?.map { it.rain ?: 0.0 }?.slice(1..24)
                val pop = weatherData.hourly?.map { it.pop }?.slice(1..24)

//                Toast.makeText(this@DetailActivity, dt?.size ?: 0, Toast.LENGTH_LONG).show()

                val adapter = HourlyWeatherAdapter(
                    timezoneOffset,
                    sunrise,
                    sunset,
                    dt,
                    temp,
                    clouds,
                    id,
                    desc,
                    rain,
                    pop
                )
                hourlyRecyclerView.adapter = adapter

                val humidityText = findViewById<TextView>(R.id.humidity_text)
                humidityText.text = weatherData.current!!.humidity.toString() + "%"

                val dewPointText = findViewById<TextView>(R.id.dew_point_text)
                dewPointText.text = weatherData.current!!.dewPoint.toInt().toString() + "°C"

                val pressureText = findViewById<TextView>(R.id.pressure_text)
                pressureText.text = weatherData.current!!.pressure.toString() + " hPa"

                val uvIndexText = findViewById<TextView>(R.id.uv_index_text)
                val uvIndexLevel: String;
                if (weatherData.current!!.uvi.toInt() < 3) {
                    uvIndexLevel = "Low"
                } else if (weatherData.current!!.uvi.toInt() < 6) {
                    uvIndexLevel = "Moderate"
                } else if (weatherData.current!!.uvi.toInt() < 8) {
                    uvIndexLevel = "High"
                } else if (weatherData.current!!.uvi.toInt() < 11) {
                    uvIndexLevel = "Very high"
                } else {
                    uvIndexLevel = "Extreme"
                }
                uvIndexText.text =
                    uvIndexLevel + ", " + weatherData.current!!.uvi.toInt().toString()

                val visibilityText = findViewById<TextView>(R.id.visibility_text)
                if (weatherData.current!!.visibility < 1000) {
                    visibilityText.text = weatherData.current!!.visibility.toString() + " m"
                } else {
                    var roundedNumber =
                        String.format("%.1f", weatherData.current!!.visibility.toFloat() / 1000)
                    if (roundedNumber.endsWith(",0")) {
                        roundedNumber = roundedNumber.substring(0, roundedNumber.length - 2)

                    }
                    visibilityText.text = roundedNumber + " km"
                }

                val windSpeedText = findViewById<TextView>(R.id.wind_speed_text)
                windSpeedText.text = weatherData.current!!.humidity.toString() + " m/s"

                val sunriseText = findViewById<TextView>(R.id.sunrise_text)
                sunriseText.text = formatHour(weatherData.current!!.sunrise)

                val sunsetText = findViewById<TextView>(R.id.sunset_text)
                sunsetText.text = formatHour(weatherData.current!!.sunset)

                val dayLengthText = findViewById<TextView>(R.id.day_length_text)
                dayLengthText.text =
                    "${((weatherData.current!!.sunset - weatherData.current!!.sunrise) / 3600).toInt()}h i ${(((weatherData.current!!.sunset - weatherData.current!!.sunrise) % 3600) / 60).toInt()}min"

                val remainingDaylightText = findViewById<TextView>(R.id.remaining_daylight_text)
                remainingDaylightText.text =
                    "${((weatherData.current!!.dt - weatherData.current!!.sunrise) / 3600).toInt()}h i ${(((weatherData.current!!.dt - weatherData.current!!.sunrise) % 3600) / 60).toInt()}min"


                val moonPhaseImage = findViewById<ImageView>(R.id.moon_phase_image)
                val moonPhase = weatherData.daily!![0].moonPhase
                val moonPhaseIcon: String
                if (moonPhase == 0.0 || moonPhase == 1.0) {
                    moonPhaseIcon = "moon_new"
                } else if (moonPhase == 0.25) {
                    moonPhaseIcon = "moon_first_quarter"
                } else if (moonPhase == 0.5) {
                    moonPhaseIcon = "moon_full"
                } else if (moonPhase == 0.75) {
                    moonPhaseIcon="moon_last_quarter"
                } else if (moonPhase < 0.25) {
                    moonPhaseIcon = "moon_waxing_crescent"
                } else if (moonPhase < 0.5) {
                    moonPhaseIcon = "moon_waxing_gibbous"
                } else if (moonPhase < 0.75) {
                    moonPhaseIcon = "moon_waning_gibbous"
                } else {
                    moonPhaseIcon = "moon_waning_crescent"
                }
                val moonImage = getDrawableByName(this@DetailActivity, moonPhaseIcon)
                moonPhaseImage.setImageDrawable(moonImage)

                val moonPhaseText = findViewById<TextView>(R.id.moon_phase_text)
                val moonPhaseName: String
                if (moonPhase == 0.0 || moonPhase == 1.0) {
                    moonPhaseName = "New moon"
                } else if (moonPhase == 0.25) {
                    moonPhaseName = "First quarter moon"
                } else if (moonPhase == 0.5) {
                    moonPhaseName = "Full moon"
                } else if (moonPhase == 0.75) {
                    moonPhaseName = "Last quarter moon"
                } else if (moonPhase < 0.25) {
                    moonPhaseName = "Waxing crescent"
                } else if (moonPhase < 0.5) {
                    moonPhaseName = "Waxing gibbous"
                } else if (moonPhase < 0.75) {
                    moonPhaseName = "Waning gibbous"
                } else {
                    moonPhaseName = "Waning crescent"
                }
                moonPhaseText.text = moonPhaseName

                val moonriseText = findViewById<TextView>(R.id.moonrise_text)
                moonriseText.text = formatHour(weatherData.daily!![0].moonrise)

                val moonsetText = findViewById<TextView>(R.id.moonset_text)
                moonsetText.text = formatHour(weatherData.daily!![0].moonset)

                val nightLengthText = findViewById<TextView>(R.id.night_length_text)
                dayLengthText.text =
                    "${((weatherData.daily!![0].moonset - weatherData.daily!![0].moonrise) / 3600).toInt()}h i ${(((weatherData.daily!![0].moonset - weatherData.daily!![0].moonrise) % 3600) / 60).toInt()}min"

                textScrolling.text = weatherData.toString()

            } catch (e: Exception) {
                textScrolling.text = e.toString()
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("MMMM d, HH:mm", Locale.ENGLISH)

        return format.format(date)
    }

    private fun formatHour(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("HH:mm", Locale.ENGLISH)

        return format.format(date)
    }
}
