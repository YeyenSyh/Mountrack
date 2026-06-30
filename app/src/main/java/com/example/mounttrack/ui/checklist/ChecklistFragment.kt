package com.example.mounttrack.ui.checklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mounttrack.data.local.PreferenceManager
import com.example.mounttrack.databinding.FragmentChecklistBinding

/**
 * Halaman 8: Checklist Perlengkapan Pendaki.
 * Daftar perlengkapan standar mendaki yang tersimpan persisten via SharedPreferences.
 */
class ChecklistFragment : Fragment() {

    private var _binding: FragmentChecklistBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var adapter: ChecklistAdapter

    private val defaultGears = listOf(
        "Tenda Dome",
        "Jaket Gunung (Tebal & Windproof)",
        "Sleeping Bag",
        "Matras",
        "Senter / Headlamp",
        "Kompor Camping & Nesting",
        "Logistik & Bahan Makanan",
        "P3K Pribadi & Obat-obatan",
        "Jas Hujan / Raincoat",
        "Trash Bag (Kantong Sampah)",
        "Sepatu / Sandal Gunung",
        "Botol Minum / Water Bladder"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChecklistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        val checkedGears = preferenceManager.getCheckedGears()

        adapter = ChecklistAdapter(defaultGears, checkedGears) { gearName, isChecked ->
            // Update data ke SharedPreferences secara persisten
            val currentChecked = preferenceManager.getCheckedGears().toMutableSet()
            if (isChecked) {
                currentChecked.add(gearName)
            } else {
                currentChecked.remove(gearName)
            }
            preferenceManager.setCheckedGears(currentChecked)
        }

        binding.rvChecklist.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChecklist.adapter = adapter
    }

    private fun setupListeners() {
        // Tombol Kembali
        binding.btnChecklistBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Tombol Reset Centang
        binding.btnResetChecklist.setOnClickListener {
            preferenceManager.clearCheckedGears()
            adapter.resetCheckedGears()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
