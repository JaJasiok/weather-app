package com.example.weatherapp

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.Locale


internal class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
//    private lateinit var mapSearchView: SearchView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var textView: TextView
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private var isGestureInProgress: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1
            )
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map

            googleMap.uiSettings.isMapToolbarEnabled = false
            googleMap.uiSettings.isZoomControlsEnabled = true

            geocoder = Geocoder(this@MainActivity, Locale.getDefault())

            googleMap.setOnMapLongClickListener { latLng ->
                googleMap.clear()

                val marker = googleMap.addMarker(
                    MarkerOptions().position(latLng)
                )

                val city = getCityName(latLng.latitude, latLng.longitude)


                if (marker != null) {
                    marker.title = city ?: "Marker"
                    marker.snippet =
                        null ?: (latLng.latitude.toString() + ", " + latLng.longitude.toString())
                    marker.tag = latLng
                }

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

            }

            googleMap.setOnInfoWindowClickListener { marker ->
                // Handle info window click event here
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_PLACE_NAME, marker.title)
                intent.putExtra(DetailActivity.EXTRA_PLACE_LATLNG, marker.snippet)
                startActivity(intent)
            }
        }

        Places.initialize(applicationContext, "AIzaSyBqQY0NwTHxhCh_JP9R1O2dPSO61sR-l0A")
        val placesClient = Places.createClient(this)

        autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        val rootView = autocompleteFragment.view
        rootView?.setBackgroundResource(R.drawable.rounded_background);


        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                autocompleteFragment.setLocationBias(
                    RectangularBounds.newInstance(
                        LatLng(location?.longitude ?: 0.0, location?.longitude ?: 0.0),
                        LatLng(location?.latitude ?: 0.0, location?.latitude ?: 0.0)))
                Toast.makeText(applicationContext, "NewLocation",Toast.LENGTH_LONG).show()
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng((location?.latitude ?: 0.0),location?.longitude ?: 0.0), 10f))

            }

        autocompleteFragment.setTypesFilter(listOf(PlaceTypes.CITIES))

        textView = findViewById(R.id.text_view)

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
                Toast.makeText(applicationContext, "Toast1",Toast.LENGTH_LONG).show()

                val latLng: LatLng = LatLng(place.latLng?.latitude ?: 0.0,
                    place.latLng?.longitude ?: 0.0
                )
                googleMap.clear()

                googleMap.addMarker(MarkerOptions().position(latLng).title(place.name).snippet(null ?: (latLng.latitude.toString() + ", " + latLng.longitude.toString())
                ))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
                Toast.makeText(applicationContext, "An error occurred: $status", Toast.LENGTH_LONG).show()
                textView.text = "An error occurred: $status"
            }
        })

    }

//    override fun onBackPressed() {
//        if (isGestureInProgress) {
//            isGestureInProgress = false
//            return
//        }
//
//        if (autocompleteFragment.view?.hasFocus() == true) {
//            autocompleteFragment.view?.clearFocus()
//            return
//        }
//
//        super.onBackPressed()
//    }
//
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        isGestureInProgress = !hasFocus
//    }

    private fun getCityName(latitude: Double, longitude: Double): String? {
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val cityName = address.locality ?: address.subAdminArea
                    return cityName
                }
            }
        } catch (e: Exception) {
            Log.e("Geocoding", "Error: ${e.message}")
            textView.text = e.message
        }
        return null
    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }
}
