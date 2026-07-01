package com.example.mounttrack.data.model

import java.io.Serializable

/**
 * Model data lokal untuk menyimpan informasi gunung.
 * Di-load dari assets/gunung.json.
 */
data class Mountain(
    val nama: String,
    val provinsi: String,
    val tinggi_mdpl: Int,
    val deskripsi: String,
    val latitude: Double,
    val longitude: Double,
    val simaksi_per_hari: Int = 0,
    val tarif_parkir: Int = 0,
    val sumber_air_tersedia: Boolean = false
) : Serializable
