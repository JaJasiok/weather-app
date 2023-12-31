package com.example.weatherapp

import android.content.Context
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.util.Log
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs


fun getIconNameHour(
    id: Int,
    dt: Long,
    sunriseToday: Long?,
    sunsetToday: Long?,
    sunriseTomorrow: Long?,
    clouds: Int
): String {
    val dayTime: String

    if (dt < sunriseToday!! || (dt > sunsetToday!! && dt < sunriseTomorrow!!)) {
        dayTime = "night"
    } else {
        dayTime = "day"
    }

    var name = ""

    if (id in (200..232)) {
        name = "thunderstorms"

        if (clouds < 25) {
            name += "_$dayTime"
        }

        if (id in (230..232)) {
            name += "_rain"
        }
    } else if (id in (300..321)) {
        name = "drizzle"

        if (clouds < 25) {
            name = "partly_cloudy_${dayTime}_drizzle"
        }
    } else if (id in (500..531)) {
        name = "rain"

        if (clouds < 25) {
            name = "partly_cloudy_${dayTime}_rain"
        }
    } else if (id in (600..622)) {
        name = "snow"

        if (clouds < 25) {
            name = "partly_cloudy_${dayTime}_snow"
        }

        if (id in (611..616)) {
            name = "sleet"

            if (clouds < 25) {
                name = "partly_cloudy_${dayTime}_sleet"
            }
        }
    } else if (id == 701) {
        name = "mist"
    } else if (id == 711) {
        name = "smoke"

        if (clouds < 25) {
            name = "partly_cloudy_${dayTime}_smoke"
        }
    } else if (id == 721) {
        name = "haze_${dayTime}"

        if (clouds > 25) {
            name = "partly_cloudy_{$dayTime}_haze"
        }
        if (clouds > 50) {
            name = "haze"
        }
    } else if (id == 741) {
        name = "fog_${dayTime}"

        if (clouds > 25) {
            name = "partly_cloudy_{$dayTime}_fog"
        }
        if (clouds > 50) {
            name = "fog"
        }
    } else if (id in (731..762)) {
        name = "dust_${dayTime}"

        if (clouds > 50) {
            name = "dust"
        }
    } else if (id in (771..781)) {
        name = "tornado"
    } else if (id == 800) {
        name = "clear_${dayTime}"
    } else if (id == 801) {
        name = "partly_cloudy_${dayTime}"
    } else if (id in (802..803)) {
        name = "overcast_${dayTime}"
    } else if (id == 804) {
        name = "overcast"
    }

    return name
}


fun getIconNameDay(id: Int, dt: Long, sunrise: Long?, sunset: Long?, clouds: Int): String {
    val dayTime: String

    if (dt < sunrise!! || dt > sunset!!) {
        dayTime = "night"
    } else {
        dayTime = "day"
    }

    var name = ""

    if (id in (200..232)) {
        name = "thunderstorms"

        if (clouds < 25) {
            name += "_$dayTime"
        }

        if (id !in (210..221)) {
            name += "_rain"
        }
    } else if (id in (300..321)) {
        name = "drizzle"

        if (clouds < 25) {
            name = "partly_cloudy_${dayTime}_drizzle"
        }
    } else if (id in (500..531)) {
        name = "rain"

        if (clouds < 25) {
            name = "partly_cloudy_${dayTime}_rain"
        }
    } else if (id in (600..622)) {
        name = "snow"

        if (clouds < 25) {
            name = "partly_cloudy_${dayTime}_snow"
        }

        if (id in (611..616)) {
            name = "sleet"

            if (clouds < 25) {
                name = "partly_cloudy_${dayTime}_sleet"
            }
        }
    } else if (id == 701) {
        name = "mist"
    } else if (id == 711) {
        name = "smoke"

        if (clouds < 25) {
            name = "partly_cloudy_${dayTime}_smoke"
        }
    } else if (id == 721) {
        name = "haze_${dayTime}"

        if (clouds > 25) {
            name = "partly_cloudy_{$dayTime}_haze"
        }
        if (clouds > 50) {
            name = "haze"
        }
    } else if (id == 741) {
        name = "fog_${dayTime}"

        if (clouds > 25) {
            name = "partly_cloudy_{$dayTime}_fog"
        }
        if (clouds > 50) {
            name = "fog"
        }
    } else if (id in (731..762)) {
        name = "dust_${dayTime}"

        if (clouds > 50) {
            name = "dust"
        }
    } else if (id in (771..781)) {
        name = "tornado"
    } else if (id == 800) {
        name = "clear_${dayTime}"
    } else if (id == 801) {
        name = "partly_cloudy_${dayTime}"
    } else if (id in (802..803)) {
        name = "overcast_${dayTime}"
    } else if (id == 804) {
        name = "overcast"
    }

    return name
}

fun getDrawableByName(context: Context, drawableName: String): Drawable? {
    val resources = context.resources
    val packageName = context.packageName
    val resourceId = resources.getIdentifier(drawableName, "drawable", packageName)
    return if (resourceId != 0) {
        resources.getDrawable(resourceId, null)
    } else {
        null
    }
}

fun formatDate(timestamp: Long, offsetSeconds: Int): String {
    val instant = Instant.ofEpochSecond(timestamp)
    val zoneOffset = ZoneOffset.ofTotalSeconds(offsetSeconds)

    val formatter = DateTimeFormatter.ofPattern("MMMM d, HH:mm", Locale.ENGLISH)
        .withLocale(Locale.ENGLISH)
        .withZone(zoneOffset)

    return formatter.format(instant)
}

fun formatHour(timestamp: Long, offsetSeconds: Int): String {
    val instant = Instant.ofEpochSecond(timestamp)
    val zoneOffset = ZoneOffset.ofTotalSeconds(offsetSeconds)

    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
        .withZone(zoneOffset)

    return formatter.format(instant)
}

fun getCityName(geocoder: Geocoder, latitude: Double, longitude: Double): String {
    try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                return address.locality ?: address.subAdminArea
            }
        }
    } catch (e: Exception) {
        Log.e("Geocoding", "Error: ${e.message}")
    }
    return convertToNSEW(latitude, longitude)
}

fun getCountryName(geocoder: Geocoder, latitude: Double, longitude: Double): String {
    try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                return address.countryName
            }
        }
    } catch (e: Exception) {
        Log.e("Geocoding", "Error: ${e.message}")
    }
    return ""
}

fun convertToNSEW(latitude: Double, longitude: Double): String {
    val latDegrees = latitude.toInt()
    val latMinutesDecimal = (latitude - latDegrees) * 60
    val latMinutes = latMinutesDecimal.toInt()
    val latCardinal = if (latitude >= 0) "N" else "S"

    val longDegrees = longitude.toInt()
    val longMinutesDecimal = (longitude - longDegrees) * 60
    val longMinutes = longMinutesDecimal.toInt()
    val longCardinal = if (longitude >= 0) "E" else "W"

    val absLatDegrees = abs(latDegrees)
    val absLatMinutes = abs(latMinutes)
    val absLongDegrees = abs(longDegrees)
    val absLongMinutes = abs(longMinutes)

    return "$absLatDegrees°$absLatMinutes'$latCardinal $absLongDegrees°$absLongMinutes'$longCardinal"
}