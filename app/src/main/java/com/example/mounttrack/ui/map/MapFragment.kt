package com.example.mounttrack.ui.map

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mounttrack.data.model.Mountain
import com.example.mounttrack.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

/**
 * Halaman 6: Peta Lokasi Basecamp.
 * Mengintegrasikan Osmdroid (OpenStreetMap) untuk menandai koordinat basecamp gunung.
 * Menyediakan tombol untuk menavigasikan koordinat tersebut menggunakan peta eksternal (Intent Google Maps).
 */
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var mountain: Mountain? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Memuat konfigurasi Osmdroid sebelum UI ditampilkan
        val ctx = requireContext().applicationContext
        Configuration.getInstance().load(ctx, android.preference.PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = ctx.packageName

        arguments?.let {
            mountain = it.getSerializable("mountain") as? Mountain
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()
        setupListeners()
    }

    private fun setupMap() {
        mountain?.let { m ->
            binding.tvMapTitle.text = "Peta: ${m.nama}"

            // Konfigurasi MapView
            binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
            binding.mapView.setMultiTouchControls(true)
            binding.mapView.zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)

            val mapController = binding.mapView.controller
            mapController.setZoom(13.5)
            val mountainPoint = GeoPoint(m.latitude, m.longitude)
            mapController.setCenter(mountainPoint)

            // Tambahkan Marker Gunung
            val marker = Marker(binding.mapView)
            marker.position = mountainPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = m.nama
            marker.subDescription = "${m.tinggi_mdpl} MDPL, ${m.provinsi}"
            
            binding.mapView.overlays.add(marker)
            binding.mapView.invalidate()
        }
    }

    private fun setupListeners() {
        // Tombol Kembali
        binding.btnMapBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Buka lokasi di Google Maps / Maps Eksternal
        binding.fabExternalMap.setOnClickListener {
            mountain?.let { m ->
                val query = Uri.encode(m.nama)
                val gmmIntentUri = Uri.parse("geo:${m.latitude},${m.longitude}?q=$query")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                
                try {
                    startActivity(mapIntent)
                } catch (e: Exception) {
                    // Fallback jika aplikasi Google Maps tidak ada
                    val genericIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=${m.latitude},${m.longitude}")
                    )
                    startActivity(genericIntent)
                }
            } ?: run {
                Toast.makeText(requireContext(), "Data koordinat tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
