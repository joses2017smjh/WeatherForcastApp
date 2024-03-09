package edu.oregonstate.cs492.assignment4.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_locations")
data class ForecastLocation(
    @PrimaryKey @ColumnInfo(name = "city_name") val cityName: String,
    @ColumnInfo(name = "last_viewed_timestamp") val lastViewedTimestamp: Long
)
