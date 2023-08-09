package com.example.weatherapp.fragments

import WeatherApiResponse
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.weatherapp.LocationModelFactory
import com.example.weatherapp.LocationViewModel
import com.example.weatherapp.MyFragmentAdapter
import com.example.weatherapp.R
import com.example.weatherapp.WeatherApiClient
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.example.weatherapp.db.Location
import com.example.weatherapp.getCityName
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.util.Locale

class WeatherFragment(private val latLng: LatLng?) : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var geocoder: Geocoder
    private lateinit var viewPager: ViewPager
    private var weatherData: WeatherApiResponse? = null
    private val locationViewModel: LocationViewModel by activityViewModels {
        LocationModelFactory((requireActivity().application as WeatherApplication).repository)
    }
    private val weatherApiClient = WeatherApiClient("f7e942927369dbd7b31e7a69df30b3fd")

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        enterTransition = MaterialFadeThrough()
//        exitTransition = MaterialFadeThrough()
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)

        val weatherView = binding.root

        val toolbar = binding.toolbar

        if (latLng == null) {
            toolbar.title = "Unknown"
            binding.pager.visibility = GONE
            val errorText = binding.errorText
            errorText.text = "Unable to fetch current location. Please check permissions!"

            return weatherView
        }

        geocoder = Geocoder(requireActivity(), Locale.getDefault())

        val latitude = latLng.latitude
        val longitude = latLng.longitude

        val locationName = getCityName(geocoder, latitude, longitude)

        toolbar.title = locationName
        toolbar.inflateMenu(R.menu.like_menu)

        locationViewModel.locations.observe(viewLifecycleOwner) { locations ->
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

                else -> false
            }
        }

        lifecycleScope.launch {
            weatherData = weatherApiClient.getWeatherData(latLng.latitude, latLng.longitude)

            viewPager = binding.pager

            if (weatherData == null) {
                val errorText = binding.errorText
                errorText.text = "Unable to fetch data from the API. Try again!"
                return@launch
            }

            val adapter = MyFragmentAdapter(childFragmentManager, weatherData!!)
            viewPager.adapter = adapter

            val tabLayout = binding.tabs
            tabLayout.setupWithViewPager(viewPager)
        }

        return weatherView
    }
}