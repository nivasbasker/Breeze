package com.zio.breeze.data

import android.content.Context

/**
 * A manager class to persist and retrieve weather data and also for simple preferences and settings
 */
class CacheManager(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveWeatherData(weatherData: WeatherData) {
        val editor = sharedPreferences.edit()

        editor.putString(KEY_CITY, weatherData.cityName)
        editor.putString(KEY_DESC, weatherData.description)
        editor.putInt(KEY_TEMP, weatherData.temperature)
        editor.putInt(KEY_PRESS, weatherData.pressure)
        editor.putInt(KEY_HUMID, weatherData.humidity)
        editor.putInt(KEY_SEA, weatherData.seaLevel)
        editor.putFloat(KEY_WIND, weatherData.windSpeed)

        editor.putBoolean(KEY_PRESENT, true)

        editor.apply();
    }

    fun saveLastCity(city: City) {
        val editor = sharedPreferences.edit()

        editor.putString(KEY_CITY, city.cityName)
        editor.putInt(KEY_PIN, city.pinCode)
        editor.putFloat(KEY_LAT, city.latitude.toFloat())
        editor.putFloat(KEY_LON, city.longitude.toFloat())

        editor.putBoolean(KEY_PRESENT, true)

        editor.apply();
    }

    fun getLastCity(): City {

        return City(
            sharedPreferences.getString(KEY_CITY, null) ?: "Place",
            sharedPreferences.getInt(KEY_PIN, 627805),
            sharedPreferences.getFloat(KEY_LAT, 0.0F).toDouble(),
            sharedPreferences.getFloat(KEY_LON, 0.0F).toDouble()
        )
    }

    fun getLastWeatherData(): WeatherData {

        return WeatherData(
            sharedPreferences.getString(KEY_CITY, "Place") ?: "Place",
            sharedPreferences.getInt(KEY_TEMP, 0),
            sharedPreferences.getString(KEY_DESC, null) ?: "condition",
            sharedPreferences.getString(KEY_CLIMATE, null) ?: "climate",
            sharedPreferences.getInt(KEY_PRESS, 0),
            sharedPreferences.getInt(KEY_HUMID, 0),
            sharedPreferences.getFloat(KEY_WIND, 0.0F),
            sharedPreferences.getInt(KEY_SEA, 0)
        )
    }

    fun isCached(): Boolean {
        return sharedPreferences.getBoolean(KEY_PRESENT, false)
    }

    fun isCelsius(): Boolean {
        return sharedPreferences.getBoolean(KEY_CELSIUS, false)
    }

    fun setToCelsius(inCelsius: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_CELSIUS, inCelsius).apply()
    }


    fun cleanAll() {
        sharedPreferences.edit().putBoolean(KEY_PRESENT, false).apply()
    }


    companion object {
        private const val KEY_PRESENT = "present"
        private const val KEY_CELSIUS = "celsius"

        private const val KEY_CITY = "city"
        private const val KEY_TEMP = "temp"
        private const val KEY_DESC = "desc"
        private const val KEY_CLIMATE = "climate"
        private const val KEY_PRESS = "pres"
        private const val KEY_HUMID = "humid"
        private const val KEY_SEA = "sea"
        private const val KEY_WIND = "wind"

        private const val KEY_PIN = "pin"
        private const val KEY_LAT = "lat"
        private const val KEY_LON = "lon"

        private const val PREF_NAME = "WeatherCache"


    }
}