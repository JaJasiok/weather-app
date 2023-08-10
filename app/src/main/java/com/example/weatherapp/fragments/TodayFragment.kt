package com.example.weatherapp.fragments

import WeatherApiResponse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.HourlyWeatherAdapter
import com.example.weatherapp.MyFragmentAdapter
import com.example.weatherapp.RainAdapter
import com.example.weatherapp.databinding.FragmentTodayBinding
import com.example.weatherapp.formatDate
import com.example.weatherapp.formatHour
import com.example.weatherapp.getDrawableByName
import com.example.weatherapp.getIconNameHour
import java.util.Locale

class TodayFragment(private var weatherData: WeatherApiResponse) : Fragment(), MyFragmentAdapter.WeatherDataListener {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTodayBinding.inflate(inflater, container, false)

        updateFragment()

        return binding.root
    }

    private fun updateFragment(){
        _binding?.let { binding ->

            val currentLayout = binding.currentLayout

            val hourlyRecyclerView = binding.hourlyRecyclerView

            val layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            hourlyRecyclerView.layoutManager = layoutManager

            val gradientName: String

            val dayTime: String =
                if (weatherData.current!!.dt > weatherData.current!!.sunset && weatherData.current!!.dt < weatherData.current!!.sunrise) {
                    "dark"
                } else {
                    "light"
                }
            gradientName = if (weatherData.current!!.clouds < 25) {
                "${dayTime}_blue_gradient"
            } else {
                "${dayTime}_grey_gradient"
            }

            val gradient = getDrawableByName(requireContext(), gradientName)
            currentLayout.background = gradient

            val updateTimeText = binding.updateTimeText
            updateTimeText.text = formatDate(weatherData.current!!.dt, weatherData.timezoneOffset)

            val dayNightText = binding.dayNightText
            dayNightText.text =
                "Day ${weatherData.daily!![0].temp.day.toInt()}°C · Night ${weatherData.daily!![0].temp.night.toInt()}°C"

            val tempText = binding.tempText
            tempText.text = weatherData.current!!.temp.toInt().toString()

            val feelsLikeText = binding.feelsLikeText
            feelsLikeText.text =
                "Feels like " + weatherData.current!!.feelsLike.toInt().toString() + "°C"

            val currentWeatherImage = binding.currentWeatherImage
            val icon = getIconNameHour(
                weatherData.current!!.weather.id,
                weatherData.current!!.dt,
                weatherData.current!!.sunrise,
                weatherData.current!!.sunset,
                weatherData.daily!![1].sunrise,
                weatherData.current!!.clouds
            )
            val drawable = getDrawableByName(requireContext(), icon)
            currentWeatherImage.setImageDrawable(drawable)

            val weatherText = binding.weatherText
            weatherText.text = weatherData.current!!.weather.description.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }

            val timezoneOffset = weatherData.timezoneOffset
            val sunriseToday = weatherData.daily!![0].sunrise
            val sunsetToday = weatherData.daily!![0].sunset
            val sunriseTomorrow = weatherData.daily!![1].sunrise
            val dt = weatherData.hourly?.map { it.dt }?.slice(1..24)
            val temp = weatherData.hourly?.map { it.temp }?.slice(1..24)
            val clouds = weatherData.hourly?.map { it.clouds }?.slice(1..24)
            val id = weatherData.hourly?.map { it.weather.id }?.slice(1..24)
            val desc = weatherData.hourly?.map { it.weather.description }?.slice(1..24)
            val main = weatherData.hourly?.map { it.weather.main }?.slice(1..24)
            val pop = weatherData.hourly?.map { it.pop }?.slice(1..24)

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

            val humidityText = binding.humidityText
            humidityText.text = weatherData.current!!.humidity.toString() + "%"

            val dewPointText = binding.dewPointText
            dewPointText.text = weatherData.current!!.dewPoint.toInt().toString() + "°C"

            val pressureText = binding.pressureText
            pressureText.text = weatherData.current!!.pressure.toString() + " hPa"

            val uvIndexText = binding.uvIndexText
            val uvIndexLevel: String
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

            val visibilityText = binding.visibilityText
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

            val windSpeedText = binding.windSpeedText
            windSpeedText.text = weatherData.current!!.humidity.toString() + " m/s"

            val rainRecyclerView = binding.rainRecyclerView
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

            val dayVolumeText = binding.dayVolumeText
            if ((weatherData.daily!![0].rain?.rem(1.0) ?: 0.0) == 0.0) {
                dayVolumeText.text =
                    String.format("%.0f", weatherData.daily!![0].rain).replace(",", ".") + " mm"
            } else {
                dayVolumeText.text =
                    String.format("%.1f", weatherData.daily!![0].rain).replace(",", ".") + " mm"
            }

            val sunriseText = binding.sunriseText
            sunriseText.text = formatHour(weatherData.current!!.sunrise, weatherData.timezoneOffset)

            val sunsetText = binding.sunsetText
            sunsetText.text = formatHour(weatherData.current!!.sunset, weatherData.timezoneOffset)

            val dayLengthText = binding.dayLengthText
            dayLengthText.text =
                "${((weatherData.current!!.sunset - weatherData.current!!.sunrise) / 3600).toInt()} h and ${(((weatherData.current!!.sunset - weatherData.current!!.sunrise) % 3600) / 60).toInt()} min"

            val remainingDaylightText = binding.remainingDaylightText
            remainingDaylightText.text =
                "${((weatherData.current!!.sunset - weatherData.current!!.dt) / 3600).toInt()} h and ${(((weatherData.current!!.sunset - weatherData.current!!.dt) % 3600) / 60).toInt()} min"
            if ((weatherData.current!!.sunset - weatherData.current!!.dt) < 1) {
                val remainingDaylightString = binding.remainingDaylightString
                remainingDaylightString.visibility = View.GONE
                remainingDaylightText.visibility = View.GONE
            }

            val moonPhaseImage = binding.moonPhaseImage
            val moonPhase = weatherData.daily!![0].moonPhase
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

            val moonPhaseText = binding.moonPhaseText
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

            val moonriseText = binding.moonriseText
            moonriseText.text =
                formatHour(weatherData.daily!![0].moonrise, weatherData.timezoneOffset)

            val moonsetText = binding.moonsetText
            moonsetText.text =
                formatHour(weatherData.daily!![1].moonset, weatherData.timezoneOffset)
        }
    }

    override fun onWeatherDataUpdated(newWeatherData: WeatherApiResponse) {
        this.weatherData = newWeatherData

        updateFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}