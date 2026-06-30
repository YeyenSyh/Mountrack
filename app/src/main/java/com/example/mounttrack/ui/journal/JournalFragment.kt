package com.example.mounttrack.ui.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mounttrack.R
import com.example.mounttrack.data.local.AppDatabase
import com.example.mounttrack.data.repository.JournalRepository
import com.example.mounttrack.databinding.FragmentJournalBinding

/**
 * Halaman 9: Buku Jurnal Pendakian (Daftar Catatan).
 * Menampilkan seluruh riwayat pendakian yang tersimpan di database lokal.
 */
class JournalFragment : Fragment() {

    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi Database, DAO, dan Repository lokal secara lazy
    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    private val repository by lazy { JournalRepository(database.journalDao()) }
    
    private val viewModel: JournalViewModel by viewModels {
        JournalViewModelFactory(repository)
    }

    private lateinit var adapter: JournalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = JournalAdapter { journal ->
            // Hapus item dari database ketika icon sampah diklik
            viewModel.delete(journal)
        }
        binding.rvJournals.layoutManager = LinearLayoutManager(requireContext())
        binding.rvJournals.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.allJournals.observe(viewLifecycleOwner) { journals ->
            adapter.submitList(journals)
            
            // Atur visibilitas empty state jika database kosong
            if (journals.isEmpty()) {
                binding.layoutJournalEmpty.visibility = View.VISIBLE
                binding.rvJournals.visibility = View.GONE
            } else {
                binding.layoutJournalEmpty.visibility = View.GONE
                binding.rvJournals.visibility = View.VISIBLE
            }
        }
    }

    private fun setupListeners() {
        // Klik FAB untuk tambah jurnal baru
        binding.fabAddJournal.setOnClickListener {
            findNavController().navigate(R.id.action_journal_to_addJournal)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
