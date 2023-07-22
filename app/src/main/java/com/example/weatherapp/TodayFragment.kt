package com.example.weatherapp

import WeatherApiResponse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class TodayFragment(private val weatherData: WeatherApiResponse) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val todayView =
            inflater.inflate(R.layout.fragment_today, container, false) as NestedScrollView

        val currentLayout = todayView.findViewById<RelativeLayout>(R.id.current_layout)

        val hourlyRecyclerView = currentLayout.findViewById<RecyclerView>(R.id.hourly_recycler_view)
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        hourlyRecyclerView.layoutManager = layoutManager

        val dayTime: String
        val gradientName: String

        if (weatherData.current!!.dt > weatherData.current.sunset && weatherData.current.dt < weatherData.current.sunrise) {
            dayTime = "dark"
        } else {
            dayTime = "light"
        }
        if (weatherData.current.clouds < 25) {
            gradientName = "${dayTime}_blue_gradient"
        } else {
            gradientName = "${dayTime}_grey_gradient"
        }

//        Toast.makeText(requireContext(), gradientName, Toast.LENGTH_LONG).show()

        val gradient = getDrawableByName(requireContext(), gradientName)
        currentLayout.background = gradient

        val updateTimeText = todayView.findViewById<TextView>(R.id.update_time_text)
//                updateTimeText.text = formatDate(weatherData.current!!.dt + weatherData.timezoneOffset)
        updateTimeText.text = formatDate(weatherData.current.dt, weatherData.timezoneOffset)
//                updateTimeText.text = formatDate(1688045426)

        val dayNightText = todayView.findViewById<TextView>(R.id.day_night)
        dayNightText.text =
            "Day ${weatherData.daily!![0].temp.day.toInt()}°C · Night ${weatherData.daily[0].temp.night.toInt()}°C"

        val tempText = todayView.findViewById<TextView>(R.id.temp_text)
        tempText.text = weatherData.current.temp.toInt().toString()

        val feelsLikeText = todayView.findViewById<TextView>(R.id.feels_like_text)
        feelsLikeText.text =
            "Feels like " + weatherData.current.feelsLike.toInt().toString() + "°C"

        val currentWeatherImage = todayView.findViewById<ImageView>(R.id.current_weather_image)
        val icon = getIconNameHour(
            weatherData.current.weather.id,
            weatherData.current.dt,
            weatherData.current.sunrise,
            weatherData.current.sunset,
            weatherData.daily[1].sunrise,
            weatherData.current.clouds
        )
        val drawable = getDrawableByName(requireContext(), icon)
        currentWeatherImage.setImageDrawable(drawable)

        val weatherText = todayView.findViewById<TextView>(R.id.weather_text)
        weatherText.text = weatherData.current.weather.description.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }

        val timezoneOffset = weatherData.timezoneOffset
        val sunriseToday = weatherData.daily[0].sunrise
        val sunsetToday = weatherData.daily[0].sunset
        val sunriseTomorrow = weatherData.daily[1].sunrise
        val dt = weatherData.hourly?.map { it.dt }?.slice(1..24)
        val temp = weatherData.hourly?.map { it.temp }?.slice(1..24)
        val clouds = weatherData.hourly?.map { it.clouds }?.slice(1..24)
        val id = weatherData.hourly?.map { it.weather.id }?.slice(1..24)
        val desc = weatherData.hourly?.map { it.weather.description }?.slice(1..24)
        val main = weatherData.hourly?.map { it.weather.main }?.slice(1..24)
        val pop = weatherData.hourly?.map { it.pop }?.slice(1..24)

        Toast.makeText(requireContext(), timezoneOffset.toString(), Toast.LENGTH_LONG).show()

        val hourlyAdapter = HourlyWeatherAdapter(
            timezoneOffset,
            sunriseToday,
            sunsetToday,
            sunriseTomorrow,
            dt,
            temp,
            clouds,
            id,
            desc,
            main,
            pop
        )
        hourlyRecyclerView.adapter = hourlyAdapter

        val humidityText = todayView.findViewById<TextView>(R.id.humidity_text)
        humidityText.text = weatherData.current.humidity.toString() + "%"

        val dewPointText = todayView.findViewById<TextView>(R.id.dew_point_text)
        dewPointText.text = weatherData.current.dewPoint.toInt().toString() + "°C"

        val pressureText = todayView.findViewById<TextView>(R.id.pressure_text)
        pressureText.text = weatherData.current.pressure.toString() + " hPa"

        val uvIndexText = todayView.findViewById<TextView>(R.id.uv_index_text)
        val uvIndexLevel: String
        if (weatherData.current.uvi.toInt() < 3) {
            uvIndexLevel = "Low"
        } else if (weatherData.current.uvi.toInt() < 6) {
            uvIndexLevel = "Moderate"
        } else if (weatherData.current.uvi.toInt() < 8) {
            uvIndexLevel = "High"
        } else if (weatherData.current.uvi.toInt() < 11) {
            uvIndexLevel = "Very high"
        } else {
            uvIndexLevel = "Extreme"
        }
        uvIndexText.text =
            uvIndexLevel + ", " + weatherData.current.uvi.toInt().toString()

        val visibilityText = todayView.findViewById<TextView>(R.id.visibility_text)
        if (weatherData.current.visibility < 1000) {
            visibilityText.text = weatherData.current.visibility.toString() + " m"
        } else {
            var roundedNumber =
                String.format("%.1f", weatherData.current.visibility.toFloat() / 1000)
            if (roundedNumber.endsWith(",0")) {
                roundedNumber = roundedNumber.substring(0, roundedNumber.length - 2)

            }
            visibilityText.text = roundedNumber + " km"
        }

        val windSpeedText = todayView.findViewById<TextView>(R.id.wind_speed_text)
        windSpeedText.text = weatherData.current.humidity.toString() + " m/s"

        val rainRecyclerView = todayView.findViewById<RecyclerView>(R.id.rain_recycler_view)
        val layoutManager2 =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rainRecyclerView.layoutManager = layoutManager2

        val rain = weatherData.hourly?.map { it.rain ?: 0.0 }?.slice(1..24)

        val rainAdapter = RainAdapter(
            timezoneOffset,
            dt,
            rain,
            pop
        )
        rainRecyclerView.adapter = rainAdapter

        val volumeText = todayView.findViewById<TextView>(R.id.day_volume_text)
        if ((weatherData.daily[0].rain?.rem(1.0) ?: 0.0) == 0.0) {
            volumeText.text = String.format("%.0f", weatherData.daily[0].rain).replace(",", ".") + " mm"
        } else {
            volumeText.text = String.format("%.1f", weatherData.daily[0].rain).replace(",", ".") + " mm"
        }

        val sunriseText = todayView.findViewById<TextView>(R.id.sunrise_text)
        sunriseText.text = formatHour(weatherData.current.sunrise, weatherData.timezoneOffset)

        val sunsetText = todayView.findViewById<TextView>(R.id.sunset_text)
        sunsetText.text = formatHour(weatherData.current.sunset, weatherData.timezoneOffset)

        val dayLengthText = todayView.findViewById<TextView>(R.id.day_length_text)
        dayLengthText.text =
            "${((weatherData.current.sunset - weatherData.current.sunrise) / 3600).toInt()}h and ${(((weatherData.current.sunset - weatherData.current.sunrise) % 3600) / 60).toInt()}min"

        val remainingDaylightText = todayView.findViewById<TextView>(R.id.remaining_daylight_text)
        remainingDaylightText.text =
            "${((weatherData.current.sunset - weatherData.current.dt) / 3600).toInt()}h and ${(((weatherData.current.sunset - weatherData.current.dt) % 3600) / 60).toInt()}min"
        if ((weatherData.current.sunset - weatherData.current.dt) < 1) {
            val remainingDaylightString =
                todayView.findViewById<TextView>(R.id.remaining_daylight_string)
            remainingDaylightString.visibility = View.GONE
            remainingDaylightText.visibility = View.GONE
        }

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
            moonPhaseIcon = "moon_last_quarter"
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
        moonriseText.text = formatHour(weatherData.daily[0].moonrise, weatherData.timezoneOffset)

        val moonsetText = todayView.findViewById<TextView>(R.id.moonset_text)
        moonsetText.text = formatHour(weatherData.daily[1].moonset, weatherData.timezoneOffset)

//        val nightLengthText = todayView.findViewById<TextView>(R.id.night_length_text)
//        nightLengthText.text =
//            "${((weatherData.daily!![1].moonset - weatherData.daily!![0].moonrise) / 3600).toInt()}h i ${(((weatherData.daily!![1].moonset - weatherData.daily!![0].moonrise) % 3600) / 60).toInt()}min"

        return todayView
    }
}