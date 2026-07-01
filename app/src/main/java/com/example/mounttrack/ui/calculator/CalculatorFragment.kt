package com.example.mounttrack.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mounttrack.data.model.Mountain
import com.example.mounttrack.data.repository.MountainRepository
import com.example.mounttrack.databinding.FragmentCalculatorBinding
import java.text.NumberFormat
import java.util.Locale

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    private lateinit var mountainList: List<Mountain>
    private var selectedMountain: Mountain? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mountainList = MountainRepository(requireContext()).getMountains()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mountainList.map { it.nama })
        binding.etMountain.setAdapter(adapter)

        binding.etMountain.setOnItemClickListener { _, _, position, _ ->
            selectedMountain = mountainList[position]
            binding.inputLayoutMountain.error = null
        }

        binding.btnCalcBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCalculate.setOnClickListener {
            calculateBudget()
        }
    }

    private fun calculateBudget() {
        val personnelStr = binding.etPersonnel.text?.toString()
        val daysStr = binding.etDays.text?.toString()

        var isValid = true

        if (selectedMountain == null) {
            binding.inputLayoutMountain.error = "Pilih gunung terlebih dahulu"
            isValid = false
        } else {
            binding.inputLayoutMountain.error = null
        }

        if (personnelStr.isNullOrEmpty()) {
            binding.inputLayoutPersonnel.error = "Masukkan jumlah personil"
            isValid = false
        } else {
            binding.inputLayoutPersonnel.error = null
        }

        if (daysStr.isNullOrEmpty()) {
            binding.inputLayoutDays.error = "Masukkan durasi hari"
            isValid = false
        } else {
            binding.inputLayoutDays.error = null
        }

        if (!isValid) return

        val personnel = personnelStr!!.toInt()
        val days = daysStr!!.toInt()
        val mountain = selectedMountain!!

        val simaksiCost = personnel * days * mountain.simaksi_per_hari.toLong()
        val transportCost = personnel * 50000L
        val logisticsCost = personnel * days * 40000L
        val parkingCost = mountain.tarif_parkir.toLong()
        
        val waterCost = if (!mountain.sumber_air_tersedia) {
            personnel * days * 20000L
        } else {
            0L
        }

        val totalCost = simaksiCost + transportCost + logisticsCost + parkingCost + waterCost

        binding.tvResultSimaksi.text = formatRupiah(simaksiCost)
        binding.tvResultTransport.text = formatRupiah(transportCost)
        binding.tvResultLogistics.text = formatRupiah(logisticsCost)
        binding.tvResultParking.text = formatRupiah(parkingCost)
        binding.tvResultWater.text = formatRupiah(waterCost)
        binding.tvResultTotal.text = formatRupiah(totalCost)

        if (mountain.sumber_air_tersedia) {
            binding.rowWater.visibility = View.GONE
        } else {
            binding.rowWater.visibility = View.VISIBLE
        }

        binding.cardCalcResult.visibility = View.VISIBLE
    }

    private fun formatRupiah(amount: Long): String {
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp " + format.format(amount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
