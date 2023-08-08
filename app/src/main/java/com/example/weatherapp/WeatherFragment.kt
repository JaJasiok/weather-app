package com.example.weatherapp

import WeatherApiResponse
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.weatherapp.databinding.FragmentWeatherBinding
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import java.util.Locale

class WeatherFragment (private val latLng: LatLng): Fragment(){

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var geocoder: Geocoder
    private lateinit var viewPager: ViewPager
    private lateinit var weatherData: WeatherApiResponse

    private val weatherApiClient = WeatherApiClient("f7e942927369dbd7b31e7a69df30b3fd")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)

        val weatherView = binding.root

        geocoder = Geocoder(requireActivity(), Locale.getDefault())

        lifecycleScope.launch {
            weatherData = weatherApiClient.getWeatherData(latLng.latitude, latLng.longitude)

        val toolbar = binding.toolbar
        toolbar.title = getCityName(geocoder, latLng.latitude, latLng.longitude)

        viewPager = binding.pager

        val adapter = MyFragmentAdapter(childFragmentManager, weatherData)
        viewPager.adapter = adapter

        val tabLayout = binding.tabs
        tabLayout.setupWithViewPager(viewPager)
        }

        return weatherView
    }
}