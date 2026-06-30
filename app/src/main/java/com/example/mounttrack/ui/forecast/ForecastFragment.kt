package com.example.mounttrack.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mounttrack.data.model.Mountain
import com.example.mounttrack.data.remote.WeatherApiService
import com.example.mounttrack.data.repository.WeatherRepository
import com.example.mounttrack.databinding.FragmentForecastBinding

/**
 * Halaman 5: Prakiraan Cuaca Mendalam.
 * Menampilkan rincian ramalan cuaca harian 3-7 hari ke depan untuk koordinat gunung yang dipilih.
 */
class ForecastFragment : Fragment() {

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private var mountain: Mountain? = null
    private lateinit var adapter: ForecastAdapter

    private val apiService = WeatherApiService.create()
    private val weatherRepository = WeatherRepository(apiService)
    private val viewModel: ForecastViewModel by viewModels {
        ForecastViewModelFactory(weatherRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        arguments?.let {
            mountain = it.getSerializable("mountain") as? Mountain
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        setupObservers()
        setupListeners()

        loadForecast()
    }

    private fun setupUI() {
        mountain?.let {
            binding.tvForecastMountainName.text = it.nama
        }
    }

    private fun setupRecyclerView() {
        adapter = ForecastAdapter()
        binding.rvForecast.layoutManager = LinearLayoutManager(requireContext())
        binding.rvForecast.adapter = adapter
    }

    private fun loadForecast() {
        mountain?.let {
            viewModel.fetchForecast(it.latitude, it.longitude)
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressForecast.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                binding.rvForecast.visibility = View.GONE
                binding.layoutForecastError.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                binding.layoutForecastError.visibility = View.VISIBLE
                binding.tvForecastErrorMsg.text = errorMsg
                binding.rvForecast.visibility = View.GONE
            } else {
                binding.layoutForecastError.visibility = View.GONE
            }
        }

        viewModel.forecastData.observe(viewLifecycleOwner) { forecastResponse ->
            if (forecastResponse != null) {
                binding.rvForecast.visibility = View.VISIBLE
                adapter.submitList(forecastResponse.forecast.forecastDays)
            }
        }
    }

    private fun setupListeners() {
        binding.btnForecastBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnForecastRetry.setOnClickListener {
            loadForecast()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
