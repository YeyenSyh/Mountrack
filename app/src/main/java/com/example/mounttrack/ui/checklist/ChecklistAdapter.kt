package com.example.mounttrack.ui.checklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mounttrack.databinding.ItemGearBinding

/**
 * Adapter RecyclerView untuk Checklist Perlengkapan.
 */
class ChecklistAdapter(
    private val gearList: List<String>,
    private val checkedGears: Set<String>,
    private val onCheckChanged: (String, Boolean) -> Unit
) : RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    // Menyimpan salinan internal set tercentang untuk respons cepat
    private val activeCheckedGears = checkedGears.toMutableSet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val binding = ItemGearBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChecklistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.bind(gearList[position])
    }

    override fun getItemCount(): Int = gearList.size

    /**
     * Memperbarui seluruh state centang secara eksternal (misal saat reset).
     */
    fun resetCheckedGears() {
        activeCheckedGears.clear()
        notifyDataSetChanged()
    }

    inner class ChecklistViewHolder(
        private val binding: ItemGearBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gearName: String) {
            // Hapus listener sementara untuk mencegah trigger saat bind daur ulang
            binding.cbGearItem.setOnCheckedChangeListener(null)
            
            binding.cbGearItem.text = gearName
            binding.cbGearItem.isChecked = activeCheckedGears.contains(gearName)

            // Pasang listener baru
            binding.cbGearItem.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    activeCheckedGears.add(gearName)
                } else {
                    activeCheckedGears.remove(gearName)
                }
                onCheckChanged(gearName, isChecked)
            }
        }
    }
}
