package com.example.mounttrack.ui.forecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mounttrack.R
import com.example.mounttrack.data.local.PreferenceManager
import com.example.mounttrack.data.model.ForecastDay
import com.example.mounttrack.databinding.ItemForecastBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter RecyclerView untuk menampilkan prakiraan cuaca harian (forecast harian).
 */
class ForecastAdapter : ListAdapter<ForecastDay, ForecastAdapter.ForecastViewHolder>(ForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = ItemForecastBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ForecastViewHolder(
        private val binding: ItemForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val preferenceManager = PreferenceManager(binding.root.context)

        fun bind(forecastDay: ForecastDay, position: Int) {
            // Tentukan label hari
            val dayLabel = when (position) {
                0 -> "Hari Ini"
                1 -> "Besok"
                else -> getFormattedDay(forecastDay.dateEpoch * 1000)
            }
            binding.tvForecastDay.text = dayLabel
            binding.tvForecastDate.text = getFormattedDate(forecastDay.dateEpoch * 1000)

            // Cek satuan suhu
            val isCelsius = preferenceManager.isCelsius
            val minTemp = if (isCelsius) forecastDay.day.minTempC else forecastDay.day.minTempF
            val maxTemp = if (isCelsius) forecastDay.day.maxTempC else forecastDay.day.maxTempF
            val tempUnit = if (isCelsius) "°" else "°"

            binding.tvForecastTempRange.text = "${minTemp.toInt()}$tempUnit - ${maxTemp.toInt()}$tempUnit"
            binding.tvForecastRainChance.text = "Hujan: ${forecastDay.day.dailyChanceOfRain}%"

            // Tampilkan badge rekomendasi jika peluang hujan < 20%
            if (forecastDay.day.dailyChanceOfRain < 20) {
                binding.tvRecommendationBadge.visibility = View.VISIBLE
            } else {
                binding.tvRecommendationBadge.visibility = View.GONE
            }

            // Load icon cuaca
            val iconUrl = "https:${forecastDay.day.condition.icon}"
            Glide.with(binding.root.context)
                .load(iconUrl)
                .placeholder(R.drawable.ic_exploration)
                .into(binding.ivForecastIcon)
        }

        private fun getFormattedDay(timeMillis: Long): String {
            val sdf = SimpleDateFormat("EEEE", Locale("id", "ID"))
            return sdf.format(Date(timeMillis))
        }

        private fun getFormattedDate(timeMillis: Long): String {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            return sdf.format(Date(timeMillis))
        }
    }

    class ForecastDiffCallback : DiffUtil.ItemCallback<ForecastDay>() {
        override fun areItemsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean {
            return oldItem == newItem
        }
    }
}
