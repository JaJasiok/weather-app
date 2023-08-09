package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.db.LocationDatabase
import com.example.weatherapp.db.LocationRepository

class WeatherApplication : Application() {

    private val database by lazy { LocationDatabase.getDatabase(this) }
    val repository by lazy { LocationRepository(database.locationDao()) }
}