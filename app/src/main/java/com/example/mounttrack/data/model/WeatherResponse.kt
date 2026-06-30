package com.example.mounttrack.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model data respons dari WeatherAPI untuk endpoint current.json.
 */
data class WeatherResponse(
    @SerializedName("location") val location: WeatherLocation,
    @SerializedName("current") val current: CurrentWeather
)

data class WeatherLocation(
    @SerializedName("name") val name: String,
    @SerializedName("region") val region: String,
    @SerializedName("country") val country: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("localtime") val localtime: String
)

data class CurrentWeather(
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("temp_f") val tempF: Double,
    @SerializedName("condition") val condition: WeatherCondition,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("feelslike_c") val feelsLikeC: Double,
    @SerializedName("feelslike_f") val feelsLikeF: Double
)

data class WeatherCondition(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("code") val code: Int
)
