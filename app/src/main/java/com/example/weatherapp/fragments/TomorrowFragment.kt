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
import com.example.weatherapp.databinding.FragmentTomorrowBinding
import com.example.weatherapp.formatHour
import com.example.weatherapp.getDrawableByName
import com.example.weatherapp.getIconNameHour
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TomorrowFragment() : Fragment(), MyFragmentAdapter.WeatherDataListener {

    private var _binding: FragmentTomorrowBinding? = null
    private val binding get() = _binding!!

    private lateinit var weatherData: WeatherApiResponse
    private var dataReceived = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTomorrowBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (dataReceived) {
            updateFragment()
        }
    }

    private fun updateFragment() {
        _binding?.let { binding ->

            val dayLayout = binding.dayLayout

            val hourlyRecyclerView = binding.hourlyRecyclerView

            val layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            hourlyRecyclerView.layoutManager = layoutManager

            val gradientName: String

            if (weatherData.daily!![1].clouds < 25) {
                gradientName = "light_blue_gradient"
            } else {
                gradientName = "light_grey_gradient"
            }

            val gradient = getDrawableByName(requireContext(), gradientName)
            dayLayout.background = gradient

            val dateText = binding.dateText
            val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH)
            dateText.text = dateFormat.format(Date(weatherData.daily!![1].dt * 1000))

            val dayNightText = binding.dayNightText
            dayNightText.text =
                "Day ${weatherData.daily!![1].temp.day.toInt()}°C · Night ${weatherData.daily!![1].temp.night.toInt()}°C"

            val weatherText = binding.weatherText
            weatherText.text = weatherData.daily!![1].weather.description.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }

            val currentWeatherImage = binding.currentWeatherImage
            var icon = getIconNameHour(
                weatherData.daily!![1].weather.id,
                weatherData.daily!![1].dt,
                weatherData.daily!![1].sunrise,
                weatherData.daily!![1].sunset,
                weatherData.daily!![2].sunset,
                weatherData.daily!![1].clouds
            )

            icon = icon.replace("night", "day")

            val drawable = getDrawableByName(requireContext(), icon)
            currentWeatherImage.setImageDrawable(drawable)

            val timezoneOffset = weatherData.timezoneOffset
            val sunriseToday = weatherData.daily!![1].sunrise
            val sunsetToday = weatherData.daily!![1].sunset
            val sunriseTomorrow = weatherData.daily!![2].sunrise
            val dt = weatherData.hourly?.map { it.dt }?.slice(24..47)
            val temp = weatherData.hourly?.map { it.temp }?.slice(24..47)
            val clouds =
                weatherData.hourly?.map { it.clouds }?.slice(24..47)
            val id =
                weatherData.hourly?.map { it.weather.id }?.slice(24..47)
            val desc = weatherData.hourly?.map { it.weather.description }
                ?.slice(24..47)
            val main =
                weatherData.hourly?.map { it.weather.main }?.slice(24..47)
            val pop = weatherData.hourly?.map { it.pop }?.slice(24..47)

            val adapter = HourlyWeatherAdapter(
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
            hourlyRecyclerView.adapter = adapter

            val humidityText = binding.humidityText
            humidityText.text = weatherData.daily!![1].humidity.toString() + "%"

            val dewPointText = binding.dewPointText
            dewPointText.text = weatherData.daily!![1].dewPoint.toInt().toString() + "°C"

            val pressureText = binding.pressureText
            pressureText.text = weatherData.daily!![1].pressure.toString() + " hPa"

            val uvIndexText = binding.uvIndexText
            val uvIndexLevel: String
            if (weatherData.daily!![1].uvi.toInt() < 3) {
                uvIndexLevel = "Low"
            } else if (weatherData.daily!![1].uvi.toInt() < 6) {
                uvIndexLevel = "Moderate"
            } else if (weatherData.daily!![1].uvi.toInt() < 8) {
                uvIndexLevel = "High"
            } else if (weatherData.daily!![1].uvi.toInt() < 11) {
                uvIndexLevel = "Very high"
            } else {
                uvIndexLevel = "Extreme"
            }
            uvIndexText.text =
                uvIndexLevel + ", " + weatherData.daily!![1].uvi.toInt().toString()

            val windSpeedText = binding.windSpeedText
            windSpeedText.text = weatherData.daily!![1].humidity.toString() + " m/s"

            val rainRecyclerView = binding.rainRecyclerView
            val layoutManager2 =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rainRecyclerView.layoutManager = layoutManager2

            val rain =
                weatherData.hourly?.map { it.rain ?: 0.0 }?.slice(24..47)

            val rainAdapter = RainAdapter(
                timezoneOffset,
                dt,
                rain,
                pop
            )
            rainRecyclerView.adapter = rainAdapter

            val volumeText = binding.dayVolumeText
            if ((weatherData.daily!![1].rain?.rem(1.0) ?: 0.0) == 0.0) {
                volumeText.text = "0 mm"
            } else {
                volumeText.text =
                    String.format("%.1f", weatherData.daily!![1].rain).replace(",", ".") + " mm"
            }

            val sunriseText = binding.sunriseText
            sunriseText.text =
                formatHour(weatherData.daily!![1].sunrise, weatherData.timezoneOffset)

            val sunsetText = binding.sunsetText
            sunsetText.text = formatHour(weatherData.daily!![1].sunset, weatherData.timezoneOffset)

            val dayLengthText = binding.dayLengthText
            dayLengthText.text =
                "${((weatherData.daily!![1].sunset - weatherData.daily!![1].sunrise) / 3600).toInt()} h and ${(((weatherData.current!!.sunset - weatherData.current!!.sunrise) % 3600) / 60).toInt()} min"

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
                formatHour(weatherData.daily!![1].moonrise, weatherData.timezoneOffset)

            val moonsetText = binding.moonsetText
            moonsetText.text =
                formatHour(weatherData.daily!![2].moonset, weatherData.timezoneOffset)
        }
    }

    override fun onWeatherDataUpdated(newWeatherData: WeatherApiResponse) {
        this.weatherData = newWeatherData
        dataReceived = true

        if (isAdded && view != null) {
            updateFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}