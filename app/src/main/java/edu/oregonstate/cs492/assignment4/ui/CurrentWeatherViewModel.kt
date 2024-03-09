package edu.oregonstate.cs492.assignment4.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.oregonstate.cs492.assignment4.data.AppDatabase
import edu.oregonstate.cs492.assignment4.data.CurrentWeatherRepository
import edu.oregonstate.cs492.assignment4.data.FiveDayForecastRepository
import edu.oregonstate.cs492.assignment4.data.ForecastLocation
import edu.oregonstate.cs492.assignment4.data.ForecastPeriod
import edu.oregonstate.cs492.assignment4.data.OpenWeatherService


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * This is a ViewModel class that holds current weather data for the UI.
 */
class CurrentWeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CurrentWeatherRepository

    init {
        // Use getApplication() to get the application context
        val forecastLocationDao = AppDatabase.getDatabase(getApplication()).forecastLocationDao()
        repository = CurrentWeatherRepository(
            OpenWeatherService.create(),
            forecastLocationDao
        )
    }

    /*
     * The most recent response from the OpenWeather current weather API are stored in this
     * private property.  These results are exposed to the outside world in immutable form via the
     * public `forecast` property below.
     */
    private val _weather = MutableLiveData<ForecastPeriod?>(null)

    /**
     * This value provides the most recent response from the OpenWeather current weather API.
     * It is null if there are no current results (e.g. in the case of an error).
     */
    val weather: LiveData<ForecastPeriod?> = _weather

    /*
     * The current error for the most recent API query is stored in this private property.  This
     * error is exposed to the outside world in immutable form via the public `error` property
     * below.
     */
    private val _error = MutableLiveData<Throwable?>(null)

    /**
     * This property provides the error associated with the most recent API query, if there is
     * one.  If there was no error associated with the most recent API query, it will be null.
     */
    val error: LiveData<Throwable?> = _error
    val savedLocations: LiveData<List<ForecastLocation>> = repository.getSavedLocations()

    /*
     * The current loading state is stored in this private property.  This loading state is exposed
     * to the outside world in immutable form via the public `loading` property below.
     */
    private val _loading = MutableLiveData<Boolean>(false)


    /**
     * This property indicates the current loading state of an API query.  It is `true` if an
     * API query is currently being executed or `false` otherwise.
     */
    val loading: LiveData<Boolean> = _loading

    /**
     * This method triggers a new call to the OpenWeather API's current weather method.
     * It updates the public properties of this ViewModel class to reflect the current status
     * of the API query.
     *
     * @param location Specifies the location for which to fetch weather data.  For US cities,
     *   this should be specified as "<city>,<state>,<country>" (e.g. "Corvallis,OR,US"), while
     *   for international cities, it should be specified as "<city>,<country>" (e.g. "London,GB").
     * @param units Specifies the type of units that should be returned by the OpenWeather API.
     *   Can be one of: "standard", "metric", and "imperial".
     * @param apiKey Should be a valid OpenWeather API key.
     */

    fun loadCurrentWeather(location: String?, units: String?, apiKey: String) {
        _loading.value = true
        viewModelScope.launch {
            val result = repository.loadCurrentWeather(location, units, apiKey)
            _loading.value = false
            if (result.isSuccess) {
                _weather.value = result.getOrNull()
                location?.let {
                    repository.saveLocation(it) // Save the location after fetching forecast
                }
            }  else {
                _error.value = result.exceptionOrNull()
            }
        }
    }

}


