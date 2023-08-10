package com.example.weatherapp

import WeatherApiResponse
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.weatherapp.databinding.ActivityWeatherBinding
import com.example.weatherapp.db.Location
import kotlinx.coroutines.launch
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding

    private lateinit var intent: Intent
    private lateinit var geocoder: Geocoder
    private lateinit var viewPager: ViewPager
    private var adapter: MyFragmentAdapter? = null
    private var weatherData: WeatherApiResponse? = null
    private val locationViewModel: LocationViewModel by viewModels {
        LocationModelFactory((this@WeatherActivity.application as WeatherApplication).repository)
    }
    private val weatherApiClient = WeatherApiClient("f7e942927369dbd7b31e7a69df30b3fd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewPager = binding.pager

        geocoder = Geocoder(this@WeatherActivity, Locale.getDefault())

        intent = getIntent()

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        val locationName = getCityName(geocoder, latitude, longitude)

        val errorText = binding.errorText


        val toolbar = binding.toolbar
        toolbar.title = locationName
        toolbar.setNavigationIcon(R.drawable.back_arrow)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        toolbar.inflateMenu(R.menu.weather_menu)

        locationViewModel.locations.observe(this@WeatherActivity) { locations ->
            if ((locations.find { it.locationName == locationName }) != null) {
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
                            latitude,
                            longitude,
                            System.currentTimeMillis()
                        )
                    )
                    true
                }

                R.id.action_delete_favorite -> {
                    locationViewModel.deleteLocationByName(locationName)
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
                            if (adapter == null){
                                adapter = MyFragmentAdapter(supportFragmentManager, weatherData!!)
                                viewPager.adapter = adapter
                            } else{
                                adapter!!.setWeatherData(weatherData!!)
                            }
                            errorText.text = ""
                        }
                    }
                    true
                }

                else -> false
            }
        }


        lifecycleScope.launch {
            weatherData = weatherApiClient.getWeatherData(latitude, longitude)

            if (weatherData == null) {
                errorText.text = "Unable to fetch data from the API. Try again!"
                return@launch
            }

            adapter = MyFragmentAdapter(supportFragmentManager, weatherData!!)
            viewPager.adapter = adapter

            val tabLayout = binding.tabs
            tabLayout.setupWithViewPager(viewPager)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}