package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.fragments.FavoritesFragment
import com.example.weatherapp.fragments.MapFragment
import com.example.weatherapp.fragments.WeatherFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.RectangularBounds

internal class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapFragment: MapFragment
    private lateinit var weatherFragment: WeatherFragment
    private lateinit var favoritesFragment: FavoritesFragment
    private var currentLatLng: LatLng? = null
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)

        mapFragment = MapFragment(fusedLocationClient)

        weatherFragment = WeatherFragment(null)

        favoritesFragment = FavoritesFragment()

        getCurrentLocation()

        if (savedInstanceState == null) {
            loadFragment(mapFragment)
        }

        val bottomNavigation = binding.bottomNavigation
        bottomNavigation.selectedItemId = R.id.page_2

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    showWeatherFragment()
                    true
                }

                R.id.page_2 -> {
                    showMapFragment()
                    true
                }

                R.id.page_3 -> {
                    showFavoritesFragment()
                    true
                }

                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showWeatherFragment() {
        supportFragmentManager.beginTransaction().apply {
            hide(mapFragment)
            hide(favoritesFragment)
            weatherFragment.let {
                if (!it.isAdded) {
                    add(R.id.fragment_container, it)
                }
                show(it)
            }
            commit()
        }
    }

    private fun showMapFragment() {
        supportFragmentManager.beginTransaction().apply {
            hide(weatherFragment)
            hide(favoritesFragment)
            mapFragment.let {
                if (!it.isAdded) {
                    add(R.id.fragment_container, it)
                }
                show(it)
            }
            commit()
        }
    }

    private fun showFavoritesFragment() {
        supportFragmentManager.beginTransaction().apply {
            hide(mapFragment)
            hide(weatherFragment)
            favoritesFragment.let {
                if (!it.isAdded) {
                    add(R.id.fragment_container, it)
                }
                show(it)
            }
            commit()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLatLng = LatLng(location.latitude, location.longitude)
                    weatherFragment = WeatherFragment(currentLatLng!!)
                    showWeatherFragment()
                    showMapFragment()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) else {
            if (requestCode == 1000) {

                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        currentLatLng = LatLng(location.latitude, location.longitude)
                        weatherFragment = WeatherFragment(currentLatLng!!)
                        showWeatherFragment()
                        showMapFragment()

                        mapFragment.googleMap.isMyLocationEnabled = true

                        mapFragment.autocompleteFragment.setLocationBias(
                            RectangularBounds.newInstance(
                                LatLng(location.longitude, location.longitude),
                                LatLng(location.latitude, location.latitude)
                            )
                        )
                        mapFragment.googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    (location.latitude), location.longitude
                                ), 10f
                            )
                        )
                    }
                }
            }
        }
    }
}