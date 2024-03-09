package edu.oregonstate.cs492.assignment4.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.oregonstate.cs492.assignment4.data.AppDatabase
import edu.oregonstate.cs492.assignment4.data.FiveDayForecastRepository
import edu.oregonstate.cs492.assignment4.data.ForecastLocation
import edu.oregonstate.cs492.assignment4.data.OpenWeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FiveDayForecastRepository
    private val _savedLocations = MutableLiveData<List<ForecastLocation>>()
    val savedLocations: LiveData<List<ForecastLocation>> = _savedLocations

    init {
        val weatherService = OpenWeatherService.create()
        val forecastLocationDao = AppDatabase.getDatabase(getApplication()).forecastLocationDao()
        repository = FiveDayForecastRepository(weatherService, forecastLocationDao, Dispatchers.IO)
    }

    fun updateSavedLocations(locations: List<ForecastLocation>) {
        _savedLocations.value = locations
    }

    fun updateLocationTimestamp(cityName: String) {
        viewModelScope.launch {
            val newTimestamp = System.currentTimeMillis()
            repository.updateLocationTimestamp(cityName, newTimestamp)
        }
    }
}