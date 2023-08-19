package com.example.weatherapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.weatherapp.db.LocationDatabase
import com.example.weatherapp.db.LocationRepository

class WeatherApplication : Application() {

    private val database by lazy { LocationDatabase.getDatabase(this) }
    val repository by lazy { LocationRepository(database.locationDao()) }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}