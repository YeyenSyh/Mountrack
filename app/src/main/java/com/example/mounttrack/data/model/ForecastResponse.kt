package com.example.mounttrack.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model data respons dari WeatherAPI untuk endpoint forecast.json.
 */
data class ForecastResponse(
    @SerializedName("location") val location: WeatherLocation,
    @SerializedName("forecast") val forecast: ForecastData
)

data class ForecastData(
    @SerializedName("forecastday") val forecastDays: List<ForecastDay>
)

data class ForecastDay(
    @SerializedName("date") val date: String,
    @SerializedName("date_epoch") val dateEpoch: Long,
    @SerializedName("day") val day: DayDetail
)

data class DayDetail(
    @SerializedName("maxtemp_c") val maxTempC: Double,
    @SerializedName("maxtemp_f") val maxTempF: Double,
    @SerializedName("mintemp_c") val minTempC: Double,
    @SerializedName("mintemp_f") val minTempF: Double,
    @SerializedName("condition") val condition: WeatherCondition,
    @SerializedName("daily_chance_of_rain") val dailyChanceOfRain: Int
)
