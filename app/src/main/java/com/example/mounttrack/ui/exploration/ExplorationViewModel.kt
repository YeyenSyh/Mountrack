package com.example.mounttrack.ui.exploration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mounttrack.data.model.Mountain
import com.example.mounttrack.data.repository.MountainRepository

/**
 * ViewModel untuk Eksplorasi Gunung.
 * Mengelola data list gunung lokal dan memproses pencarian live-filtering.
 */
class ExplorationViewModel(private val repository: MountainRepository) : ViewModel() {

    private val allMountains = repository.getMountains()

    private val _filteredMountains = MutableLiveData<List<Mountain>>()
    val filteredMountains: LiveData<List<Mountain>> = _filteredMountains

    init {
        // Tampilkan seluruh gunung saat pertama dibuka
        _filteredMountains.value = allMountains
    }

    /**
     * Melakukan filter pencarian berdasarkan nama gunung atau provinsi.
     */
    fun filterMountains(query: String) {
        if (query.trim().isEmpty()) {
            _filteredMountains.value = allMountains
        } else {
            val filtered = allMountains.filter {
                it.nama.contains(query, ignoreCase = true) ||
                        it.provinsi.contains(query, ignoreCase = true)
            }
            _filteredMountains.value = filtered
        }
    }
}

/**
 * Factory untuk menyuntikkan MountainRepository ke ExplorationViewModel.
 */
class ExplorationViewModelFactory(private val repository: MountainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExplorationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExplorationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
