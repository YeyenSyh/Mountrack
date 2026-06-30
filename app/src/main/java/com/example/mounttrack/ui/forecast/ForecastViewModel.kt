package com.example.mounttrack.ui.forecast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mounttrack.data.model.ForecastResponse
import com.example.mounttrack.data.repository.WeatherRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Prakiraan Cuaca.
 * Mengambil ramalan cuaca 7 hari ke depan dari WeatherAPI secara asinkron.
 */
class ForecastViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _forecastData = MutableLiveData<ForecastResponse?>()
    val forecastData: LiveData<ForecastResponse?> = _forecastData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Fetch forecast data for 7 days.
     */
    fun fetchForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getWeatherForecast(lat, lon, 7)
                _forecastData.value = response
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Gagal memuat prakiraan cuaca. Periksa jaringan internet Anda."
                _forecastData.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Factory untuk ForecastViewModel.
 */
class ForecastViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForecastViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForecastViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
