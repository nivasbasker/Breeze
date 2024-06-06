package com.zio.breeze.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zio.breeze.data.City
import com.zio.breeze.data.ForecastItem
import com.zio.breeze.data.WeatherData
import com.zio.breeze.ui.model.WeatherRepo
import com.zio.breeze.ui.model.WeatherRepo.WeatherCallback


class BreezeViewModel(application: Application) : AndroidViewModel(application) {

    val app = application

    private val repo = WeatherRepo(app.baseContext)

    private val _weatherData = MutableLiveData<WeatherData>()
    val weatherData: LiveData<WeatherData> get() = _weatherData

    private val _forecast = MutableLiveData<List<ForecastItem>>()
    val forecast: LiveData<List<ForecastItem>> get() = _forecast

    private val _error = MutableLiveData<Int>()
    val error: LiveData<Int> get() = _error
    // 1- no internet 2- api error 3-parse error 4-invalid input

    private val _isProcessing = MutableLiveData<Boolean>()
    val isProcessing: LiveData<Boolean> get() = _isProcessing


    private val _city = MutableLiveData<City>()
    val city: LiveData<City> get() = _city

    fun getWeather(forCity: City) {
        Log.d("app debug", "weather called")
        _isProcessing.postValue(true)

        repo.getWeatherData(forCity, object : WeatherCallback {
            override fun onWeatherDataReceived(data: WeatherData) {
                _weatherData.postValue(data)
            }

            override fun onForecastReceived(forecast: List<ForecastItem>) {
                _forecast.postValue(forecast)
                _isProcessing.postValue(false)
            }

            override fun onLatLanReceived(city: City) {
            }

            override fun onError(errorCode: Int) {
                _error.postValue(errorCode)
                _isProcessing.postValue(false)

            }

        })
    }

    fun setSearch(text: String) {
        Log.d("app debug", "seqrch called")
        if (text.length == 6 && text.all { it.isDigit() }) {
            getLatLon(text)
        } else {
            _error.postValue(4)
        }

    }

    private fun getLatLon(pinCode: String) {
        Log.d("app debug", "lat lan called")
        _isProcessing.postValue(true)

        repo.getLatLon(pinCode.toInt(), object : WeatherCallback {
            override fun onWeatherDataReceived(data: WeatherData) {
            }

            override fun onForecastReceived(forecast: List<ForecastItem>) {
            }

            override fun onLatLanReceived(city: City) {
                _city.postValue(city)
                _isProcessing.postValue(false)
                Log.d("app debug", "received lat called")
            }

            override fun onError(errorCode: Int) {
                _isProcessing.postValue(false)
                Log.d("app debug", "received error")
                _error.postValue(errorCode)
            }

        })
    }

}

