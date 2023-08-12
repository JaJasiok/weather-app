package com.example.weatherapp.fragments

import WeatherApiResponse
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
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
    private var weatherData: WeatherApiResponse? = null
    private val locationViewModel: LocationViewModel by activityViewModels {
        LocationModelFactory((requireActivity().application as WeatherApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        fetchWeatherData()
    }

    private fun setupViews() {
        val viewPager = binding.pager
        val adapter = MyFragmentAdapter(childFragmentManager)
        viewPager.adapter = adapter

        binding.tabs.setupWithViewPager(viewPager)
        binding.toolbar.inflateMenu(R.menu.weather_menu)
        binding.errorText.visibility = View.GONE

        if (latLng == null) {
            handleUnknownLocation()
        } else {
            geocoder = Geocoder(requireActivity(), Locale.getDefault())
            val latitude = latLng.latitude
            val longitude = latLng.longitude
            val locationName = getCityName(geocoder, latitude, longitude)
            setupToolbar(locationName)
            setupMenu(locationName, latitude, longitude)
        }
    }

    private fun handleUnknownLocation() {
        binding.toolbar.title = "Unknown"
        binding.errorText.text = resources.getString(R.string.location_error)
        binding.errorText.visibility = View.VISIBLE
        binding.pager.visibility = View.GONE
    }

    private fun setupToolbar(locationName: String) {
        binding.toolbar.title = locationName
    }

    private fun setupMenu(locationName: String, latitude: Double, longitude: Double) {
        locationViewModel.locations.observe(viewLifecycleOwner) { locations ->
            val menu = binding.toolbar.menu
            val addFavoriteItem = menu.findItem(R.id.action_add_favorite)
            val deleteFavoriteItem = menu.findItem(R.id.action_delete_favorite)
            val locationExists = locations.any { it.locationName == locationName }

            addFavoriteItem.isVisible = !locationExists
            deleteFavoriteItem.isVisible = locationExists

            binding.toolbar.setOnMenuItemClickListener { item ->
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
                        refreshWeatherData(latitude, longitude)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun fetchWeatherData() {
        if (latLng != null) {
            val latitude = latLng.latitude
            val longitude = latLng.longitude

            lifecycleScope.launch {
                val weatherApiClient = WeatherApiClient(requireContext(), "f7e942927369dbd7b31e7a69df30b3fd")
                weatherData = weatherApiClient.getWeatherData(latitude, longitude)

                if (weatherData == null) {
                    handleApiError()
                } else {
                    binding.errorText.visibility = View.GONE
                    binding.pager.visibility = View.VISIBLE
                    val adapter = binding.pager.adapter as? MyFragmentAdapter
                    adapter?.setWeatherData(weatherData!!)
                }
            }
        }
    }

    private fun refreshWeatherData(latitude: Double, longitude: Double) {
        lifecycleScope.launch {
            val weatherApiClient = WeatherApiClient(requireContext(), "f7e942927369dbd7b31e7a69df30b3fd")
            val newWeatherData = weatherApiClient.getWeatherData(latitude, longitude)

            if (newWeatherData == null) {
                Toast.makeText(
                    requireContext(),
                    "Unable to refresh data. Try again!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                weatherData = newWeatherData
                val adapter = binding.pager.adapter as? MyFragmentAdapter
                adapter?.setWeatherData(weatherData!!)
                binding.errorText.visibility = View.GONE
                binding.pager.visibility = View.VISIBLE
            }
        }
    }

    private fun handleApiError() {
        binding.errorText.text = resources.getString(R.string.api_error)
        binding.errorText.visibility = View.VISIBLE
        binding.pager.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}