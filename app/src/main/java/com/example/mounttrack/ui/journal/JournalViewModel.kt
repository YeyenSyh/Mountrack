package com.example.mounttrack.ui.journal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mounttrack.data.model.JournalEntity
import com.example.mounttrack.data.repository.JournalRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Buku Jurnal Pendakian.
 * Menghubungkan UI dengan database Room melalui JournalRepository.
 */
class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    // Konversi Flow dari Room menjadi LiveData secara otomatis
    val allJournals: LiveData<List<JournalEntity>> = repository.allJournals.asLiveData()

    /**
     * Memasukkan entri jurnal baru ke database secara asinkron.
     */
    fun insert(journal: JournalEntity) = viewModelScope.launch {
        repository.insert(journal)
    }

    /**
     * Menghapus entri jurnal dari database secara asinkron.
     */
    fun delete(journal: JournalEntity) = viewModelScope.launch {
        repository.delete(journal)
    }
}

/**
 * Factory untuk JournalViewModel.
 */
class JournalViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
