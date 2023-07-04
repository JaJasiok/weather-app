package com.example.weatherapp

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
import org.json.JSONObject
import java.util.Locale

internal class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var textView: TextView
    private lateinit var autocompleteFragment: AutocompleteSupportFragment


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

            try {
                val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this,
                        R.raw.map_style_json
                    )
                )
                if (!success) {
                    Log.e(TAG, "Style parsing failed.")

                }
            } catch (e: NotFoundException) {
                Log.e(TAG, "Can't find style. Error: ", e)
            }

            googleMap.isMyLocationEnabled = true

            googleMap.uiSettings.isMapToolbarEnabled = false
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true


            geocoder = Geocoder(this@MainActivity, Locale.getDefault())

            googleMap.setOnMapLongClickListener { latLng ->
                googleMap.clear()

                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .icon(getBitmapDescriptorFromVector(resources.getDrawable(R.drawable.marker)))
                )

                val city = getCityName(latLng.latitude, latLng.longitude)

                if (marker != null) {
                    marker.title = city ?: "Marker"
                    marker.snippet =
                        null ?: (latLng.latitude.toString() + ", " + latLng.longitude.toString())
                    marker.tag = arrayOf(latLng.latitude, latLng.longitude)
                }

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

            }

            googleMap.setOnInfoWindowClickListener { marker ->
                val weatherApiClient = WeatherApiClient("f7e942927369dbd7b31e7a69df30b3fd")
                val json = JSONObject(
                    "{\"lat\":52.4,\"lon\":16.9,\"timezone\":\"Europe/Warsaw\",\"timezone_offset\":7200,\"current\":{\"dt\":1688038226,\"sunrise\":1688005969,\"sunset\":1688066309,\"temp\":24.14,\"feels_like\":23.86,\"pressure\":1015,\"humidity\":48,\"dew_point\":12.45,\"uvi\":6.66,\"clouds\":0,\"visibility\":10000,\"wind_speed\":4.63,\"wind_deg\":290,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}]},\"hourly\":[{\"dt\":1688036400,\"temp\":24.13,\"feels_like\":23.83,\"pressure\":1015,\"humidity\":47,\"dew_point\":12.12,\"uvi\":7.12,\"clouds\":11,\"visibility\":10000,\"wind_speed\":3.46,\"wind_deg\":280,\"wind_gust\":3.93,\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"pop\":0},{\"dt\":1688040000,\"temp\":24.14,\"feels_like\":23.86,\"pressure\":1015,\"humidity\":48,\"dew_point\":12.45,\"uvi\":6.66,\"clouds\":0,\"visibility\":10000,\"wind_speed\":3.41,\"wind_deg\":279,\"wind_gust\":4.01,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"pop\":0},{\"dt\":1688043600,\"temp\":24.36,\"feels_like\":24.05,\"pressure\":1015,\"humidity\":46,\"dew_point\":12.01,\"uvi\":5.57,\"clouds\":1,\"visibility\":10000,\"wind_speed\":3.18,\"wind_deg\":278,\"wind_gust\":4.2,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"pop\":0},{\"dt\":1688047200,\"temp\":24.74,\"feels_like\":24.42,\"pressure\":1014,\"humidity\":44,\"dew_point\":11.68,\"uvi\":4.09,\"clouds\":3,\"visibility\":10000,\"wind_speed\":2.89,\"wind_deg\":278,\"wind_gust\":4.22,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"pop\":0},{\"dt\":1688050800,\"temp\":25.12,\"feels_like\":24.76,\"pressure\":1013,\"humidity\":41,\"dew_point\":10.95,\"uvi\":2.58,\"clouds\":8,\"visibility\":10000,\"wind_speed\":2.74,\"wind_deg\":275,\"wind_gust\":4.13,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"pop\":0},{\"dt\":1688054400,\"temp\":24.89,\"feels_like\":24.53,\"pressure\":1013,\"humidity\":42,\"dew_point\":11.11,\"uvi\":0.87,\"clouds\":21,\"visibility\":10000,\"wind_speed\":2.89,\"wind_deg\":259,\"wind_gust\":3.76,\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"pop\":0},{\"dt\":1688058000,\"temp\":22.63,\"feels_like\":22.31,\"pressure\":1012,\"humidity\":52,\"dew_point\":12.21,\"uvi\":0.38,\"clouds\":41,\"visibility\":10000,\"wind_speed\":2.51,\"wind_deg\":270,\"wind_gust\":3.82,\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"pop\":0},{\"dt\":1688061600,\"temp\":21.22,\"feels_like\":20.89,\"pressure\":1012,\"humidity\":57,\"dew_point\":12.34,\"uvi\":0.12,\"clouds\":51,\"visibility\":10000,\"wind_speed\":2.44,\"wind_deg\":321,\"wind_gust\":3.12,\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"pop\":0},{\"dt\":1688065200,\"temp\":20.55,\"feels_like\":20.12,\"pressure\":1012,\"humidity\":56,\"dew_point\":11.6,\"uvi\":0,\"clouds\":100,\"visibility\":10000,\"wind_speed\":1.53,\"wind_deg\":1,\"wind_gust\":1.71,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"pop\":0},{\"dt\":1688068800,\"temp\":20,\"feels_like\":19.52,\"pressure\":1012,\"humidity\":56,\"dew_point\":11,\"uvi\":0,\"clouds\":97,\"visibility\":10000,\"wind_speed\":1.35,\"wind_deg\":54,\"wind_gust\":1.32,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"pop\":0},{\"dt\":1688072400,\"temp\":19.55,\"feels_like\":19.08,\"pressure\":1012,\"humidity\":58,\"dew_point\":11.21,\"uvi\":0,\"clouds\":98,\"visibility\":10000,\"wind_speed\":1.52,\"wind_deg\":81,\"wind_gust\":1.52,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"pop\":0},{\"dt\":1688076000,\"temp\":19.38,\"feels_like\":18.94,\"pressure\":1012,\"humidity\":60,\"dew_point\":11.4,\"uvi\":0,\"clouds\":99,\"visibility\":10000,\"wind_speed\":1.33,\"wind_deg\":98,\"wind_gust\":1.4,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"pop\":0},{\"dt\":1688079600,\"temp\":19.22,\"feels_like\":18.76,\"pressure\":1012,\"humidity\":60,\"dew_point\":11.31,\"uvi\":0,\"clouds\":99,\"visibility\":10000,\"wind_speed\":1.04,\"wind_deg\":147,\"wind_gust\":1.03,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"pop\":0},{\"dt\":1688083200,\"temp\":18.99,\"feels_like\":18.54,\"pressure\":1011,\"humidity\":61,\"dew_point\":11.14,\"uvi\":0,\"clouds\":99,\"visibility\":10000,\"wind_speed\":1.37,\"wind_deg\":157,\"wind_gust\":1.4,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"pop\":0},{\"dt\":1688086800,\"temp\":18.72,\"feels_like\":18.24,\"pressure\":1011,\"humidity\":61,\"dew_point\":11.14,\"uvi\":0,\"clouds\":97,\"visibility\":10000,\"wind_speed\":1.2,\"wind_deg\":189,\"wind_gust\":1.32,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"pop\":0},{\"dt\":1688090400,\"temp\":18.02,\"feels_like\":17.52,\"pressure\":1011,\"humidity\":63,\"dew_point\":11.04,\"uvi\":0,\"clouds\":88,\"visibility\":10000,\"wind_speed\":1.74,\"wind_deg\":245,\"wind_gust\":1.7,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"pop\":0},{\"dt\":1688094000,\"temp\":16.49,\"feels_like\":16.05,\"pressure\":1010,\"humidity\":71,\"dew_point\":11.34,\"uvi\":0,\"clouds\":92,\"visibility\":10000,\"wind_speed\":1.69,\"wind_deg\":249,\"wind_gust\":1.72,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"pop\":0},{\"dt\":1688097600,\"temp\":17.27,\"feels_like\":16.96,\"pressure\":1010,\"humidity\":73,\"dew_point\":12.34,\"uvi\":0.21,\"clouds\":94,\"visibility\":10000,\"wind_speed\":2.31,\"wind_deg\":251,\"wind_gust\":3.1,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"pop\":0},{\"dt\":1688101200,\"temp\":19.42,\"feels_like\":19.17,\"pressure\":1010,\"humidity\":67,\"dew_point\":13.04,\"uvi\":0.63,\"clouds\":85,\"visibility\":10000,\"wind_speed\":2.17,\"wind_deg\":248,\"wind_gust\":3.21,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"pop\":0},{\"dt\":1688104800,\"temp\":21.24,\"feels_like\":21.01,\"pressure\":1010,\"humidity\":61,\"dew_point\":13.4,\"uvi\":1.41,\"clouds\":84,\"visibility\":10000,\"wind_speed\":2.59,\"wind_deg\":255,\"wind_gust\":3.63,\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"pop\":0},{\"dt\":1688108400,\"temp\":22.95,\"feels_like\":22.76,\"pressure\":1010,\"humidity\":56,\"dew_point\":13.64,\"uvi\":0.96,\"clouds\":73,\"visibility\":10000,\"wind_speed\":2.55,\"wind_deg\":257,\"wind_gust\":3.41,\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"pop\":0},{\"dt\":1688112000,\"temp\":24.12,\"feels_like\":23.95,\"pressure\":1010,\"humidity\":52,\"dew_point\":13.74,\"uvi\":1.46,\"clouds\":83,\"visibility\":10000,\"wind_speed\":2.76,\"wind_deg\":262,\"wind_gust\":3.21,\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"pop\":0},{\"dt\":1688115600,\"temp\":24.79,\"feels_like\":24.66,\"pressure\":1010,\"humidity\":51,\"dew_point\":13.94,\"uvi\":1.95,\"clouds\":80,\"visibility\":10000,\"wind_speed\":4.29,\"wind_deg\":262,\"wind_gust\":4.3,\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"pop\":0},{\"dt\":1688119200,\"temp\":22.89,\"feels_like\":22.75,\"pressure\":1010,\"humidity\":58,\"dew_point\":14.24,\"uvi\":3.89,\"clouds\":85,\"visibility\":10000,\"wind_speed\":4.24,\"wind_deg\":276,\"wind_gust\":4.02,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"pop\":0},{\"dt\":1688122800,\"temp\":24.67,\"feels_like\":24.58,\"pressure\":1009,\"humidity\":53,\"dew_point\":14.54,\"uvi\":4.07,\"clouds\":81,\"visibility\":10000,\"wind_speed\":2.95,\"wind_deg\":278,\"wind_gust\":3.53,\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"pop\":0},{\"dt\":1688126400,\"temp\":24.07,\"feels_like\":24.02,\"pressure\":1009,\"humidity\":57,\"dew_point\":15.04,\"uvi\":3.8,\"clouds\":84,\"visibility\":10000,\"wind_speed\":3.71,\"wind_deg\":270,\"wind_gust\":4.52,\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"pop\":0},{\"dt\":1688130000,\"temp\":21.97,\"feels_like\":21.95,\"pressure\":1009,\"humidity\":66,\"dew_point\":15.5,\"uvi\":1.98,\"clouds\":100,\"visibility\":10000,\"wind_speed\":3.83,\"wind_deg\":266,\"wind_gust\":4.73,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"pop\":0.4},{\"dt\":1688133600,\"temp\":21.52,\"feels_like\":21.53,\"pressure\":1008,\"humidity\":69,\"dew_point\":15.74,\"uvi\":1.46,\"clouds\":100,\"visibility\":10000,\"wind_speed\":4.81,\"wind_deg\":270,\"wind_gust\":6.64,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"pop\":0.6,\"rain\":{\"1h\":0.19}},{\"dt\":1688137200,\"temp\":18.92,\"feels_like\":19.17,\"pressure\":1009,\"humidity\":88,\"dew_point\":16.94,\"uvi\":0.92,\"clouds\":100,\"visibility\":5319,\"wind_speed\":5.61,\"wind_deg\":269,\"wind_gust\":8.62,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"pop\":0.84,\"rain\":{\"1h\":0.94}},{\"dt\":1688140800,\"temp\":17.76,\"feels_like\":18.1,\"pressure\":1009,\"humidity\":96,\"dew_point\":17.24,\"uvi\":0.2,\"clouds\":100,\"visibility\":10000,\"wind_speed\":4.41,\"wind_deg\":292,\"wind_gust\":9.33,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"pop\":1,\"rain\":{\"1h\":3.56}},{\"dt\":1688144400,\"temp\":17.49,\"feels_like\":17.8,\"pressure\":1009,\"humidity\":96,\"dew_point\":16.94,\"uvi\":0.09,\"clouds\":100,\"visibility\":10000,\"wind_speed\":3.24,\"wind_deg\":312,\"wind_gust\":7.02,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"pop\":1,\"rain\":{\"1h\":0.63}},{\"dt\":1688148000,\"temp\":17.26,\"feels_like\":17.57,\"pressure\":1009,\"humidity\":97,\"dew_point\":16.74,\"uvi\":0.03,\"clouds\":100,\"visibility\":10000,\"wind_speed\":2.16,\"wind_deg\":285,\"wind_gust\":4.9,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"pop\":1,\"rain\":{\"1h\":0.38}},{\"dt\":1688151600,\"temp\":17.12,\"feels_like\":17.39,\"pressure\":1009,\"humidity\":96,\"dew_point\":16.54,\"uvi\":0,\"clouds\":100,\"visibility\":10000,\"wind_speed\":2.24,\"wind_deg\":268,\"wind_gust\":4.3,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"pop\":0.12},{\"dt\":1688155200,\"temp\":16.52,\"feels_like\":16.76,\"pressure\":1009,\"humidity\":97,\"dew_point\":16.14,\"uvi\":0,\"clouds\":100,\"visibility\":10000,\"wind_speed\":2.52,\"wind_deg\":276,\"wind_gust\":3.73,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"pop\":0.12},{\"dt\":1688158800,\"temp\":16.52,\"feels_like\":16.79,\"pressure\":1009,\"humidity\":98,\"dew_point\":16.14,\"uvi\":0,\"clouds\":100,\"visibility\":10000,\"wind_speed\":1.88,\"wind_deg\":286,\"wind_gust\":2.91,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"pop\":0.24},{\"dt\":1688162400,\"temp\":16.66,\"feels_like\":16.91,\"pressure\":1009,\"humidity\":97,\"dew_point\":16.24,\"uvi\":0,\"clouds\":100,\"visibility\":10000,\"wind_speed\":1.62,\"wind_deg\":310,\"wind_gust\":2.81,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"pop\":0.12},{\"dt\":1688166000,\"temp\":16.69,\"feels_like\":16.92,\"pressure\":1009,\"humidity\":96,\"dew_point\":16.01,\"uvi\":0,\"clouds\":100,\"visibility\":8203,\"wind_speed\":1.56,\"wind_deg\":334,\"wind_gust\":2.61,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"pop\":0.28,\"rain\":{\"1h\":0.38}},{\"dt\":1688169600,\"temp\":16.42,\"feels_like\":16.62,\"pressure\":1009,\"humidity\":96,\"dew_point\":15.84,\"uvi\":0,\"clouds\":100,\"visibility\":10000,\"wind_speed\":1.76,\"wind_deg\":317,\"wind_gust\":2.62,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"pop\":0.4,\"rain\":{\"1h\":0.5}},{\"dt\":1688173200,\"temp\":16.02,\"feels_like\":16.21,\"pressure\":1009,\"humidity\":97,\"dew_point\":15.44,\"uvi\":0,\"clouds\":100,\"visibility\":10000,\"wind_speed\":2.09,\"wind_deg\":315,\"wind_gust\":3.7,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"pop\":0.6,\"rain\":{\"1h\":0.63}},{\"dt\":1688176800,\"temp\":15.99,\"feels_like\":16.15,\"pressure\":1008,\"humidity\":96,\"dew_point\":15.34,\"uvi\":0,\"clouds\":100,\"visibility\":10000,\"wind_speed\":1.92,\"wind_deg\":287,\"wind_gust\":3.8,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10n\"}],\"pop\":0.84,\"rain\":{\"1h\":0.19}},{\"dt\":1688180400,\"temp\":15.87,\"feels_like\":16.05,\"pressure\":1008,\"humidity\":97,\"dew_point\":15.34,\"uvi\":0,\"clouds\":100,\"visibility\":10000,\"wind_speed\":2.31,\"wind_deg\":275,\"wind_gust\":4.9,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"pop\":0.8,\"rain\":{\"1h\":0.5}},{\"dt\":1688184000,\"temp\":15.69,\"feels_like\":15.82,\"pressure\":1008,\"humidity\":96,\"dew_point\":15,\"uvi\":0.21,\"clouds\":91,\"visibility\":10000,\"wind_speed\":2.84,\"wind_deg\":275,\"wind_gust\":5.32,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"pop\":0.72,\"rain\":{\"1h\":0.25}},{\"dt\":1688187600,\"temp\":15.94,\"feels_like\":16.02,\"pressure\":1008,\"humidity\":93,\"dew_point\":14.84,\"uvi\":0.61,\"clouds\":92,\"visibility\":10000,\"wind_speed\":3.02,\"wind_deg\":272,\"wind_gust\":4.7,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"pop\":0.76},{\"dt\":1688191200,\"temp\":17.08,\"feels_like\":17.04,\"pressure\":1008,\"humidity\":84,\"dew_point\":14.44,\"uvi\":1.37,\"clouds\":94,\"visibility\":10000,\"wind_speed\":3.88,\"wind_deg\":261,\"wind_gust\":4.7,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"pop\":0.72,\"rain\":{\"1h\":0.13}},{\"dt\":1688194800,\"temp\":18.04,\"feels_like\":17.94,\"pressure\":1008,\"humidity\":78,\"dew_point\":14.14,\"uvi\":2.62,\"clouds\":100,\"visibility\":10000,\"wind_speed\":4.2,\"wind_deg\":256,\"wind_gust\":5.01,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"pop\":0.4},{\"dt\":1688198400,\"temp\":19.24,\"feels_like\":19,\"pressure\":1008,\"humidity\":68,\"dew_point\":13.3,\"uvi\":3.99,\"clouds\":100,\"visibility\":10000,\"wind_speed\":4.41,\"wind_deg\":256,\"wind_gust\":5.02,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"pop\":0.4},{\"dt\":1688202000,\"temp\":20.64,\"feels_like\":20.25,\"pressure\":1007,\"humidity\":57,\"dew_point\":11.8,\"uvi\":5.31,\"clouds\":98,\"visibility\":10000,\"wind_speed\":4.5,\"wind_deg\":258,\"wind_gust\":5.2,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"pop\":0.32},{\"dt\":1688205600,\"temp\":21.72,\"feels_like\":21.23,\"pressure\":1007,\"humidity\":49,\"dew_point\":10.64,\"uvi\":6.47,\"clouds\":95,\"visibility\":10000,\"wind_speed\":4.45,\"wind_deg\":254,\"wind_gust\":5.3,\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}],\"pop\":0.32}],\"daily\":[{\"dt\":1688032800,\"sunrise\":1688005969,\"sunset\":1688066309,\"moonrise\":1688050620,\"moonset\":1687994760,\"moon_phase\":0.35,\"summary\":\"Expect a day of partly cloudy with clear spells\",\"temp\":{\"day\":23.76,\"min\":12.12,\"max\":25.12,\"night\":19.55,\"eve\":24.89,\"morn\":13.05},\"feels_like\":{\"day\":23.44,\"night\":19.08,\"eve\":24.53,\"morn\":12.79},\"pressure\":1015,\"humidity\":48,\"dew_point\":12.11,\"wind_speed\":3.5,\"wind_deg\":310,\"wind_gust\":8.31,\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"clouds\":28,\"pop\":0.08,\"uvi\":7.12},{\"dt\":1688119200,\"sunrise\":1688092405,\"sunset\":1688152695,\"moonrise\":1688142240,\"moonset\":1688082180,\"moon_phase\":0.39,\"summary\":\"Expect a day of partly cloudy with rain\",\"temp\":{\"day\":22.89,\"min\":16.49,\"max\":24.79,\"night\":16.52,\"eve\":17.76,\"morn\":17.27},\"feels_like\":{\"day\":22.75,\"night\":16.79,\"eve\":18.1,\"morn\":16.96},\"pressure\":1010,\"humidity\":58,\"dew_point\":14.24,\"wind_speed\":5.61,\"wind_deg\":269,\"wind_gust\":9.33,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"clouds\":85,\"pop\":1,\"rain\":5.7,\"uvi\":4.07},{\"dt\":1688205600,\"sunrise\":1688178844,\"sunset\":1688239079,\"moonrise\":1688233860,\"moonset\":1688169960,\"moon_phase\":0.42,\"summary\":\"Expect a day of partly cloudy with rain\",\"temp\":{\"day\":21.72,\"min\":15.69,\"max\":24,\"night\":17.87,\"eve\":21.55,\"morn\":15.69},\"feels_like\":{\"day\":21.23,\"night\":17.36,\"eve\":21.12,\"morn\":15.82},\"pressure\":1007,\"humidity\":49,\"dew_point\":10.64,\"wind_speed\":6.15,\"wind_deg\":253,\"wind_gust\":10.1,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"clouds\":95,\"pop\":0.84,\"rain\":2.58,\"uvi\":6.77},{\"dt\":1688292000,\"sunrise\":1688265286,\"sunset\":1688325458,\"moonrise\":1688324940,\"moonset\":1688258520,\"moon_phase\":0.46,\"summary\":\"Expect a day of partly cloudy with rain\",\"temp\":{\"day\":19.56,\"min\":14.22,\"max\":21.17,\"night\":15.42,\"eve\":20.39,\"morn\":14.68},\"feels_like\":{\"day\":18.9,\"night\":14.32,\"eve\":19.89,\"morn\":14.58},\"pressure\":1006,\"humidity\":51,\"dew_point\":9.02,\"wind_speed\":8.81,\"wind_deg\":257,\"wind_gust\":13.11,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"clouds\":30,\"pop\":0.76,\"rain\":0.82,\"uvi\":5.83},{\"dt\":1688378400,\"sunrise\":1688351731,\"sunset\":1688411835,\"moonrise\":1688415000,\"moonset\":1688348280,\"moon_phase\":0.5,\"summary\":\"Expect a day of partly cloudy with clear spells\",\"temp\":{\"day\":19.69,\"min\":10.58,\"max\":24.27,\"night\":15.8,\"eve\":24.27,\"morn\":10.58},\"feels_like\":{\"day\":18.79,\"night\":15.08,\"eve\":23.64,\"morn\":9.78},\"pressure\":1013,\"humidity\":41,\"dew_point\":6.24,\"wind_speed\":6.82,\"wind_deg\":247,\"wind_gust\":10.2,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"clouds\":1,\"pop\":0,\"uvi\":6.74},{\"dt\":1688464800,\"sunrise\":1688438178,\"sunset\":1688498209,\"moonrise\":1688503860,\"moonset\":1688439240,\"moon_phase\":0.54,\"summary\":\"There will be partly cloudy today\",\"temp\":{\"day\":22.32,\"min\":13.23,\"max\":25.82,\"night\":18.2,\"eve\":25.82,\"morn\":13.23},\"feels_like\":{\"day\":21.7,\"night\":17.64,\"eve\":25.37,\"morn\":12.46},\"pressure\":1015,\"humidity\":42,\"dew_point\":8.85,\"wind_speed\":4.85,\"wind_deg\":244,\"wind_gust\":6.9,\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"clouds\":28,\"pop\":0,\"uvi\":7},{\"dt\":1688551200,\"sunrise\":1688524628,\"sunset\":1688584579,\"moonrise\":1688591940,\"moonset\":1688531100,\"moon_phase\":0.58,\"summary\":\"You can expect partly cloudy in the morning, with rain in the afternoon\",\"temp\":{\"day\":27.65,\"min\":17.05,\"max\":30.95,\"night\":17.63,\"eve\":29.83,\"morn\":17.05},\"feels_like\":{\"day\":27.84,\"night\":17.88,\"eve\":28.91,\"morn\":16.59},\"pressure\":1013,\"humidity\":47,\"dew_point\":15.41,\"wind_speed\":7.01,\"wind_deg\":199,\"wind_gust\":13,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"clouds\":100,\"pop\":0.92,\"rain\":3.31,\"uvi\":7},{\"dt\":1688637600,\"sunrise\":1688611080,\"sunset\":1688670946,\"moonrise\":1688679480,\"moonset\":1688623200,\"moon_phase\":0.61,\"summary\":\"There will be rain until morning, then partly cloudy\",\"temp\":{\"day\":20.79,\"min\":11.61,\"max\":25.53,\"night\":18.51,\"eve\":25.53,\"morn\":11.61},\"feels_like\":{\"day\":20.1,\"night\":17.75,\"eve\":25,\"morn\":11.33},\"pressure\":1020,\"humidity\":45,\"dew_point\":8.41,\"wind_speed\":6.19,\"wind_deg\":291,\"wind_gust\":11.5,\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"clouds\":83,\"pop\":0.8,\"uvi\":7}]}"
                )
                var weatherData = weatherApiClient.parseWeatherApiResponse(json);

                val bottomSheet = BottomSheet(marker.title!!, weatherData)
                bottomSheet.show(supportFragmentManager, "TAG1")
            }
        }

        Places.initialize(applicationContext, "AIzaSyBqQY0NwTHxhCh_JP9R1O2dPSO61sR-l0A")
        val placesClient = Places.createClient(this)

        autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        val rootView = autocompleteFragment.view
        rootView?.setBackgroundResource(R.drawable.rounded_background);


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

        textView = findViewById(R.id.text_view)

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
                val latLng: LatLng = LatLng(
                    place.latLng?.latitude ?: 0.0,
                    place.latLng?.longitude ?: 0.0
                )
                googleMap.clear()

                googleMap.addMarker(
                    MarkerOptions().position(latLng).title(place.name).icon(getBitmapDescriptorFromVector(resources.getDrawable(R.drawable.marker))).snippet(
                        null ?: (latLng.latitude.toString() + ", " + latLng.longitude.toString())
                    )
                )
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
                textView.text = "An error occurred: $status"
            }
        })

    }


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

    private fun getBitmapDescriptorFromVector(
        vectorDrawable: Drawable,
        scaleFactor: Float = 0.05f
    ): BitmapDescriptor? {
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
}

