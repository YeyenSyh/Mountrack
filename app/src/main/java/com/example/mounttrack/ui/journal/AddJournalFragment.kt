package com.example.mounttrack.ui.journal

// Import DatePickerDialog untuk memproses dialog pemilih tanggal
import android.app.DatePickerDialog
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
// Import AppDatabase untuk instansiasi Room Database
import com.example.mounttrack.data.local.AppDatabase
// Import JournalEntity kelas model tabel untuk entri database
import com.example.mounttrack.data.model.JournalEntity
// Import JournalRepository untuk mengelola request DB lokal
import com.example.mounttrack.data.repository.JournalRepository
// Import layout binding kelas generator untuk mengambil view secara aman
import com.example.mounttrack.databinding.FragmentAddJournalBinding
// Import SimpleDateFormat untuk memformat tanggal millisecond menjadi string
import java.text.SimpleDateFormat
// Import Calendar untuk menampung data waktu kalender
import java.util.Calendar
// Import Locale untuk memanggil format bahasa wilayah Indonesia
import java.util.Locale

/**
 * Halaman 9 (Sub-page): Form Tambah Jurnal Baru.
 * Menginput data gunung, tanggal pendakian, dan catatan perjalanan.
 * Mendukung prefill nama gunung jika diarahkan dari Detail Gunung.
 */
class AddJournalFragment : Fragment() {

    // Menyimpan referensi binding layout (nullable saat view hancur)
    private var _binding: FragmentAddJournalBinding? = null
    // Akses aman non-null ke binding
    private val binding get() = _binding!!

    // Variabel penampung prefill nama gunung yang dikirim dari halaman detail gunung
    private var prefilledMountainName: String? = null

    // Menginisialisasi Room Database lokal secara lazy
    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    // Menginisialisasi JournalRepository secara lazy dengan menyertakan DAO
    private val repository by lazy { JournalRepository(database.journalDao()) }
    // Menginisialisasi ViewModel dengan menyertakan factory repository
    private val viewModel: JournalViewModel by viewModels {
        JournalViewModelFactory(repository)
    }

    // Instansiasi kalender penampung waktu saat ini
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Membaca data argument "mountainName" jika dikirim oleh halaman Detail
        arguments?.let {
            prefilledMountainName = it.getString("mountainName")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflasi layout fragment_add_journal.xml menggunakan view binding
        _binding = FragmentAddJournalBinding.inflate(inflater, container, false)
        // Mengembalikan root view dari binding layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menyiapkan data awal pre-filled
        setupPrefilledData()
        // Menyiapkan dialog pemilih tanggal (DatePicker)
        setupDatePicker()
        // Menyiapkan listener aksi tombol klik
        setupListeners()
    }

    /**
     * Memproses pengisian awal form jika diarahkan dari halaman Detail Gunung.
     */
    private fun setupPrefilledData() {
        prefilledMountainName?.let {
            // Set kolom teks nama gunung dengan data prefilled
            binding.etAddMountainName.setText(it)
        }
        
        // Mengubah teks input tanggal default menjadi hari ini
        updateDateLabel()
    }

    /**
     * Mengonfigurasi DatePickerDialog pada input tanggal.
     */
    private fun setupDatePicker() {
        // Menyiapkan callback listener ketika pengguna selesai memilih tanggal di dialog
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            // Update tahun pada kalender
            calendar.set(Calendar.YEAR, year)
            // Update bulan pada kalender
            calendar.set(Calendar.MONTH, month)
            // Update hari pada kalender
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            // Tulis tanggal terpilih ke form teks
            updateDateLabel()
        }

        // Listener klik kolom tanggal untuk meluncurkan DatePickerDialog
        binding.etAddDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener, // Callback listener
                calendar.get(Calendar.YEAR), // Tahun saat ini
                calendar.get(Calendar.MONTH), // Bulan saat ini
                calendar.get(Calendar.DAY_OF_MONTH) // Hari saat ini
            ).show() // Tampilkan dialog
        }
    }

    /**
     * Memformat tanggal objek kalender menjadi format string Indonesia (contoh: 30 Juni 2026).
     */
    private fun updateDateLabel() {
        val myFormat = "dd MMMM yyyy"
        // Menginisialisasi formatter dengan bahasa Indonesia (id - ID)
        val sdf = SimpleDateFormat(myFormat, Locale("id", "ID"))
        // Menuliskan hasil formasi tanggal ke kolom teks etAddDate
        binding.etAddDate.setText(sdf.format(calendar.time))
    }

    /**
     * Menghubungkan listener aksi tombol klik.
     */
    private fun setupListeners() {
        // Listener tombol kembali di toolbar
        binding.btnAddJournalBack.setOnClickListener {
            // Navigasi mundur ke fragment sebelumnya
            findNavController().navigateUp()
        }

        // Listener tombol simpan jurnal
        binding.btnSaveJournal.setOnClickListener {
            // Memanggil fungsi validasi dan penyimpanan database
            saveJournal()
        }
    }

    /**
     * Melakukan validasi kolom kosong, instansiasi entitas baru, dan menyimpan ke Room DB.
     */
    private fun saveJournal() {
        // Membaca teks nama gunung
        val mountainName = binding.etAddMountainName.text?.toString()?.trim()
        // Membaca teks tanggal pendakian
        val date = binding.etAddDate.text?.toString()?.trim()
        // Membaca teks catatan pendakian
        val notes = binding.etAddNotes.text?.toString()?.trim()

        // Variabel penanda validasi form
        var isValid = true

        // Memeriksa jika nama gunung kosong
        if (mountainName.isNullOrEmpty()) {
            binding.layoutAddMountain.error = "Nama gunung harus diisi"
            isValid = false
        } else {
            binding.layoutAddMountain.error = null
        }

        // Memeriksa jika tanggal kosong
        if (date.isNullOrEmpty()) {
            binding.layoutAddDate.error = "Tanggal harus dipilih"
            isValid = false
        } else {
            binding.layoutAddDate.error = null
        }

        // Memeriksa jika catatan kosong
        if (notes.isNullOrEmpty()) {
            binding.layoutAddNotes.error = "Catatan/Kesan harus diisi"
            isValid = false
        } else {
            binding.layoutAddNotes.error = null
        }

        // Jika salah satu input tidak valid, batalkan proses penyimpanan
        if (!isValid) return

        // Instansiasi entitas JournalEntity baru dengan data input
        val newJournal = JournalEntity(
            mountainName = mountainName!!,
            date = date!!,
            notes = notes!!
        )
        
        // Panggil fungsi insert di ViewModel (coroutine berjalan di background)
        viewModel.insert(newJournal)

        // Navigasi mundur kembali ke daftar jurnal pendakian
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Mengosongkan referensi binding guna mencegah kebocoran memori (memory leak)
        _binding = null
    }
}
