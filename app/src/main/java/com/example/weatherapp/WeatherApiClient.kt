package com.example.weatherapp

import Alert
import CurrentWeather
import DailyWeather
import FeelsLike
import HourlyWeather
import MinutelyWeather
import Temperature
import WeatherApiResponse
import WeatherCondition
import android.os.AsyncTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class WeatherApiClient(private val apiKey: String) {
    suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherApiResponse = withContext(
        Dispatchers.IO) {
        val url = "https://api.openweathermap.org/data/3.0/onecall?lat=$latitude&lon=$longitude&units=metric&exclude=minutely&appid=$apiKey"

        val json = withContext(Dispatchers.IO) {
            getJsonData(url)
        }
        return@withContext parseWeatherApiResponse(json)
    }

    private suspend fun getJsonData(url: String): JSONObject = withContext(Dispatchers.IO) {
        val response = async { URL(url).readText() }
        JSONObject(response.await())
    }

    private fun parseWeatherApiResponse(json: JSONObject): WeatherApiResponse {
        return with(json) {
            WeatherApiResponse(
                lat = getDouble("lat"),
                lon = getDouble("lon"),
                timezone = getString("timezone"),
                timezoneOffset = getInt("timezone_offset"),
                current = if (has("current")) parseCurrentWeather(getJSONObject("current")) else null,
                minutely = if (has("minutely")) parseMinutelyWeather(getJSONArray("minutely")) else null,
                hourly = if (has("hourly")) parseHourlyWeather(getJSONArray("hourly")) else null,
                daily = if (has("daily")) parseDailyWeather(getJSONArray("daily")) else null,
                alerts = if (has("alerts")) parseAlerts(getJSONArray("alerts")) else null
            )
        }
    }

    private fun parseCurrentWeather(json: JSONObject): CurrentWeather {
        return with(json) {
            CurrentWeather(
                dt = getLong("dt"),
                sunrise = getLong("sunrise"),
                sunset = getLong("sunset"),
                temp = getDouble("temp"),
                feelsLike = getDouble("feels_like"),
                pressure = getInt("pressure"),
                humidity = getInt("humidity"),
                dewPoint = getDouble("dew_point"),
                clouds = getInt("clouds"),
                uvi = getDouble("uvi"),
                visibility = getInt("visibility"),
                windSpeed = getDouble("wind_speed"),
                windGust = if (has("wind_gust")) getDouble("wind_gust") else null,
                windDeg = getInt("wind_deg"),
                rain = if (json.has("rain")) {
                    val rainObj = json.getJSONObject("rain")
                    if (rainObj.has("1h")) rainObj.getDouble("1h") else 0.0
                } else {
                    0.0
                },
                snow = if (json.has("snow")) {
                    val snowObj = json.getJSONObject("snow")
                    if (snowObj.has("1h")) snowObj.getDouble("1h") else 0.0
                } else {
                    0.0
                },
                weather = parseWeatherCondition(getJSONArray("weather").getJSONObject(0))
            )
        }
    }

    private fun parseMinutelyWeather(jsonArray: JSONArray): List<MinutelyWeather> {
        val minutelyWeatherList = mutableListOf<MinutelyWeather>()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            minutelyWeatherList.add(
                MinutelyWeather(
                    dt = json.getLong("dt"),
                    precipitation = json.getDouble("precipitation")
                )
            )
        }
        return minutelyWeatherList
    }

    private fun parseHourlyWeather(jsonArray: JSONArray): List<HourlyWeather> {
        val hourlyWeatherList = mutableListOf<HourlyWeather>()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            hourlyWeatherList.add(
                HourlyWeather(
                    dt = json.getLong("dt"),
                    temp = json.getDouble("temp"),
                    feelsLike = json.getDouble("feels_like"),
                    pressure = json.getInt("pressure"),
                    humidity = json.getInt("humidity"),
                    dewPoint = json.getDouble("dew_point"),
                    clouds = json.getInt("clouds"),
                    visibility = json.getInt("visibility"),
                    windSpeed = json.getDouble("wind_speed"),
                    windGust = if (json.has("wind_gust")) json.getDouble("wind_gust") else null,
                    windDeg = json.getInt("wind_deg"),
                    pop = json.getDouble("pop"),
                    rain = if (json.has("rain")) {
                        val rainObj = json.getJSONObject("rain")
                        if (rainObj.has("1h")) rainObj.getDouble("1h") else 0.0
                    } else {
                        0.0
                    },
                    snow = if (json.has("snow")) {
                        val snowObj = json.getJSONObject("snow")
                        if (snowObj.has("1h")) snowObj.getDouble("1h") else 0.0
                    } else {
                        0.0
                    },
                    weather = parseWeatherCondition(json.getJSONArray("weather").getJSONObject(0))
                )
            )
        }
        return hourlyWeatherList
    }

    private fun parseDailyWeather(jsonArray: JSONArray): List<DailyWeather> {
        val dailyWeatherList = mutableListOf<DailyWeather>()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            dailyWeatherList.add(
                DailyWeather(
                    dt = json.getLong("dt"),
                    sunrise = json.getLong("sunrise"),
                    sunset = json.getLong("sunset"),
                    moonrise = json.getLong("moonrise"),
                    moonset = json.getLong("moonset"),
                    moonPhase = json.getDouble("moon_phase"),
                    summary = json.getString("summary"),
                    temp = parseTemperature(json.getJSONObject("temp")),
                    feelsLike = parseFeelsLike(json.getJSONObject("feels_like")),
                    pressure = json.getInt("pressure"),
                    humidity = json.getInt("humidity"),
                    dewPoint = json.getDouble("dew_point"),
                    windSpeed = json.getDouble("wind_speed"),
                    windGust = if (json.has("wind_gust")) json.getDouble("wind_gust") else null,
                    windDeg = json.getInt("wind_deg"),
                    clouds = json.getInt("clouds"),
                    uvi = json.getDouble("uvi"),
                    pop = json.getDouble("pop"),
                    rain = if (json.has("rain")) json.getDouble("rain") else null,
                    snow = if (json.has("snow")) json.getDouble("snow") else null,
                    weather = parseWeatherCondition(json.getJSONArray("weather").getJSONObject(0))
                )
            )
        }
        return dailyWeatherList
    }

    private fun parseTemperature(json: JSONObject): Temperature {
        return with(json) {
            Temperature(
                morn = getDouble("morn"),
                day = getDouble("day"),
                eve = getDouble("eve"),
                night = getDouble("night"),
                min = getDouble("min"),
                max = getDouble("max")
            )
        }
    }

    private fun parseFeelsLike(json: JSONObject): FeelsLike {
        return with(json) {
            FeelsLike(
                morn = getDouble("morn"),
                day = getDouble("day"),
                eve = getDouble("eve"),
                night = getDouble("night"),
            )
        }
    }

    private fun parseWeatherCondition(json: JSONObject): WeatherCondition {
        return with(json) {
            WeatherCondition(
                id = getInt("id"),
                main = getString("main"),
                description = getString("description"),
                icon = getString("icon")
            )
        }
    }

    private fun parseAlerts(jsonArray: JSONArray): List<Alert> {
        val alertList = mutableListOf<Alert>()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            alertList.add(
                Alert(
                    senderName = json.getString("sender_name"),
                    event = json.getString("event"),
                    start = json.getLong("start"),
                    end = json.getLong("end"),
                    description = json.getString("description"),
                    tags = parseTags(json.getJSONArray("tags"))
                )
            )
        }
        return alertList
    }

    private fun parseTags(jsonArray: JSONArray): List<String> {
        val tags = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            tags.add(jsonArray.getString(i))
        }
        return tags
    }

// Utility functions to handle missing data

    private fun JSONObject.getDoubleOrDefault(key: String): Double {
        return if (has(key)) getDouble(key) else 0.0
    }

    private fun JSONObject.getIntOrDefault(key: String): Int {
        return if (has(key)) getInt(key) else 0
    }

    private fun JSONObject.getStringOrDefault(key: String): String {
        return if (has(key)) getString(key) else "N/A"
    }
}
