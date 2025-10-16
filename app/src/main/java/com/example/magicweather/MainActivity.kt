package com.example.magicweather

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    var city: String = "Dhaka"
    var api: String = "6d525bde8a0908438db205ef469cee0d"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchWeather(city, api)

        findViewById<MaterialButton>(R.id.btnChangeCity).setOnClickListener {
            city = findViewById<EditText>(R.id.etCityInput).text.toString()
            fetchWeather(city, api)
        }
    }
    private fun fetchWeather(city: String, apiKey: String) {
        // Show loading
        findViewById<LinearLayout>(R.id.holder).visibility = View.GONE
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        findViewById<TextView>(R.id.errorText).visibility = View.GONE

        // Launch coroutine
        lifecycleScope.launch {
            try {
                // Network call on IO thread
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.weatherService.getWeather(city, "metric", apiKey)
                }

                // Handle response on Main thread
                if (response.isSuccessful && response.body() != null) {
                    val weatherData = response.body()!!
                    updateUI(weatherData, city)
                } else {
                    showError()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showError()
            }
        }
    }

    private fun updateUI(weatherData: WeatherResponse, city: String) {
        val temp = "${weatherData.main.temp}°C"
        val humidity = "${weatherData.main.humidity}%"
        val windSpeed = "${weatherData.wind.speed}km/h"
        val mainCondition = weatherData.weather[0].main
        val icon = weatherData.weather[0].icon

        findViewById<TextView>(R.id.tvTemperature).text = temp
        findViewById<TextView>(R.id.tvHumidity).text = humidity
        findViewById<TextView>(R.id.tvWindSpeed).text = windSpeed
        findViewById<TextView>(R.id.tvWeatherCondition).text = mainCondition
        findViewById<TextView>(R.id.textCondition).text = mainCondition
        findViewById<TextView>(R.id.tvCityName).text = city

        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
        findViewById<LinearLayout>(R.id.holder).visibility = View.VISIBLE
        findViewById<TextView>(R.id.errorText).visibility = View.GONE
    }

    private fun showError() {
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
        findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.holder).visibility = View.GONE
    }
}