package com.zio.breeze.util

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class WeatherCaller(private val context: Context) {

    interface ResultBack {
        fun onResult(response: JSONObject)
        fun onError(code: Int)
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }

    private var zipCode = 627805
    private var countryCode = "IN"

    private val apiKey = "ea712bdfd9104d4cd9dc46d017d22058"


    fun fetchLatLan(pinCode: Int, call: ResultBack) {
        zipCode = pinCode
        val geoCodingUrl =
            "https://api.openweathermap.org/geo/1.0/zip?zip=$zipCode,$countryCode&appid=$apiKey"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, geoCodingUrl, null,
            { response ->
                call.onResult(response)
            },
            { error ->
                call.onError(1)
                Log.e("Volley", "Error fetching latitude and longitude: ${error.message}")
            })

        requestQueue.add(jsonObjectRequest)
    }

    fun fetchWeatherData(lat: Double, lon: Double, callback: ResultBack) {
        val weatherUrl =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$apiKey"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, weatherUrl, null,
            { response ->
                callback.onResult(response)
            },
            { error ->
                Log.e("Volley", "Error fetching weather data: ${error.message}")
                callback.onError(1)
            })

        requestQueue.add(jsonObjectRequest)
    }

    fun fetchForecast(lat: Double, lon: Double, callback: ResultBack) {
        val forecastUrl =
            "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&appid=$apiKey"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, forecastUrl, null,
            { response ->
                callback.onResult(response)
            },
            { error ->
                Log.d("app debug", error.message.toString())
                callback.onError(1)
            })

        requestQueue.add(jsonObjectRequest)
    }


}

