package com.example.mounttrack.data.remote

import com.example.mounttrack.data.model.ForecastResponse
import com.example.mounttrack.data.model.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Service API Retrofit untuk terhubung dengan WeatherAPI.com.
 */
interface WeatherApiService {

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): WeatherResponse

    @GET("forecast.json")
    suspend fun getWeatherForecast(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("days") days: Int
    ): ForecastResponse

    companion object {
        private const val BASE_URL = "https://api.weatherapi.com/v1/"

        fun create(): WeatherApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
        }
    }
}
