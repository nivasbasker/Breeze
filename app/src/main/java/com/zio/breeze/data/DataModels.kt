package com.zio.breeze.data

/**
 * A Data class to store weather information of a particular location along with
 * member functions to retrieve temperature in appropriate format.
 */
data class WeatherData(
    val cityName: String,
    val temperature: Int,
    val description: String,
    val climate: String,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Float,
    val seaLevel: Int
) {
    companion object {
        private const val KELVIN_TO_CELSIUS_DIFF = 273
    }

    fun getInCelsius(): String {
        val tempInCelsius = temperature - KELVIN_TO_CELSIUS_DIFF
        return "$tempInCelsius °C"
    }

    fun getInKelvin(): String {
        return "$temperature K"
    }
}

/**
 * Data class to represent a place with its geographical name and coordinates.
 */
data class City(
    val cityName: String,
    val pinCode: Int,
    val latitude: Double,
    val longitude: Double
)

/**
 * Data class to represent a forecast item with temperature, time, and climate information.
 * Possible climates: clear, clouds, rain.
 */
data class ForecastItem(
    val temp: Int = 0,
    val time: String = "00:00",
    val climate: String
) {
    companion object {
        private const val KELVIN_TO_CELSIUS_DIFF = 273
    }

    fun getInCelsius(): String {
        val tempInCelsius = temp - KELVIN_TO_CELSIUS_DIFF
        return "$tempInCelsius °C"
    }

    fun getInKelvin(): String {
        return "$temp K"
    }
}
