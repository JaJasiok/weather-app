package com.example.weatherapp.fragments

import WeatherApiResponse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment
import com.example.weatherapp.LocationModelFactory
import com.example.weatherapp.LocationViewModel
import com.example.weatherapp.MyFragmentAdapter
import com.example.weatherapp.R
import com.example.weatherapp.WeatherApiClient
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.databinding.SheetBottomBinding
import com.example.weatherapp.db.Location
import kotlinx.coroutines.launch

class BottomSheet(
    private val locationName: String,
    private val latitude: Double,
    private val longitude: Double,
    private var weatherData: WeatherApiResponse?,
) : ViewPagerBottomSheetDialogFragment() {

    private var _binding: SheetBottomBinding? = null
    private val binding get() = _binding!!

    private val locationViewModel: LocationViewModel by activityViewModels {
        LocationModelFactory((requireActivity().application as WeatherApplication).repository)
    }
    private lateinit var viewPager: ViewPager
    private val weatherApiClient = WeatherApiClient("f7e942927369dbd7b31e7a69df30b3fd")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SheetBottomBinding.inflate(inflater, container, false)
        val view = binding.root

        viewPager = binding.pager

        val errorText = binding.errorText
        if (weatherData == null) {
            errorText.text = "Unable to fetch data from the API. Try again!"
            return view
        }

        val adapter = MyFragmentAdapter(childFragmentManager, weatherData!!)
        viewPager.adapter = adapter

        val tabLayout = binding.tabs
        tabLayout.setupWithViewPager(viewPager)

        val toolbar = binding.toolbar
        toolbar.title = locationName

        toolbar.inflateMenu(R.menu.weather_menu)

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

                R.id.action_refresh -> {
                    lifecycleScope.launch {
                        val newWeatherData = weatherApiClient.getWeatherData(latitude, longitude)
                        if (newWeatherData == null) {
                            Toast.makeText(
                                requireContext(),
                                "Unable to refresh data. Try again!",
                                Toast.LENGTH_LONG
                            ).show()
//                        }else if (weatherData == newWeatherData) {
//                            weatherData!!.current!!.dt = System.currentTimeMillis()
//                            adapter.setWeatherData(weatherData!!)
                        } else {
                            weatherData = newWeatherData
                            adapter.setWeatherData(weatherData!!)
                            errorText.text = ""
                        }
                    }
                    true
                }

                else -> false
            }
        }

//        ViewPagerBottomSheetBehavior.from(binding.bottomSheet).peekHeight = 100

        return view
    }
}