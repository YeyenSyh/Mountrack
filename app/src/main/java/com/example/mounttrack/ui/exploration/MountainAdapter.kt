package com.example.mounttrack.ui.exploration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mounttrack.data.model.Mountain
import com.example.mounttrack.databinding.ItemMountainBinding

/**
 * Adapter RecyclerView untuk menampilkan daftar gunung dalam card.
 */
class MountainAdapter(
    private val onMountainClicked: (Mountain) -> Unit
) : ListAdapter<Mountain, MountainAdapter.MountainViewHolder>(MountainDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MountainViewHolder {
        val binding = ItemMountainBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MountainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MountainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MountainViewHolder(
        private val binding: ItemMountainBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mountain: Mountain) {
            binding.tvMountainName.text = mountain.nama
            binding.tvMountainProvince.text = mountain.provinsi
            binding.tvMountainElevation.text = "${mountain.tinggi_mdpl} MDPL"

            binding.root.setOnClickListener {
                onMountainClicked(mountain)
            }
        }
    }

    class MountainDiffCallback : DiffUtil.ItemCallback<Mountain>() {
        override fun areItemsTheSame(oldItem: Mountain, newItem: Mountain): Boolean {
            return oldItem.nama == newItem.nama
        }

        override fun areContentsTheSame(oldItem: Mountain, newItem: Mountain): Boolean {
            return oldItem == newItem
        }
    }
}
