package com.example.mounttrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room untuk menyimpan data jurnal pendakian.
 */
@Entity(tableName = "climb_journals")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mountainName: String,
    val date: String,
    val notes: String
)
