package com.zio.breeze.ui.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.zio.breeze.data.City
import com.zio.breeze.data.ForecastItem
import com.zio.breeze.data.WeatherData
import com.zio.breeze.util.WeatherCaller
import org.json.JSONObject
import kotlin.math.min

class WeatherRepo(private val context: Context) {

    interface WeatherCallback {
        fun onWeatherDataReceived(weatherData: WeatherData)
        fun onForecastReceived(forecast: List<ForecastItem>)
        fun onLatLanReceived(city: City)
        fun onError(errorCode: Int)
    }

    private val caller = WeatherCaller(context)

    fun getWeatherData(city: City, callback: WeatherCallback) {
        if (!isInternetAvailable()) {
            callback.onError(ERROR_NO_INTERNET)
            return
        }
        caller.fetchWeatherData(city.latitude, city.longitude, object : WeatherCaller.ResultBack {
            override fun onResult(response: JSONObject) {
                try {
                    callback.onWeatherDataReceived(parseResponseWeather(response))
                } catch (e: Exception) {
                    callback.onError(ERROR_PARSE)
                }
            }

            override fun onError(code: Int) {
                callback.onError(ERROR_NETWORK)
            }
        })

        caller.fetchForecast(city.latitude, city.longitude, object : WeatherCaller.ResultBack {
            override fun onResult(response: JSONObject) {
                try {
                    callback.onForecastReceived(parseResponseForecast(response))
                } catch (e: Exception) {
                    callback.onError(ERROR_PARSE)
                }
            }

            override fun onError(code: Int) {
                callback.onError(ERROR_NETWORK)
            }
        })
    }

    fun getLatLon(pin: Int, callback: WeatherCallback) {
        if (!isInternetAvailable()) {
            callback.onError(ERROR_NO_INTERNET)
            return
        }
        caller.fetchLatLan(pin, object : WeatherCaller.ResultBack {
            override fun onResult(response: JSONObject) {
                try {
                    callback.onLatLanReceived(parseResponseCity(response))
                } catch (e: Exception) {
                    callback.onError(ERROR_PARSE)
                }
            }

            override fun onError(code: Int) {
                callback.onError(ERROR_NETWORK)
            }
        })
    }

    private fun parseResponseCity(response: JSONObject): City {
        return City(
            response.getString("name"),
            response.getInt("zip"),
            response.getDouble("lat"),
            response.getDouble("lon")
        )
    }

    private fun parseResponseWeather(response: JSONObject): WeatherData {
        val cityName = response.getString("name")
        val main = response.getJSONObject("main")
        val temp = main.getDouble("temp").toInt()
        val climate = response.getJSONArray("weather").getJSONObject(0).getString("main")
        val speed = response.getJSONObject("wind").getDouble("speed").toFloat()
        val seaLevel = main.optInt("sea_level", 1000)
        val pressure = main.getInt("pressure")
        val humidity = main.getInt("humidity")
        val details = response.getJSONArray("weather").getJSONObject(0).getString("description")
        Log.d("APP", climate)
        return WeatherData(
            cityName,
            temp,
            details,
            climate,
            pressure,
            humidity,
            speed,
            seaLevel
        )
    }

    private fun parseResponseForecast(response: JSONObject): List<ForecastItem> {
        val itemList = mutableListOf<ForecastItem>()
        val listArray = response.getJSONArray("list")
        for (i in 0 until min(10, listArray.length())) {
            val listObject = listArray.getJSONObject(i)
            val mainObject = listObject.getJSONObject("main")
            val temp = mainObject.getDouble("temp").toInt()
            val dtTxt = listObject.getString("dt_txt")
            val climate = listObject.getJSONArray("weather").getJSONObject(0).getString("main")
            val formattedTime = dtTxt.substring(11, 16) // Extracting the time part (HH:mm)
            itemList.add(ForecastItem(temp = temp, time = formattedTime, climate))
        }
        return itemList
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        const val ERROR_NO_INTERNET = 1
        const val ERROR_NETWORK = 2
        const val ERROR_PARSE = 3
    }
}
