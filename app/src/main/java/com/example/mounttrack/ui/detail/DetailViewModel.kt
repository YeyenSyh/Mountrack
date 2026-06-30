package com.example.mounttrack.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mounttrack.data.model.WeatherResponse
import com.example.mounttrack.data.repository.WeatherRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Detail Gunung.
 * Mengambil informasi cuaca terkini di puncak gunung asinkron menggunakan koordinat GPS gunung.
 */
class DetailViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse?>()
    val weatherData: LiveData<WeatherResponse?> = _weatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getCurrentWeather(lat, lon)
                _weatherData.value = response
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Gagal memuat cuaca."
                _weatherData.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}

/**
 * Factory untuk DetailViewModel.
 */
class DetailViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
