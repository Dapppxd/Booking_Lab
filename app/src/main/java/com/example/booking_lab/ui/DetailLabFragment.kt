package com.example.booking_lab.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.booking_lab.databinding.FragmentDetailLabBinding
import java.util.Calendar

class DetailLabFragment : Fragment() {

    private var _binding: FragmentDetailLabBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailLabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tombol Kembali
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Menampilkan Kalender saat kolom Tanggal diklik
        binding.etTanggal.setOnClickListener {
            val kalender = Calendar.getInstance()
            val tahun = kalender.get(Calendar.YEAR)
            val bulan = kalender.get(Calendar.MONTH)
            val hari = kalender.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val tanggalPilih = "$dayOfMonth/${month + 1}/$year"
                binding.etTanggal.setText(tanggalPilih)
            }, tahun, bulan, hari).show()
        }

        // Menampilkan Jam saat kolom Waktu diklik
        binding.etWaktu.setOnClickListener {
            val kalender = Calendar.getInstance()
            val jam = kalender.get(Calendar.HOUR_OF_DAY)
            val menit = kalender.get(Calendar.MINUTE)

            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                val waktuPilih = String.format("%02d:%02d", hourOfDay, minute)
                binding.etWaktu.setText(waktuPilih)
            }, jam, menit, true).show()
        }

        // Aksi Tombol Ajukan Peminjaman
        binding.btnSubmitBooking.setOnClickListener {
            val tujuan = binding.etTujuan.text.toString().trim()
            val tanggal = binding.etTanggal.text.toString().trim()
            val waktu = binding.etWaktu.text.toString().trim()

            if (tujuan.isEmpty() || tanggal.isEmpty() || waktu.isEmpty()) {
                Toast.makeText(requireContext(), "Harap lengkapi semua data!", Toast.LENGTH_SHORT).show()
            } else {
                // Nanti kita tambahkan logika simpan ke Firebase di sini
                Toast.makeText(requireContext(), "Memproses Booking...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}