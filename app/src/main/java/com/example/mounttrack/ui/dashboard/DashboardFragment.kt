package com.example.mounttrack.ui.dashboard

// Import Bundle untuk menampung argumen status siklus hidup fragment
import android.os.Bundle
// Import kelas-kelas UI dasar untuk memproses layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
// Import base class Fragment untuk membuat sub-halaman
import androidx.fragment.app.Fragment
// Import viewModels untuk melakukan instansiasi ViewModel secara asinkron
import androidx.fragment.app.viewModels
// Import findNavController untuk memproses aksi navigasi Jetpack Navigation
import androidx.navigation.fragment.findNavController
// Import library Glide untuk memproses download gambar ikon cuaca dari URL
import com.bumptech.glide.Glide
// Import kelas R untuk mengakses resource ID aplikasi
import com.example.mounttrack.R
// Import PreferenceManager untuk membaca preferensi satuan suhu Celsius/Fahrenheit
import com.example.mounttrack.data.local.PreferenceManager
// Import WeatherApiService untuk instansiasi Retrofit service
import com.example.mounttrack.data.remote.WeatherApiService
// Import WeatherRepository untuk mengelola request API cuaca
import com.example.mounttrack.data.repository.WeatherRepository
// Import layout binding kelas generator untuk mengambil view secara aman
import com.example.mounttrack.databinding.FragmentDashboardBinding

/**
 * Halaman 2: Dashboard Utama.
 * Menampilkan informasi cuaca di lokasi terkini pendaki, serta menu navigasi cepat.
 */
class DashboardFragment : Fragment() {

    // Menyimpan referensi binding layout (nullable saat view hancur)
    private var _binding: FragmentDashboardBinding? = null
    // Akses aman non-null ke binding
    private val binding get() = _binding!!

    // Mendeklarasikan manager preferensi lokal
    private lateinit var preferenceManager: PreferenceManager
    
    // Inisialisasi API Service menggunakan builder Retrofit
    private val apiService = WeatherApiService.create()
    // Menyuntikkan service ke WeatherRepository
    private val weatherRepository = WeatherRepository(apiService)
    
    // Menginisialisasi ViewModel dengan menyertakan factory repository
    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(weatherRepository)
    }

    // Variabel latitude default Jakarta
    private var userLat: Double = -6.2088
    // Variabel longitude default Jakarta
    private var userLon: Double = 106.8456
    // Menampung status apakah GPS gagal membaca lokasi asli
    private var isFallbackGPS: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Membaca bundle extras dari Intent Activity utama jika ada
        activity?.intent?.let {
            // Ambil latitude lokasi user, default Jakarta jika kosong
            userLat = it.getDoubleExtra("EXTRA_LAT", -6.2088)
            // Ambil longitude lokasi user, default Jakarta jika kosong
            userLon = it.getDoubleExtra("EXTRA_LON", 106.8456)
            // Ambil flag apakah lokasi default yang digunakan
            isFallbackGPS = it.getBooleanExtra("EXTRA_IS_FALLBACK", true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflasi layout fragment_dashboard.xml menggunakan view binding
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        // Mengembalikan root view dari binding layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inisialisasi PreferenceManager dengan konteks Fragment
        preferenceManager = PreferenceManager(requireContext())

        // Mengatur banner peringatan GPS aktif/tidak berdasarkan data intent
        binding.cardGpsWarning.visibility = if (isFallbackGPS) View.VISIBLE else View.GONE

        // Menyiapkan observer data dari ViewModel
        setupObservers()
        // Menyiapkan listener aksi tombol klik
        setupListeners()

        // Memanggil fungsi pemuatan data cuaca
        loadWeather()
    }

    /**
     * Memanggil pemrosesan fetch data cuaca di ViewModel.
     */
    private fun loadWeather() {
        // Mengirim koordinat lokasi ke ViewModel untuk memproses API
        viewModel.fetchWeather(userLat, userLon)
    }

    /**
     * Menghubungkan LiveData ViewModel dengan komponen tampilan XML secara reaktif.
     */
    private fun setupObservers() {
        // Mengamati status loading dari LiveData
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Tampilkan progress bar ketika sedang meload, sembunyikan jika selesai
            binding.progressWeather.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Jika sedang memuat baru, hilangkan notifikasi error sebelumnya
            if (isLoading) {
                binding.layoutError.visibility = View.GONE
            }
        }

        // Mengamati status pesan error jika request gagal
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                // Tampilkan card layout error
                binding.layoutError.visibility = View.VISIBLE
                // Menetapkan deskripsi kesalahan ke teks tampilan
                binding.tvErrorMessage.text = errorMsg
            } else {
                // Sembunyikan card layout error jika tidak ada kendala
                binding.layoutError.visibility = View.GONE
            }
        }

        // Mengamati data objek cuaca sukses ter-load
        viewModel.weatherData.observe(viewLifecycleOwner) { weather ->
            if (weather != null) {
                // Set teks nama kota lokasi cuaca saat ini
                binding.tvLocationName.text = "${weather.location.name}, ${weather.location.country}"
                
                // Mengambil status preferensi satuan suhu (°C atau °F)
                val isCelsius = preferenceManager.isCelsius
                // Menentukan nilai suhu yang akan ditampilkan sesuai preferensi
                val temp = if (isCelsius) weather.current.tempC else weather.current.tempF
                // Menentukan teks simbol satuan
                val tempUnit = if (isCelsius) "°C" else "°F"
                // Mengubah nilai suhu menjadi integer bulat dan menampilkannya
                binding.tvTemperature.text = "${temp.toInt()}$tempUnit"
                
                // Menampilkan nama status cuaca (contoh: "Hujan Ringan")
                binding.tvWeatherCondition.text = weather.current.condition.text
                // Menampilkan kecepatan angin saat ini
                binding.tvWindSpeed.text = "${weather.current.windKph} km/h"
                // Menampilkan kelembaban udara saat ini
                binding.tvHumidity.text = "${weather.current.humidity}%"

                // Membuat URL ikon cuaca dari WeatherAPI
                val iconUrl = "https:${weather.current.condition.icon}"
                // Mendownload dan memuat gambar ikon cuaca menggunakan Glide
                Glide.with(this)
                    .load(iconUrl) // Mengunduh dari tautan URL
                    .placeholder(R.drawable.ic_dashboard) // Placeholder jika lambat
                    .into(binding.ivWeatherIcon) // Menetapkan ke ImageView tujuan
            }
        }
    }

    /**
     * Mendefinisikan klik-listener untuk interaksi tombol di layar.
     */
    private fun setupListeners() {
        // Listener tombol coba lagi pada layar error cuaca
        binding.btnRetry.setOnClickListener {
            // Memanggil ulang proses pemuatan data cuaca
            loadWeather()
        }

        // Listener navigasi cepat ke halaman Eksplorasi Gunung
        binding.btnQuickExplore.setOnClickListener {
            // Berpindah tab menggunakan rute aksi NavController
            findNavController().navigate(R.id.action_dashboard_to_exploration)
        }

        // Listener navigasi cepat ke halaman Kalkulator Biaya
        binding.btnQuickCalculator.setOnClickListener {
            // Berpindah ke Fragment Kalkulator
            findNavController().navigate(R.id.navigation_calculator)
        }

        // Listener navigasi cepat ke halaman Checklist Perlengkapan
        binding.btnQuickChecklist.setOnClickListener {
            // Berpindah ke Fragment Checklist
            findNavController().navigate(R.id.navigation_checklist)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Mengosongkan referensi binding guna mencegah kebocoran memori (memory leak)
        _binding = null
    }
}
