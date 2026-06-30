package com.example.mounttrack.ui.calculator

// Import Bundle untuk menampung data parameter siklus hidup Fragment
import android.os.Bundle
// Import kelas-kelas UI dasar untuk memproses layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
// Import base class Fragment untuk membuat sub-halaman
import androidx.fragment.app.Fragment
// Import findNavController untuk memproses aksi navigasi Jetpack Navigation
import androidx.navigation.fragment.findNavController
// Import layout binding kelas generator untuk mengambil view secara aman
import com.example.mounttrack.databinding.FragmentCalculatorBinding
// Import NumberFormat untuk melakukan formasi mata uang rupiah
import java.text.NumberFormat
// Import Locale untuk memanggil format bahasa wilayah Indonesia
import java.util.Locale

/**
 * Halaman 7: Kalkulator Logistik & Estimasi Biaya.
 * Menghitung estimasi biaya dasar pendakian (Simaksi, Transport, Logistik Konsumsi)
 * secara offline berdasarkan input jumlah orang dan durasi pendakian harian.
 */
class CalculatorFragment : Fragment() {

    // Menyimpan referensi binding layout (nullable saat view hancur)
    private var _binding: FragmentCalculatorBinding? = null
    // Akses aman non-null ke binding
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflasi layout fragment_calculator.xml menggunakan view binding
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        // Mengembalikan root view dari binding layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listener klik tombol kembali untuk mundur ke fragment sebelumnya
        binding.btnCalcBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Listener klik tombol hitung estimasi biaya
        binding.btnCalculate.setOnClickListener {
            // Memanggil fungsi kalkulasi biaya
            calculateBudget()
        }
    }

    /**
     * Membaca input, melakukan validasi kosong, dan menghitung rincian pengeluaran logistik.
     */
    private fun calculateBudget() {
        // Membaca teks input jumlah anggota (personil)
        val personnelStr = binding.etPersonnel.text?.toString()
        // Membaca teks input durasi pendakian (hari)
        val daysStr = binding.etDays.text?.toString()

        // Variabel penanda validasi input form
        var isValid = true

        // Memeriksa jika input jumlah personil kosong
        if (personnelStr.isNullOrEmpty()) {
            // Tampilkan pesan error pada TextInputLayout personil
            binding.inputLayoutPersonnel.error = "Masukkan jumlah personil"
            isValid = false // Set penanda validasi gagal
        } else {
            // Bersihkan pesan error jika terisi
            binding.inputLayoutPersonnel.error = null
        }

        // Memeriksa jika input jumlah hari kosong
        if (daysStr.isNullOrEmpty()) {
            // Tampilkan pesan error pada TextInputLayout hari
            binding.inputLayoutDays.error = "Masukkan durasi hari"
            isValid = false // Set penanda validasi gagal
        } else {
            // Bersihkan pesan error jika terisi
            binding.inputLayoutDays.error = null
        }

        // Jika salah satu input tidak valid, hentikan eksekusi perhitungan
        if (!isValid) return

        // Mengubah teks input menjadi integer angka
        val personnel = personnelStr!!.toInt()
        val days = daysStr!!.toInt()

        // Menghitung Biaya Simaksi (Rp25.000 per orang per hari)
        val simaksiCost = personnel * days * 25000L
        // Menghitung Biaya Transportasi & Bensin (Rp50.000 per orang sekali jalan)
        val transportCost = personnel * 50000L
        // Menghitung Biaya Logistik Konsumsi (Rp40.000 per orang per hari)
        val logisticsCost = personnel * days * 40000L
        // Menjumlahkan total seluruh biaya dasar pendakian
        val totalCost = simaksiCost + transportCost + logisticsCost

        // Memformat masing-masing biaya menjadi bentuk teks rupiah (Rp)
        val simaksiFormatted = formatRupiah(simaksiCost)
        val transportFormatted = formatRupiah(transportCost)
        val logisticsFormatted = formatRupiah(logisticsCost)
        val totalFormatted = formatRupiah(totalCost)

        // Menampilkan teks hasil breakdown biaya ke layar XML
        binding.tvResultSimaksi.text = simaksiFormatted
        binding.tvResultTransport.text = transportFormatted
        binding.tvResultLogistics.text = logisticsFormatted
        binding.tvResultTotal.text = totalFormatted

        // Menampilkan card hasil perhitungan budget di layar
        binding.cardCalcResult.visibility = View.VISIBLE
    }

    /**
     * Memformat angka Long ke dalam format teks mata uang Rupiah Indonesia (contoh: Rp 150.000).
     */
    private fun formatRupiah(amount: Long): String {
        // Menginisialisasi format desimal lokal Indonesia (id - ID)
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
        // Menggabungkan prefix Rp dengan angka terformat
        return "Rp " + format.format(amount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Mengosongkan referensi binding guna mencegah kebocoran memori (memory leak)
        _binding = null
    }
}
