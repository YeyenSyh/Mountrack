package com.example.mounttrack.data.repository

import com.example.mounttrack.BuildConfig
import com.example.mounttrack.data.model.ForecastResponse
import com.example.mounttrack.data.model.WeatherResponse
import com.example.mounttrack.data.remote.WeatherApiService

/**
 * Repository untuk memuat data cuaca dari WeatherAPI.
 * Menggunakan API Key yang diekspos melalui BuildConfig.
 */
class WeatherRepository(private val apiService: WeatherApiService) {

    private val apiKey = BuildConfig.WEATHER_API_KEY

    suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse {
        val query = "$lat,$lon"
        return apiService.getCurrentWeather(apiKey, query)
    }

    suspend fun getWeatherForecast(lat: Double, lon: Double, days: Int = 7): ForecastResponse {
        val query = "$lat,$lon"
        return apiService.getWeatherForecast(apiKey, query, days)
    }
}
