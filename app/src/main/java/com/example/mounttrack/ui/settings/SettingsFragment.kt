package com.example.mounttrack.ui.settings

// Import kelas Intent untuk meluncurkan browser eksternal
import android.content.Intent
// Import kelas Uri untuk melakukan parsing tautan alamat web
import android.net.Uri
// Import Bundle untuk menampung data parameter siklus hidup Fragment
import android.os.Bundle
// Import kelas-kelas UI dasar untuk memproses layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
// Import AppCompatDelegate untuk mengubah tema malam/siang secara dinamis
import androidx.appcompat.app.AppCompatDelegate
// Import base class Fragment untuk membuat sub-halaman
import androidx.fragment.app.Fragment
// Import findNavController untuk memproses aksi navigasi Jetpack Navigation
import androidx.navigation.fragment.findNavController
// Import PreferenceManager untuk membaca dan menyimpan preferensi tema & unit suhu
import com.example.mounttrack.data.local.PreferenceManager
// Import layout binding kelas generator untuk mengambil view secara aman
import com.example.mounttrack.databinding.FragmentSettingsBinding

/**
 * Halaman 11: Pengaturan & Atribusi API.
 * Menyediakan switch untuk satuan suhu (Celsius/Fahrenheit), pilihan tema aplikasi (Terang/Gelap),
 * dan atribusi WeatherAPI sebagai penyedia data cuaca gratis.
 */
class SettingsFragment : Fragment() {

    // Menyimpan referensi binding layout (nullable saat view hancur)
    private var _binding: FragmentSettingsBinding? = null
    // Akses aman non-null ke binding
    private val binding get() = _binding!!

    // Mendeklarasikan manager preferensi lokal
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflasi layout fragment_settings.xml menggunakan view binding
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        // Mengembalikan root view dari binding layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi PreferenceManager dengan konteks Fragment
        preferenceManager = PreferenceManager(requireContext())

        // Mengonfigurasi switch state awal berdasarkan preference tersimpan
        setupUI()
        // Menyiapkan listener aksi tombol klik & perubahan switch
        setupListeners()
    }

    /**
     * Setel status awal komponen tampilan berdasarkan preferensi lokal pengguna.
     */
    private fun setupUI() {
        // Mengatur switch satuan suhu (Checked = Celsius, Unchecked = Fahrenheit)
        binding.switchTempUnit.isChecked = preferenceManager.isCelsius

        // Mengatur switch tema (Checked = Dark Mode, Unchecked = Light Mode)
        binding.switchTheme.isChecked = preferenceManager.isDarkMode
    }

    /**
     * Mendefinisikan listener untuk perubahan komponen switch dan tombol klik.
     */
    private fun setupListeners() {
        // Listener tombol kembali di toolbar
        binding.btnSettingsBack.setOnClickListener {
            // Navigasi mundur ke fragment sebelumnya
            findNavController().navigateUp()
        }

        // Listener perubahan status switch satuan suhu
        binding.switchTempUnit.setOnCheckedChangeListener { _, isChecked ->
            // Simpan status baru (true untuk Celsius, false untuk Fahrenheit) ke preferences
            preferenceManager.isCelsius = isChecked
        }

        // Listener perubahan status switch tema aplikasi
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            // Simpan preferensi status baru (true untuk Dark Mode, false untuk Light Mode)
            preferenceManager.isDarkMode = isChecked
            // Terapkan perubahan tema gelap/terang secara langsung
            applyTheme(isChecked)
        }

        // Listener klik teks atribusi WeatherAPI untuk membuka tautan resmi
        binding.tvAttribution.setOnClickListener {
            // Membuat objek intent browser internet
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.weatherapi.com/"))
            // Menjalankan intent browser untuk menuju website WeatherAPI
            startActivity(intent)
        }
    }

    /**
     * Menerapkan tema gelap/terang secara langsung di aplikasi.
     */
    private fun applyTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            // Aktifkan mode malam sistem
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            // Aktifkan mode siang sistem
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Mengosongkan referensi binding guna mencegah kebocoran memori (memory leak)
        _binding = null
    }
}
