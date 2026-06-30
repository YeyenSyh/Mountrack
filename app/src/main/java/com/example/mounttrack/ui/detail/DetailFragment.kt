package com.example.mounttrack.ui.detail

// Import Bundle untuk menampung data parameter siklus hidup Fragment
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
// Import library Glide untuk mendownload gambar ikon cuaca dari URL
import com.bumptech.glide.Glide
// Import kelas R untuk mengakses resource ID aplikasi
import com.example.mounttrack.R
// Import PreferenceManager untuk membaca preferensi satuan suhu Celsius/Fahrenheit
import com.example.mounttrack.data.local.PreferenceManager
// Import kelas model Mountain untuk menampung objek gunung
import com.example.mounttrack.data.model.Mountain
// Import WeatherApiService untuk instansiasi Retrofit service
import com.example.mounttrack.data.remote.WeatherApiService
// Import WeatherRepository untuk mengelola request API cuaca
import com.example.mounttrack.data.repository.WeatherRepository
// Import layout binding kelas generator untuk mengambil view secara aman
import com.example.mounttrack.databinding.FragmentDetailBinding

/**
 * Halaman 4: Detail Gunung & Cuaca Real-Time.
 * Menampilkan deskripsi detail gunung beserta info cuaca puncak secara real-time.
 */
class DetailFragment : Fragment() {

    // Menyimpan referensi binding layout (nullable saat view hancur)
    private var _binding: FragmentDetailBinding? = null
    // Akses aman non-null ke binding
    private val binding get() = _binding!!

    // Variabel penampung data gunung yang dikirim dari halaman eksplorasi
    private var mountain: Mountain? = null
    // Mendeklarasikan manager preferensi lokal
    private lateinit var preferenceManager: PreferenceManager

    // Inisialisasi API Service menggunakan builder Retrofit
    private val apiService = WeatherApiService.create()
    // Menyuntikkan service ke WeatherRepository
    private val weatherRepository = WeatherRepository(apiService)
    // Menginisialisasi ViewModel dengan menyertakan factory repository
    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(weatherRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Membaca argumen serializable "mountain" dari Bundle navigasi
        arguments?.let {
            mountain = it.getSerializable("mountain") as? Mountain
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflasi layout fragment_detail.xml menggunakan view binding
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        // Mengembalikan root view dari binding layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inisialisasi PreferenceManager dengan konteks Fragment
        preferenceManager = PreferenceManager(requireContext())

        // Mengisi data statis gunung ke komponen teks tampilan layar
        setupMountainDetails()
        // Menyiapkan observer data dari ViewModel
        setupObservers()
        // Menyiapkan listener aksi tombol klik
        setupListeners()

        // Memanggil API cuaca menggunakan koordinat lintang/bujur gunung jika datanya valid
        mountain?.let {
            viewModel.fetchWeather(it.latitude, it.longitude)
        }
    }

    /**
     * Memasukkan data profil gunung ke layar detail.
     */
    private fun setupMountainDetails() {
        mountain?.let {
            // Menetapkan nama gunung ke toolbar title
            binding.tvResultTitleBeforeLoaded(it.nama)
            // Menetapkan provinsi gunung
            binding.tvDetailProvince.text = it.provinsi
            // Menetapkan tinggi gunung (MDPL)
            binding.tvDetailElevation.text = "${it.tinggi_mdpl} MDPL"
            // Menetapkan deskripsi gunung
            binding.tvDetailDescription.text = it.deskripsi
        }
    }

    /**
     * Helper extension untuk mengganti teks title toolbar secara aman.
     */
    private fun FragmentDetailBinding.tvResultTitleBeforeLoaded(name: String) {
        tvDetailTitle.text = name
    }

    /**
     * Menghubungkan LiveData ViewModel dengan komponen tampilan XML secara reaktif.
     */
    private fun setupObservers() {
        // Mengamati status loading dari LiveData
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Tampilkan progress bar ketika sedang meload, sembunyikan jika selesai
            binding.progressDetailWeather.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Jika sedang memuat baru, hilangkan info cuaca dan notifikasi error sebelumnya
            if (isLoading) {
                binding.groupWeatherInfo.visibility = View.GONE
                binding.layoutDetailWeatherError.visibility = View.GONE
            }
        }

        // Mengamati status pesan error jika request gagal
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                // Tampilkan layout error khusus cuaca detail
                binding.layoutDetailWeatherError.visibility = View.VISIBLE
                // Sembunyikan grup data cuaca
                binding.groupWeatherInfo.visibility = View.GONE
            } else {
                // Sembunyikan layout error jika tidak ada kendala
                binding.layoutDetailWeatherError.visibility = View.GONE
            }
        }

        // Mengamati data objek cuaca sukses ter-load
        viewModel.weatherData.observe(viewLifecycleOwner) { weather ->
            if (weather != null) {
                // Tampilkan grup data cuaca
                binding.groupWeatherInfo.visibility = View.VISIBLE
                // Sembunyikan layout error
                binding.layoutDetailWeatherError.visibility = View.GONE

                // Mengambil status preferensi satuan suhu (°C atau °F)
                val isCelsius = preferenceManager.isCelsius
                // Menentukan nilai suhu yang akan ditampilkan sesuai preferensi
                val temp = if (isCelsius) weather.current.tempC else weather.current.tempF
                // Menentukan teks simbol satuan
                val tempUnit = if (isCelsius) "°C" else "°F"
                // Mengubah nilai suhu menjadi integer bulat dan menampilkannya
                binding.tvDetailTemperature.text = "${temp.toInt()}$tempUnit"
                
                // Menampilkan nama status cuaca (contoh: "Cerah Berawan")
                binding.tvDetailCondition.text = weather.current.condition.text
                // Menampilkan kecepatan angin puncak saat ini
                binding.tvDetailWind.text = "${weather.current.windKph} km/h"
                // Menampilkan kelembaban udara puncak saat ini
                binding.tvDetailHumidity.text = "${weather.current.humidity}%"

                // Membuat URL ikon cuaca dari WeatherAPI
                val iconUrl = "https:${weather.current.condition.icon}"
                // Mendownload dan memuat gambar ikon cuaca menggunakan Glide
                Glide.with(this)
                    .load(iconUrl) // Mengunduh dari tautan URL
                    .placeholder(R.drawable.ic_exploration) // Placeholder jika lambat
                    .into(binding.ivDetailWeatherIcon) // Menetapkan ke ImageView tujuan
            }
        }
    }

    /**
     * Mendefinisikan klik-listener untuk interaksi tombol di layar.
     */
    private fun setupListeners() {
        // Listener tombol kembali di toolbar
        binding.btnBack.setOnClickListener {
            // Navigasi mundur ke fragment sebelumnya
            findNavController().navigateUp()
        }

        // Listener tombol coba lagi pada cuaca jika terjadi gangguan koneksi
        binding.btnDetailRetry.setOnClickListener {
            mountain?.let {
                viewModel.fetchWeather(it.latitude, it.longitude)
            }
        }

        // Listener tombol navigasi ke halaman Prakiraan Cuaca Harian (Halaman 5)
        binding.btnForecast.setOnClickListener {
            mountain?.let {
                // Membuat Bundle berisi data gunung saat ini
                val bundle = Bundle().apply {
                    putSerializable("mountain", it)
                }
                // Navigasi ke fragment Forecast
                findNavController().navigate(R.id.action_detail_to_forecast, bundle)
            }
        }

        // Listener tombol navigasi ke halaman Peta Lokasi Basecamp (Halaman 6)
        binding.btnMap.setOnClickListener {
            mountain?.let {
                // Membuat Bundle berisi data gunung saat ini
                val bundle = Bundle().apply {
                    putSerializable("mountain", it)
                }
                // Navigasi ke fragment Map
                findNavController().navigate(R.id.action_detail_to_map, bundle)
            }
        }

        // Listener tombol navigasi ke halaman Tambah Jurnal baru dengan membawa pre-fill nama gunung (Halaman 9)
        binding.btnAddToJournal.setOnClickListener {
            mountain?.let {
                // Membuat Bundle berisi string nama gunung saat ini
                val bundle = Bundle().apply {
                    putString("mountainName", it.nama)
                }
                // Navigasi ke fragment AddJournal
                findNavController().navigate(R.id.action_detail_to_addJournal, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Mengosongkan referensi binding guna mencegah kebocoran memori (memory leak)
        _binding = null
    }
}
