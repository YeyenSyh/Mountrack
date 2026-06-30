package com.example.mounttrack.data.local

import androidx.room.*
import com.example.mounttrack.data.model.JournalEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) untuk tabel climb_journals.
 */
@Dao
interface JournalDao {
    
    @Query("SELECT * FROM climb_journals ORDER BY id DESC")
    fun getAllJournals(): Flow<List<JournalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: JournalEntity)

    @Delete
    suspend fun deleteJournal(journal: JournalEntity)

    @Query("SELECT COUNT(*) FROM climb_journals")
    fun getJournalCount(): Flow<Int>
}
