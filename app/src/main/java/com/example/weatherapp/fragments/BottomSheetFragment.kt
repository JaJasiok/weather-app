package com.example.weatherapp.fragments

import WeatherApiResponse
import android.location.Geocoder
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
import com.example.weatherapp.getCityName
import kotlinx.coroutines.launch
import java.util.Locale

class BottomSheet(
    private val latitude: Double,
    private val longitude: Double,
    private var weatherData: WeatherApiResponse?,
) : ViewPagerBottomSheetDialogFragment() {

    private var _binding: SheetBottomBinding? = null
    private val binding get() = _binding!!

    private lateinit var geocoder: Geocoder
    private val locationViewModel: LocationViewModel by activityViewModels {
        LocationModelFactory((requireActivity().application as WeatherApplication).repository)
    }
    private lateinit var viewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SheetBottomBinding.inflate(inflater, container, false)
        val view = binding.root

        val weatherApiClient = WeatherApiClient(requireContext(), "f7e942927369dbd7b31e7a69df30b3fd")

        viewPager = binding.pager

        val adapter = MyFragmentAdapter(childFragmentManager)
        viewPager.adapter = adapter

        val tabLayout = binding.tabs
        tabLayout.setupWithViewPager(viewPager)

        geocoder = Geocoder(requireActivity(), Locale.getDefault())

        val locationName = getCityName(geocoder, latitude, longitude)

        val toolbar = binding.toolbar
        toolbar.title = locationName

        toolbar.inflateMenu(R.menu.weather_menu)

        val errorText = binding.errorText

        locationViewModel.locations.observe(viewLifecycleOwner) { locations ->
            if ((locations.find { it.locationName == locationName }) != null) {
                toolbar.menu.findItem(R.id.action_add_favorite).isVisible = false
                toolbar.menu.findItem(R.id.action_delete_favorite).isVisible = true
            } else {
                toolbar.menu.findItem(R.id.action_add_favorite).isVisible = true
                toolbar.menu.findItem(R.id.action_delete_favorite).isVisible = false
            }
        }

        if (weatherData == null) {
            errorText.visibility = View.VISIBLE
//            viewPager.visibility = View.GONE
        }
        else{
            adapter.setWeatherData(weatherData!!)
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
                            errorText.visibility = View.GONE
                            viewPager.visibility = View.VISIBLE
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