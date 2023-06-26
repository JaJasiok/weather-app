package com.example.weatherapp

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
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
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.io.IOException

internal class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
//    private lateinit var mapSearchView: SearchView
    private lateinit var fusedLocationClient: FusedLocationProviderClient


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
        mapFragment.getMapAsync(this)

        Places.initialize(applicationContext, "AIzaSyBqQY0NwTHxhCh_JP9R1O2dPSO61sR-l0A")
        val placesClient = Places.createClient(this)

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                autocompleteFragment.setLocationBias(
                    RectangularBounds.newInstance(
                        LatLng(location?.longitude ?: 0.0, location?.longitude ?: 0.0),
                        LatLng(location?.latitude ?: 0.0, location?.latitude ?: 0.0)))
                Toast.makeText(applicationContext, "NewLocation",Toast.LENGTH_LONG).show()
            }

        autocompleteFragment.setTypesFilter(listOf(PlaceTypes.CITIES))

        val textView = findViewById<TextView>(R.id.text_view)

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
                Toast.makeText(applicationContext, "Toast1",Toast.LENGTH_LONG).show()

                val latLng: LatLng = LatLng(place.latLng?.longitude ?: 0.0,
                    place.latLng?.latitude ?: 0.0
                )
                mMap.addMarker(MarkerOptions().position(latLng).title(place.name))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
                Toast.makeText(applicationContext, "An error occurred: $status", Toast.LENGTH_LONG).show()
                textView.text = "An error occurred: $status"
            }


        })


//        mapSearchView = findViewById(R.id.search)
//
//        mapSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//
//                var location: String? = mapSearchView.query.toString()
//                var addressList: List<Address>? = null
//
//                if (location != null) {
//                    val geocoder = Geocoder(this@MainActivity)
//
//                    try {
//                        addressList = geocoder.getFromLocationName(location, 1)
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                    var address: Address? = addressList?.get(0)
//                    val latLng: LatLng = LatLng(address?.latitude ?: 0.0, address?.longitude ?: 0.0)
//                    mMap.addMarker(MarkerOptions().position(latLng).title(location))
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
//                }
//
////                mapSearchView.setQuery("", false)
//
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//        })

        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}
