package com.example.mounttrack.ui.exploration

// Import Bundle untuk menampung data parameter siklus hidup Fragment
import android.os.Bundle
// Import kelas-kelas UI dasar untuk memproses layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
// Import SearchView untuk menangani input bar pencarian
import androidx.appcompat.widget.SearchView
// Import base class Fragment untuk membuat sub-halaman
import androidx.fragment.app.Fragment
// Import viewModels untuk melakukan instansiasi ViewModel secara asinkron
import androidx.fragment.app.viewModels
// Import findNavController untuk memproses aksi navigasi Jetpack Navigation
import androidx.navigation.fragment.findNavController
// Import LinearLayoutManager untuk menyusun daftar RecyclerView secara vertikal
import androidx.recyclerview.widget.LinearLayoutManager
// Import kelas R untuk mengakses resource ID aplikasi
import com.example.mounttrack.R
// Import MountainRepository untuk membaca data gunung dari JSON assets
import com.example.mounttrack.data.repository.MountainRepository
// Import layout binding kelas generator untuk mengambil view secara aman
import com.example.mounttrack.databinding.FragmentExplorationBinding

/**
 * Halaman 3: Eksplorasi Gunung.
 * Menampilkan daftar gunung di Indonesia dari file JSON lokal yang dapat disaring secara real-time.
 */
class ExplorationFragment : Fragment() {

    // Menyimpan referensi binding layout (nullable saat view hancur)
    private var _binding: FragmentExplorationBinding? = null
    // Akses aman non-null ke binding
    private val binding get() = _binding!!

    // Instansiasi MountainRepository secara lazy menggunakan context Fragment
    private val mountainRepository by lazy { MountainRepository(requireContext()) }
    
    // Inisialisasi ViewModel dengan menyertakan factory repository
    private val viewModel: ExplorationViewModel by viewModels {
        ExplorationViewModelFactory(mountainRepository)
    }

    // Mendeklarasikan objek adapter untuk RecyclerView list gunung
    private lateinit var adapter: MountainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflasi layout fragment_exploration.xml menggunakan view binding
        _binding = FragmentExplorationBinding.inflate(inflater, container, false)
        // Mengembalikan root view dari binding layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menyiapkan RecyclerView dan adapter
        setupRecyclerView()
        // Menyiapkan SearchView listener
        setupSearchView()
        // Menyiapkan observer data dari ViewModel
        setupObservers()
    }

    /**
     * Konfigurasi RecyclerView dan interaksi klik pada card gunung.
     */
    private fun setupRecyclerView() {
        // Instansiasi adapter dengan callback saat item diklik
        adapter = MountainAdapter { mountain ->
            // Membuat Bundle untuk menyisipkan objek gunung yang diklik
            val bundle = Bundle().apply {
                putSerializable("mountain", mountain) // Menyisipkan objek Mountain serializable
            }
            // Navigasi ke fragment Detail Gunung membawa objek bundle argumen
            findNavController().navigate(R.id.action_exploration_to_detail, bundle)
        }
        
        // Menetapkan tata letak vertikal (LinearLayoutManager) pada RecyclerView
        binding.rvMountains.layoutManager = LinearLayoutManager(requireContext())
        // Mengaitkan adapter ke RecyclerView
        binding.rvMountains.adapter = adapter
    }

    /**
     * Mengatur listener teks pencarian pada SearchView.
     */
    private fun setupSearchView() {
        // Menambahkan listener untuk menangani perubahan teks pencarian
        binding.searchViewMountains.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Dipanggil saat pengguna menekan tombol enter/submit di keyboard
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Mengirimkan teks pencarian ke ViewModel untuk disaring
                viewModel.filterMountains(query.orEmpty())
                return true
            }

            // Dipanggil secara real-time setiap kali teks di kolom pencarian berubah
            override fun onQueryTextChange(newText: String?): Boolean {
                // Mengirimkan teks terbaru secara live ke ViewModel untuk disaring
                viewModel.filterMountains(newText.orEmpty())
                return true
            }
        })
    }

    /**
     * Mengamati LiveData list gunung yang sudah disaring dari ViewModel.
     */
    private fun setupObservers() {
        // Observer live data list gunung
        viewModel.filteredMountains.observe(viewLifecycleOwner) { mountains ->
            // Mengirim data gunung ke adapter untuk merender ulang RecyclerView
            adapter.submitList(mountains)
            
            // Tampilkan Empty State (teks "tidak ditemukan") jika hasil saringan kosong
            if (mountains.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE // Menampilkan teks kosong
                binding.rvMountains.visibility = View.GONE // Menyembunyikan list
            } else {
                binding.tvEmptyState.visibility = View.GONE // Menyembunyikan teks kosong
                binding.rvMountains.visibility = View.VISIBLE // Menampilkan list
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Mengosongkan referensi binding guna mencegah kebocoran memori (memory leak)
        _binding = null
    }
}
