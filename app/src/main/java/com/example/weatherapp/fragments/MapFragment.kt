package com.example.weatherapp.fragments

import WeatherApiResponse
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.LocationModelFactory
import com.example.weatherapp.LocationViewModel
import com.example.weatherapp.R
import com.example.weatherapp.WeatherApiClient
import com.example.weatherapp.WeatherApplication
import com.example.weatherapp.databinding.FragmentMapBinding
import com.example.weatherapp.getCityName
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.launch
import java.util.Locale

class MapFragment(
    private val fusedLocationClient: FusedLocationProviderClient,
) : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val locationViewModel: LocationViewModel by activityViewModels {
        LocationModelFactory((requireActivity().application as WeatherApplication).repository)
    }
    private lateinit var googleMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var textView: TextView
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private var weatherData: WeatherApiResponse? = null

    val weatherApiClient = WeatherApiClient("f7e942927369dbd7b31e7a69df30b3fd")

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

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val mapView = binding.root

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map

            try {
                val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireActivity(),
                        R.raw.map_style_json
                    )
                )
                if (!success) {
                    Log.e(ContentValues.TAG, "Style parsing failed.")

                }
            } catch (e: Resources.NotFoundException) {
                Log.e(ContentValues.TAG, "Can't find style. Error: ", e)
            }

            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1
                )
            } else {
                googleMap.isMyLocationEnabled = true
            }

            googleMap.uiSettings.isMapToolbarEnabled = false
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = false


            geocoder = Geocoder(requireActivity(), Locale.getDefault())

            googleMap.setOnMapClickListener { latLng ->
                googleMap.clear()

                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .icon(getBitmapDescriptorFromVector(resources.getDrawable(R.drawable.marker)))
                )

                val city = getCityName(geocoder, latLng.latitude, latLng.longitude)

                if (marker != null) {
                    marker.title = city
                    marker.snippet =
                        null ?: (latLng.latitude.toString() + ", " + latLng.longitude.toString())
                    marker.tag = arrayOf(latLng.latitude, latLng.longitude)
                }

                lifecycleScope.launch {
                    weatherData = weatherApiClient.getWeatherData(
                        (marker!!.tag as Array<Double>)[0],
                        (marker.tag as Array<Double>)[1]
                    )
                }

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

            }

            googleMap.setOnMarkerClickListener { marker ->
                val bottomSheet = BottomSheet(
                    marker.title!!,
                    (marker.tag as Array<Double>)[0],
                    (marker.tag as Array<Double>)[1],
                    weatherData
                )
                bottomSheet.show(childFragmentManager, "TAG1")

                true
            }

        }

        Places.initialize(requireActivity(), "AIzaSyBqQY0NwTHxhCh_JP9R1O2dPSO61sR-l0A")
//        val placesClient = Places.createClient(requireActivity())

        autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        val rootView = autocompleteFragment.view
        rootView?.setBackgroundResource(R.drawable.rounded_background)


        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                autocompleteFragment.setLocationBias(
                    RectangularBounds.newInstance(
                        LatLng(location?.longitude ?: 0.0, location?.longitude ?: 0.0),
                        LatLng(location?.latitude ?: 0.0, location?.latitude ?: 0.0)
                    )
                )
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            (location?.latitude ?: 0.0), location?.longitude ?: 0.0
                        ), 10f
                    )
                )

            }

        autocompleteFragment.setTypesFilter(listOf(PlaceTypes.CITIES))

        textView = binding.textView

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(ContentValues.TAG, "Place: ${place.name}, ${place.id}")
                val latLng = LatLng(
                    place.latLng?.latitude ?: 0.0,
                    place.latLng?.longitude ?: 0.0
                )
                googleMap.clear()

                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .icon(getBitmapDescriptorFromVector(resources.getDrawable(R.drawable.marker)))
                )

                val city = getCityName(geocoder, latLng.latitude, latLng.longitude)

                if (marker != null) {
                    marker.title = city
                    marker.snippet =
                        null ?: (latLng.latitude.toString() + ", " + latLng.longitude.toString())
                    marker.tag = arrayOf(latLng.latitude, latLng.longitude)
                }

                lifecycleScope.launch {
                    weatherData = weatherApiClient.getWeatherData(
                        (marker!!.tag as Array<Double>)[0],
                        (marker.tag as Array<Double>)[1]
                    )
                }

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            }

            override fun onError(status: Status) {
                Log.i(ContentValues.TAG, "An error occurred: $status")
                textView.text = "An error occurred: $status"
            }
        })
        return mapView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    private fun getBitmapDescriptorFromVector(
        vectorDrawable: Drawable,
        scaleFactor: Float = 0.05f
    ): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(
            (vectorDrawable.intrinsicWidth * scaleFactor).toInt(),
            (vectorDrawable.intrinsicHeight * scaleFactor).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}