package edu.oregonstate.cs492.assignment4.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ForecastLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(forecastLocation: ForecastLocation)

    @Query("SELECT * FROM forecast_locations ORDER BY last_viewed_timestamp DESC")
    fun getAllLocations(): LiveData<List<ForecastLocation>>

    @Query("UPDATE forecast_locations SET last_viewed_timestamp = :timestamp WHERE city_name = :cityName")
    suspend fun updateTimestamp(cityName: String, timestamp: Long)
}
