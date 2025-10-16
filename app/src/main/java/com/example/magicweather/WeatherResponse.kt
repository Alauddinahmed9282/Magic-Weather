package com.example.magicweather

data class WeatherResponse(
    val main: Main,
    val wind: Wind,
    val weather: List<Weather>
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class Wind(
    val speed: Double
)

data class Weather(
    val main: String,
    val icon: String
)