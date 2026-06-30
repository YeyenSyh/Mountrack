package com.example.mounttrack.ui.journal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mounttrack.data.model.JournalEntity
import com.example.mounttrack.databinding.ItemJournalBinding

/**
 * Adapter RecyclerView untuk menampilkan daftar catatan jurnal pendakian.
 */
class JournalAdapter(
    private val onDeleteClicked: (JournalEntity) -> Unit
) : ListAdapter<JournalEntity, JournalAdapter.JournalViewHolder>(JournalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val binding = ItemJournalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return JournalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class JournalViewHolder(
        private val binding: ItemJournalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(journal: JournalEntity) {
            binding.tvJournalMountainName.text = journal.mountainName
            binding.tvJournalDate.text = journal.date
            binding.tvJournalNotes.text = journal.notes

            binding.btnDeleteJournal.setOnClickListener {
                onDeleteClicked(journal)
            }
        }
    }

    class JournalDiffCallback : DiffUtil.ItemCallback<JournalEntity>() {
        override fun areItemsTheSame(oldItem: JournalEntity, newItem: JournalEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: JournalEntity, newItem: JournalEntity): Boolean {
            return oldItem == newItem
        }
    }
}
