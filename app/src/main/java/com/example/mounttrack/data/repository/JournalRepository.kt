package com.example.mounttrack.data.repository

import com.example.mounttrack.data.local.JournalDao
import com.example.mounttrack.data.model.JournalEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository untuk memuat dan memanipulasi data jurnal pendakian di lokal database.
 */
class JournalRepository(private val journalDao: JournalDao) {

    val allJournals: Flow<List<JournalEntity>> = journalDao.getAllJournals()
    val journalCount: Flow<Int> = journalDao.getJournalCount()

    suspend fun insert(journal: JournalEntity) {
        journalDao.insertJournal(journal)
    }

    suspend fun delete(journal: JournalEntity) {
        journalDao.deleteJournal(journal)
    }
}
