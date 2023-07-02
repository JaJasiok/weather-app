package com.example.weatherapp

import WeatherApiResponse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.text.capitalize
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TomorrowFragment(private val weatherData: WeatherApiResponse) : Fragment() {

    private lateinit var hourlyRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val todayView = inflater.inflate(R.layout.fragment_tomorrow, container, false) as NestedScrollView

        val currentLayout = todayView.findViewById<LinearLayout>(R.id.day_layout)

        hourlyRecyclerView = currentLayout.findViewById(R.id.hourly_recycler_view)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        hourlyRecyclerView.layoutManager = layoutManager

        val gradientName: String

        if (weatherData.daily!![1].clouds < 25) {
            gradientName = "light_blue_gradient"
        } else {
            gradientName = "light_grey_gradient"
        }

        Toast.makeText(requireContext(), gradientName, Toast.LENGTH_LONG).show()


        val gradient = getDrawableByName(requireContext(), gradientName)
        currentLayout.background = gradient

        val dateText = todayView.findViewById<TextView>(R.id.date_text)
//                updateTimeText.text = formatDate(weatherData.current!!.dt + weatherData.timezoneOffset)
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH)
        dateText.text = dateFormat.format(Date(weatherData.daily!![1].dt * 1000))
//                updateTimeText.text = formatDate(1688045426)

        val dayNightText = todayView.findViewById<TextView>(R.id.day_night)
        dayNightText.text = "Day ${weatherData.daily!![1].temp.day.toInt()}°C · Night ${weatherData.daily[1].temp.night.toInt()}°C"

        val weatherText = todayView.findViewById<TextView>(R.id.weather_text)
        weatherText.text = weatherData.daily[1].weather.description.capitalize()

        val currentWeatherImage = todayView.findViewById<ImageView>(R.id.current_weather_image)
        var icon = getIconName(
            weatherData.daily[1].weather.id,
            weatherData.daily[1].dt,
            weatherData.daily[1].sunrise,
            weatherData.daily[1].sunset,
            weatherData.daily[1].clouds
        )

        icon = icon.replace("night", "day")

        val drawable = getDrawableByName(requireContext(), icon)
        currentWeatherImage.setImageDrawable(drawable)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = weatherData.current!!.dt * 1000

        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val indexShift =  24 - hour

        val timezoneOffset = weatherData.timezoneOffset
        val sunrise = weatherData.daily[0].sunrise
        val sunset = weatherData.daily[0].sunset
        val dt = weatherData.hourly?.map { it.dt }?.slice(1 + indexShift..24 + indexShift)
        val temp = weatherData.hourly?.map { it.temp }?.slice(1 + indexShift..24 + indexShift)
        val clouds = weatherData.hourly?.map { it.clouds }?.slice(1 + indexShift..24 + indexShift)
        val id = weatherData.hourly?.map { it.weather.id }?.slice(1 + indexShift..24 + indexShift)
        val desc = weatherData.hourly?.map { it.weather.description }?.slice(1 + indexShift..24 + indexShift)
        val rain = weatherData.hourly?.map { it.rain ?: 0.0 }?.slice(1 + indexShift..24 + indexShift)
        val pop = weatherData.hourly?.map { it.pop }?.slice(1 + indexShift..24 + indexShift)

//                Toast.makeText(requireContext(), dt?.size ?: 0, Toast.LENGTH_LONG).show()

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

        val humidityText = todayView.findViewById<TextView>(R.id.humidity_text)
        humidityText.text = weatherData.daily[1].humidity.toString() + "%"

        val dewPointText = todayView.findViewById<TextView>(R.id.dew_point_text)
        dewPointText.text = weatherData.daily[1].dewPoint.toInt().toString() + "°C"

        val pressureText = todayView.findViewById<TextView>(R.id.pressure_text)
        pressureText.text = weatherData.daily[1].pressure.toString() + " hPa"

        val uvIndexText = todayView.findViewById<TextView>(R.id.uv_index_text)
        val uvIndexLevel: String;
        if (weatherData.daily[1].uvi.toInt() < 3) {
            uvIndexLevel = "Low"
        } else if (weatherData.daily[1].uvi.toInt() < 6) {
            uvIndexLevel = "Moderate"
        } else if (weatherData.daily[1].uvi.toInt() < 8) {
            uvIndexLevel = "High"
        } else if (weatherData.daily[1].uvi.toInt() < 11) {
            uvIndexLevel = "Very high"
        } else {
            uvIndexLevel = "Extreme"
        }
        uvIndexText.text =
            uvIndexLevel + ", " + weatherData.daily[1].uvi.toInt().toString()


        val windSpeedText = todayView.findViewById<TextView>(R.id.wind_speed_text)
        windSpeedText.text = weatherData.daily[1].humidity.toString() + " m/s"

        val sunriseText = todayView.findViewById<TextView>(R.id.sunrise_text)
        sunriseText.text = formatHour(weatherData.daily[1].sunrise)

        val sunsetText = todayView.findViewById<TextView>(R.id.sunset_text)
        sunsetText.text = formatHour(weatherData.daily[1].sunset)

        val dayLengthText = todayView.findViewById<TextView>(R.id.day_length_text)
        dayLengthText.text =
            "${((weatherData.daily[1].sunset - weatherData.daily[1].sunrise) / 3600).toInt()}h i ${(((weatherData.current!!.sunset - weatherData.current!!.sunrise) % 3600) / 60).toInt()}min"

        val moonPhaseImage = todayView.findViewById<ImageView>(R.id.moon_phase_image)
        val moonPhase = weatherData.daily[0].moonPhase
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
        val moonImage = getDrawableByName(requireContext(), moonPhaseIcon)
        moonPhaseImage.setImageDrawable(moonImage)

        val moonPhaseText = todayView.findViewById<TextView>(R.id.moon_phase_text)
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

        val moonriseText = todayView.findViewById<TextView>(R.id.moonrise_text)
        moonriseText.text = formatHour(weatherData.daily[1].moonrise)

        val moonsetText = todayView.findViewById<TextView>(R.id.moonset_text)
        moonsetText.text = formatHour(weatherData.daily[2].moonset)

        val nightLengthText = todayView.findViewById<TextView>(R.id.night_length_text)
        nightLengthText.text =
            "${((weatherData.daily!![2].moonset - weatherData.daily!![1].moonrise) / 3600).toInt()}h i ${(((weatherData.daily!![2].moonset - weatherData.daily!![1].moonrise) % 3600) / 60).toInt()}min"

        return todayView
    }
}