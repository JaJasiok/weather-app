package com.example.weatherapp

import WeatherApiResponse
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.databinding.ActivityWeatherBinding
import com.example.weatherapp.db.Location
import kotlinx.coroutines.launch
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding

    private lateinit var intent: Intent
    private lateinit var geocoder: Geocoder
    private var weatherData: WeatherApiResponse? = null
    private val locationViewModel: LocationViewModel by viewModels {
        LocationModelFactory((this@WeatherActivity.application as WeatherApplication).repository)
    }
    private val weatherApiClient = WeatherApiClient(this@WeatherActivity, "f7e942927369dbd7b31e7a69df30b3fd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityWeatherBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        val viewPager = binding.pager

        val adapter = MyFragmentAdapter(supportFragmentManager)
        viewPager.adapter = adapter

        val tabLayout = binding.tabs
        tabLayout.setupWithViewPager(viewPager)

        val toolbar = binding.toolbar

        val errorText = binding.errorText

        geocoder = Geocoder(this@WeatherActivity, Locale.getDefault())

        intent = getIntent()

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        lifecycleScope.launch {
            weatherData = weatherApiClient.getWeatherData(latitude, longitude)

            if (weatherData == null) {
                errorText.visibility = View.VISIBLE
                viewPager.visibility = View.GONE
                return@launch
            }

            adapter.setWeatherData(weatherData!!)
        }

        val locationName = getCityName(geocoder, latitude, longitude)
        val locationCountry = getCountryName(geocoder, latitude, longitude)

        toolbar.title = locationName
        toolbar.setNavigationIcon(R.drawable.back_arrow)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        toolbar.inflateMenu(R.menu.weather_menu)

        locationViewModel.locations.observe(this@WeatherActivity) { locations ->
            if ((locations.find { it.locationName == locationName && it.locationCountry == locationCountry}) != null) {
                toolbar.menu.findItem(R.id.action_add_favorite).isVisible = false
                toolbar.menu.findItem(R.id.action_delete_favorite).isVisible = true
            } else {
                toolbar.menu.findItem(R.id.action_add_favorite).isVisible = true
                toolbar.menu.findItem(R.id.action_delete_favorite).isVisible = false
            }
        }

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_add_favorite -> {
                    locationViewModel.addLocation(
                        Location(
                            locationName,
                            locationCountry,
                            latitude,
                            longitude,
                            System.currentTimeMillis()
                        )
                    )
                    true
                }

                R.id.action_delete_favorite -> {
                    locationViewModel.deleteLocationByData(locationName, locationCountry)
                    true
                }

                R.id.action_refresh -> {
                    lifecycleScope.launch {
                        val newWeatherData = weatherApiClient.getWeatherData(latitude, longitude)
                        if (newWeatherData == null) {
                            Toast.makeText(
                                this@WeatherActivity,
                                "Unable to refresh data. Try again!",
                                Toast.LENGTH_LONG
                            ).show()
//                        }else if (weatherData == newWeatherData) {
//                            weatherData!!.current!!.dt = System.currentTimeMillis()
//                            adapter.setWeatherData(weatherData!!)
                        } else {
                            weatherData = newWeatherData
                            adapter.setWeatherData(weatherData!!)
                            errorText.visibility = View.GONE
                            viewPager.visibility = View.VISIBLE
                        }
                    }
                    true
                }

                else -> false
            }
        }
    }
}