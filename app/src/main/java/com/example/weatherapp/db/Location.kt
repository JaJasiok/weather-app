package com.example.weatherapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Location(
    @ColumnInfo(name = "name")
    var locationName: String,
    @ColumnInfo(name = "Latitude")
    var locationLat: Double,
    @ColumnInfo(name = "Longitude")
    var locationLng: Double,
    @ColumnInfo(name = "dt")
    var locationDt: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)

