package com.example.mounttrack.ui.splash

// Import library Manifest Android untuk mendeteksi izin sistem
import android.Manifest
// Import library SuppressLint untuk mengabaikan peringatan linter splash screen kustom
import android.annotation.SuppressLint
// Import kelas Intent untuk melakukan perpindahan halaman (navigasi) ke MainActivity
import android.content.Intent
// Import kelas PackageManager untuk membandingkan status izin aplikasi
import android.content.pm.PackageManager
// Import kelas Location untuk menampung koordinat lintang dan bujur (latitude & longitude)
import android.location.Location
// Import kelas Bundle untuk menyimpan state parameter siklus hidup android
import android.os.Bundle
// Import kelas Handler untuk mengeksekusi kode tertunda (delay)
import android.os.Handler
// Import kelas Looper untuk memproses penundaan di Main Thread (thread UI)
import android.os.Looper
// Import kelas Toast untuk menampilkan notifikasi singkat di layar
import android.widget.Toast
// Import kelas ActivityResultContracts untuk memproses callback request izin runtime
import androidx.activity.result.contract.ActivityResultContracts
// Import kelas AppCompatActivity sebagai base class Activity Android
import androidx.appcompat.app.AppCompatActivity
// Import kelas ContextCompat untuk mengecek izin dengan kompatibilitas versi android lama
import androidx.core.content.ContextCompat
// Import kelas ActivitySplashBinding hasil generator View Binding untuk layout splash screen
import com.example.mounttrack.databinding.ActivitySplashBinding
// Import kelas MainActivity sebagai halaman utama aplikasi
import com.example.mounttrack.ui.MainActivity
// Import FusedLocationProviderClient untuk mengakses API lokasi Google Play Services
import com.google.android.gms.location.FusedLocationProviderClient
// Import LocationServices sebagai builder untuk instansiasi penyedia lokasi
import com.google.android.gms.location.LocationServices

/**
 * Halaman 1: Splash Screen & Izin Lokasi.
 * Mengambil koordinat GPS pengguna menggunakan FusedLocationProviderClient.
 * Jika izin ditolak, menggunakan koordinat default Jakarta (-6.2088, 106.8456).
 */
@SuppressLint("CustomSplashScreen") // Menghilangkan peringatan pembuatan Splash Screen kustom pada Android 12+
class SplashActivity : AppCompatActivity() {

    // Mendeklarasikan variabel binding untuk layout activity_splash.xml
    private lateinit var binding: ActivitySplashBinding
    // Mendeklarasikan variabel klien lokasi Google Services
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Menentukan garis lintang default (Jakarta) jika GPS tidak aktif/izin ditolak
    private val defaultLat = -6.2088
    // Menentukan garis bujur default (Jakarta) jika GPS tidak aktif/izin ditolak
    private val defaultLon = 106.8456

    // Mendaftarkan objek launcher untuk menangani izin lokasi secara runtime
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions() // Menggunakan kontrak permintaan multi izin
    ) { permissions ->
        // Mendapatkan status persetujuan izin lokasi presisi (Fine Location)
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        // Mendapatkan status persetujuan izin lokasi perkiraan (Coarse Location)
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        // Memeriksa jika salah satu izin disetujui oleh pengguna
        if (fineGranted || coarseGranted) {
            // Mengubah status teks pemberitahuan pada layar splash screen
            binding.tvPermissionNotice.text = "Izin disetujui. Membaca lokasi..."
            // Memanggil fungsi untuk membaca GPS lokasi terkini pengguna
            getLastLocationAndNavigate()
        } else {
            // Mengubah status teks pemberitahuan ketika izin ditolak
            binding.tvPermissionNotice.text = "Izin ditolak. Menggunakan lokasi default..."
            // Menampilkan toast bahwa lokasi dialihkan menggunakan default Jakarta
            Toast.makeText(this, "Izin lokasi ditolak, menggunakan lokasi default", Toast.LENGTH_SHORT).show()
            // Menunda transisi ke halaman utama selama 1 detik agar pengguna sempat melihat pesan notice
            Handler(Looper.getMainLooper()).postDelayed({
                // Meluncurkan MainActivity dengan koordinat default Jakarta dan flag fallback bernilai true
                navigateToMain(defaultLat, defaultLon, true)
            }, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Memanggil konstruktor superclass AppCompatActivity
        binding = ActivitySplashBinding.inflate(layoutInflater) // Melakukan inflasi layout dengan binding inflater
        setContentView(binding.root) // Menetapkan root view binding sebagai konten tampilan utama activity

        // Menginisialisasi penyedia layanan lokasi menggunakan konteks activity saat ini
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Menunda pengecekan izin selama 1.5 detik demi memunculkan efek branding logo di layar splash
        Handler(Looper.getMainLooper()).postDelayed({
            // Memanggil metode untuk memeriksa izin lokasi
            checkLocationPermissions()
        }, 1500)
    }

    /**
     * Memeriksa apakah izin lokasi sudah diberikan secara internal dalam sistem OS.
     */
    private fun checkLocationPermissions() {
        // Mengecek apakah izin FINE atau COARSE location sudah terdaftar aktif sebelumnya
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            // Menampilkan status membaca lokasi karena izin sudah aktif
            binding.tvPermissionNotice.text = "Izin terdeteksi. Membaca lokasi..."
            // Menjalankan pembacaan lokasi GPS
            getLastLocationAndNavigate()
        } else {
            // Mengubah status teks jika aplikasi baru pertama kali dibuka dan belum memiliki izin
            binding.tvPermissionNotice.text = "Meminta izin lokasi..."
            // Meluncurkan dialog sistem untuk meminta persetujuan izin lokasi
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION, // Meminta izin lokasi akurat
                    Manifest.permission.ACCESS_COARSE_LOCATION // Meminta izin lokasi perkiraan
                )
            )
        }
    }

    /**
     * Mengambil lokasi terakhir pengguna secara asinkron dari Google Services.
     */
    @SuppressLint("MissingPermission") // Menghilangkan warning pengecekan izin karena sudah diproses di checkLocationPermissions
    private fun getLastLocationAndNavigate() {
        // Melakukan request lokasi GPS terakhir yang tersimpan di memori perangkat
        fusedLocationClient.lastLocation
            // Menambahkan callback listener jika request sukses dijalankan
            .addOnSuccessListener { location: Location? ->
                // Memeriksa jika data lokasi tidak null
                if (location != null) {
                    // Berpindah ke MainActivity membawa koordinat asli user dan flag fallback false (GPS aktif)
                    navigateToMain(location.latitude, location.longitude, false)
                } else {
                    // Jika GPS aktif namun memori koordinat null, dialihkan ke lokasi default Jakarta
                    navigateToMain(defaultLat, defaultLon, true)
                }
            }
            // Menambahkan callback listener jika request mengalami kegagalan sistem
            .addOnFailureListener {
                // Dialihkan ke lokasi default Jakarta dengan flag fallback true
                navigateToMain(defaultLat, defaultLon, true)
            }
    }

    /**
     * Mengirim data lokasi ke MainActivity (atau LoginActivity) dan memulai activity baru.
     */
    private fun navigateToMain(lat: Double, lon: Double, isFallback: Boolean) {
        val prefs = com.example.mounttrack.data.local.PreferenceManager(this)
        val targetClass = if (prefs.isLoggedIn) MainActivity::class.java else com.example.mounttrack.ui.auth.LoginActivity::class.java
        
        // Membuat objek Intent baru untuk memulai Activity dari SplashActivity
        val intent = Intent(this, targetClass).apply {
            putExtra("EXTRA_LAT", lat) // Menyisipkan data latitude (garis lintang)
            putExtra("EXTRA_LON", lon) // Menyisipkan data longitude (garis bujur)
            putExtra("EXTRA_IS_FALLBACK", isFallback) // Menyisipkan data status fallback lokasi
        }
        startActivity(intent) // Memulai target activity baru
        finish() // Menutup SplashActivity agar tidak bisa kembali saat menekan tombol back
    }
}
