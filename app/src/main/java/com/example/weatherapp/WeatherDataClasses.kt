data class WeatherApiResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezoneOffset: Int,
    val current: CurrentWeather?,
    val minutely: List<MinutelyWeather>?,
    val hourly: List<HourlyWeather>?,
    val daily: List<DailyWeather>?,
    val alerts: List<Alert>?
)

data class CurrentWeather(
    var dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val clouds: Int,
    val uvi: Double,
    val visibility: Int,
    val windSpeed: Double,
    val windGust: Double?,
    val windDeg: Int,
    val rain: Double?,
    val snow: Double?,
    val weather: WeatherCondition
)

data class MinutelyWeather(
    val dt: Long,
    val precipitation: Double
)

data class HourlyWeather(
    val dt: Long,
    val temp: Double,
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val clouds: Int,
    val visibility: Int,
    val windSpeed: Double,
    val windGust: Double?,
    val windDeg: Int,
    val pop: Double,
    val rain: Double?,
    val snow: Double?,
    val weather: WeatherCondition
)

data class DailyWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moonPhase: Double,
    val summary: String,
    val temp: Temperature,
    val feelsLike: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val windSpeed: Double,
    val windGust: Double?,
    val windDeg: Int,
    val clouds: Int,
    val uvi: Double,
    val pop: Double,
    val rain: Double?,
    val snow: Double?,
    val weather: WeatherCondition
)

data class Temperature(
    val morn: Double,
    val day: Double,
    val eve: Double,
    val night: Double,
    val min: Double,
    val max: Double
)

data class FeelsLike(
    val morn: Double = 0.0,
    val day: Double = 0.0,
    val eve: Double = 0.0,
    val night: Double = 0.0
)

data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Alert(
    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
)