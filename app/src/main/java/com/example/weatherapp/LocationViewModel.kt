package com.example.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.db.Location
import com.example.weatherapp.db.LocationRepository
import kotlinx.coroutines.launch

class LocationViewModel(private val repository: LocationRepository) : ViewModel() {

    var locations: LiveData<List<Location>> = repository.locations.asLiveData()


    fun addLocation(newLocation: Location) = viewModelScope.launch {
        repository.insertLocation(newLocation)
    }

    fun updateLocation(location: Location) = viewModelScope.launch {
        repository.updateLocation(location)
    }

    fun deleteLocationByData(locationName: String, locationCountry: String) = viewModelScope.launch {
        repository.deleteLocationByData(locationName, locationCountry)
    }
}

class LocationModelFactory(private val repository: LocationRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java))
            return LocationViewModel(repository) as T

        throw IllegalArgumentException()
    }
}