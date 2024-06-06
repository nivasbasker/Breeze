package com.zio.breeze.ui.view

import android.content.Context

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.switchmaterial.SwitchMaterial
import com.zio.breeze.data.CacheManager
import com.zio.breeze.data.City
import com.zio.breeze.R
import com.zio.breeze.data.ForecastItem
import com.zio.breeze.data.WeatherData
import com.zio.breeze.databinding.ActivityMainBinding
import com.zio.breeze.ui.viewmodel.BreezeViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: BreezeViewModel
    private lateinit var adapter: ForecastAdapter
    private lateinit var searchPopupWindow: PopupWindow
    private lateinit var prefPopupWindow: PopupWindow
    private lateinit var searchBar: EditText
    private lateinit var currentCity: City
    private lateinit var currentWeatherData: WeatherData
    private lateinit var currentForecastItems: List<ForecastItem>
    private lateinit var cacheManager: CacheManager

    private val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize variables
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.hourlyView.layoutManager = layoutManager
        viewModel = ViewModelProvider(this).get(BreezeViewModel::class.java)
        cacheManager = CacheManager(this)

        // Initialize views and listeners
        initializeViews()
        initializeListeners()
    }

    private fun initializeViews() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val searchPopupView = inflater.inflate(R.layout.popup_search, null)
        val prefPopupView = inflater.inflate(R.layout.popup_prefs, null)
        searchPopupWindow =
            PopupWindow(searchPopupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true)
        prefPopupWindow =
            PopupWindow(prefPopupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true)
        searchBar = searchPopupView.findViewById(R.id.inp_city)

        findInCache()
    }

    private fun initializeListeners() {
        setSearchListener()
        setSearchInputListeners()
        setSearchResultListener()
        setWeatherResultsListeners()
        setErrorListener()
    }

    private fun findInCache() {
        if (cacheManager.isCached()) {
            currentWeatherData = cacheManager.getLastWeatherData()
            setWeather()
            currentCity = cacheManager.getLastCity()
            viewModel.getWeather(currentCity)
        }
    }

    private fun showSearchBarPopup() {

        searchPopupWindow.showAtLocation(binding.root, Gravity.TOP, 0, 0)
        searchBar.requestFocus()

        showKeyboardWithDelay()
        setSearchInputListeners()
        setSearchResultListener()

    }

    fun openPrefs(view: View) {
        prefPopupWindow.showAtLocation(binding.root, Gravity.TOP, 0, 250)
        val switch = prefPopupWindow.contentView.findViewById<SwitchMaterial>(R.id.pref_switch)
        switch.isChecked = cacheManager.isCelsius()
        switch.setOnClickListener() {
            cacheManager.setToCelsius(switch.isChecked)
            if (::currentWeatherData.isInitialized) setWeather()
            if (::currentForecastItems.isInitialized) setForecast()
        }
        prefPopupWindow.contentView.findViewById<LinearLayout>(R.id.outside_popup)
            .setOnClickListener() {
                if (prefPopupWindow.isShowing) prefPopupWindow.dismiss()

            }
    }

    private fun setSearchListener() {
        binding.inpCity.setOnClickListener {
            if (!::searchPopupWindow.isInitialized || !searchPopupWindow.isShowing) {
                showSearchBarPopup()
            }
        }
    }

    private fun setSearchInputListeners() {
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length == 6) viewModel.setSearch(text)
            }
        })

        searchBar.setOnKeyListener { v, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val text = searchBar.text.toString()
                if (text.length == 6) viewModel.setSearch(text)
                return@setOnKeyListener true
            }
            false
        }

        searchPopupWindow.contentView.findViewById<TextView>(R.id.result_view).setOnClickListener {
            if (::currentCity.isInitialized) {
                viewModel.getWeather(currentCity)
                searchPopupWindow.dismiss()
            }
        }
    }

    private fun setSearchResultListener() {
        viewModel.city.observe(this) { data ->
            data?.let {
                currentCity = it
                searchPopupWindow.contentView.findViewById<TextView>(R.id.result_view).text =
                    currentCity.cityName
            }
        }

        viewModel.isProcessing.observe(this) { data ->
            data?.let {
                if (it) {
                    searchPopupWindow.contentView.findViewById<ProgressBar>(R.id.pbar).visibility =
                        View.VISIBLE
                    binding.pbar.visibility = View.VISIBLE
                } else {
                    searchPopupWindow.contentView.findViewById<ProgressBar>(R.id.pbar).visibility =
                        View.INVISIBLE
                    binding.pbar.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setWeatherResultsListeners() {
        viewModel.weatherData.observe(this, Observer { data ->
            data?.let {
                binding.pbar.visibility = View.INVISIBLE
                searchPopupWindow.dismiss()
                currentWeatherData = it
                setWeather()
                cacheManager.saveWeatherData(it)
                cacheManager.saveLastCity(currentCity)

            }
        })


        viewModel.forecast.observe(this, Observer { data ->
            data?.let {
                currentForecastItems = it
                setForecast()
            }
        })
    }


    private fun showKeyboardWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)
        }, 200) // Delay to ensure the keyboard shows up
    }

    private fun setErrorListener() {
        viewModel.error.observe(this, Observer { data ->
            data?.let {
                val message = when (it) {
                    4 -> "Invalid pin code"
                    3, 2 -> "Network error, try again later"
                    1 -> "No internet"
                    else -> "Unknown error"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setForecast() {
        adapter = ForecastAdapter(currentForecastItems, cacheManager.isCelsius())
        binding.hourlyView.adapter = adapter
    }

    private fun setWeather() {
        if (cacheManager.isCelsius()) binding.currentTemp.text = currentWeatherData.getInCelsius()
        else binding.currentTemp.text = currentWeatherData.getInKelvin()
        binding.currentDetail.text = currentWeatherData.description
        binding.currentLocation.text = currentWeatherData.cityName
        binding.curretnMin.text = "${currentWeatherData.windSpeed} m/s"
        binding.currentMax.text = "${currentWeatherData.seaLevel} m"
        binding.currentPressure.text = "${currentWeatherData.pressure} hPa"
        binding.currentHumidity.text = "${currentWeatherData.humidity} %"

        setBackground(currentWeatherData.climate)
    }

    private fun setBackground(climate: String) {
        var start = 0xFF87CEFA.toInt()
        var end = 0xFF4682B4.toInt()
        when (climate) {
            "Clouds" -> {
                start = 0xFFB0E0E6.toInt()
                end = 0xFF4682B4.toInt()
                binding.climateImage.setImageResource(R.drawable.img_clouds)
            }

            "Rain" -> {
                start = 0xFF87CEEB.toInt()
                end = 0xFF2F4F4F.toInt()
                binding.climateImage.setImageResource(R.drawable.img_rains)
            }

            "Clear" -> {
                start = 0xFFFFE57F.toInt()
                end = 0xFFFFA726.toInt()
                binding.climateImage.setImageResource(R.drawable.img_clear)
            }
        }
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(start, end)
        )
        binding.root.background = gradientDrawable
    }


}