package com.example.mounttrack.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mounttrack.R
import com.example.mounttrack.data.local.AppDatabase
import com.example.mounttrack.data.local.PreferenceManager
import com.example.mounttrack.data.repository.JournalRepository
import com.example.mounttrack.databinding.FragmentProfileBinding

/**
 * Halaman 10: Profil Pendaki.
 * Menampilkan nama pendaki, total pendakian, serta level pendakian yang dihitung otomatis.
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferenceManager: PreferenceManager

    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    private val repository by lazy { JournalRepository(database.journalDao()) }

    // Predefined Avatar URLs
    private val avatarUrls = listOf(
        "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=200&auto=format&fit=crop", // Hiker 1
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop", // Hiker 2
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&auto=format&fit=crop"  // Hiker 3
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())

        loadProfileData()
        setupObservers()
        setupListeners()
    }

    private fun loadProfileData() {
        binding.tvProfileName.text = preferenceManager.userName

        val avatar = preferenceManager.userAvatar
        if (avatar.isNotEmpty()) {
            Glide.with(this)
                .load(avatar)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(binding.ivAvatar)
        } else {
            // Gunakan salah satu avatar default jika kosong
            Glide.with(this)
                .load(avatarUrls[0])
                .placeholder(android.R.drawable.sym_def_app_icon)
                .into(binding.ivAvatar)
        }
    }

    private fun setupObservers() {
        // Ambil jumlah jurnal dari Room database secara realtime
        repository.journalCount.asLiveData().observe(viewLifecycleOwner) { count ->
            binding.tvStatsJournalsCount.text = count.toString()

            // Hitung level pendaki otomatis
            val level = when (count) {
                in 0..2 -> "Pemula"
                in 3..5 -> "Menengah"
                else -> "Berpengalaman"
            }
            binding.tvProfileLevel.text = "Level: $level"
        }
    }

    private fun setupListeners() {
        // Ubah Nama
        binding.btnEditProfile.setOnClickListener {
            showEditNameDialog()
        }

        // Klik Avatar untuk ubah foto
        binding.ivAvatar.setOnClickListener {
            showSelectAvatarDialog()
        }

        // Navigasi ke Pengaturan (Halaman 11)
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_settings)
        }
        
        // Tombol Logout
        binding.btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Konfirmasi Logout")
            builder.setMessage("Apakah Anda yakin ingin keluar?")
            builder.setPositiveButton("Ya") { _, _ ->
                preferenceManager.isLoggedIn = false
                val intent = android.content.Intent(requireContext(), com.example.mounttrack.ui.auth.LoginActivity::class.java)
                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            builder.setNegativeButton("Batal", null)
            builder.show()
        }
    }

    private fun showEditNameDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Ubah Nama Pengguna")

        val input = EditText(requireContext())
        input.setText(preferenceManager.userName)
        input.setSelection(input.text.length)
        
        // Atur padding input text agar rapi di dialog
        val container = FrameLayout(requireContext())
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = resources.getDimensionPixelSize(android.R.dimen.dialog_min_width_major) / 10
        params.rightMargin = resources.getDimensionPixelSize(android.R.dimen.dialog_min_width_major) / 10
        input.layoutParams = params
        container.addView(input)
        
        builder.setView(container)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val newName = input.text.toString().trim()
            if (newName.isNotEmpty()) {
                preferenceManager.userName = newName
                binding.tvProfileName.text = newName
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showSelectAvatarDialog() {
        val options = arrayOf("Petualang A", "Petualang B", "Petualang C")
        
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pilih Avatar Anda")
        builder.setItems(options) { dialog, which ->
            val selectedUrl = avatarUrls[which]
            preferenceManager.userAvatar = selectedUrl
            
            // Reload avatar
            Glide.with(this)
                .load(selectedUrl)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .into(binding.ivAvatar)
                
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
