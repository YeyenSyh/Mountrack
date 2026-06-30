package com.example.mounttrack.data.repository

import android.content.Context
import com.example.mounttrack.data.model.Mountain
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * Repository untuk memuat data gunung dari file lokal assets/gunung.json.
 */
class MountainRepository(private val context: Context) {

    fun getMountains(): List<Mountain> {
        val jsonString: String
        try {
            jsonString = context.assets.open("gunung.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return emptyList()
        }

        val listMountainType = object : TypeToken<List<Mountain>>() {}.type
        return Gson().fromJson(jsonString, listMountainType)
    }
}
