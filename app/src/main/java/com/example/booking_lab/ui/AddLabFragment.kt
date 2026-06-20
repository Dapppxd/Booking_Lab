package com.example.booking_lab.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.booking_lab.databinding.FragmentAddLabBinding
import com.example.booking_lab.model.JadwalLab
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class AddLabFragment : Fragment() {
    private var _binding: FragmentAddLabBinding? = null
    private val binding get() = _binding!!

    // Data Statis untuk Dropdown
    private val pilihanLab = arrayOf("Lab. Komputer", "Lab. Antena", "Lab. Radio")
    private val pilihanWaktu = arrayOf(
        "Pukul 07.00 - 08.40",
        "Pukul 08.40 - 10.50",
        "Pukul 10.50 - 13.30",
        "Pukul 13.30 - 15.10",
        "Pukul 15.10 - 16.00"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddLabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Setup Spinner Nama Lab
        val adapterLab = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, pilihanLab)
        binding.spinnerNamaLab.adapter = adapterLab

        // 2. Setup Spinner Waktu
        val adapterWaktu = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, pilihanWaktu)
        binding.spinnerWaktuAdmin.adapter = adapterWaktu

        // 3. Setup Pop-up Kalender untuk Tanggal
        binding.etTanggalAdmin.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Format bulan ke dalam bahasa Indonesia
                val daftarBulan = arrayOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
                val tanggalFormat = "$selectedDay ${daftarBulan[selectedMonth]} $selectedYear"

                binding.etTanggalAdmin.setText(tanggalFormat)
            }, year, month, day)

            // Opsional: Mencegah Admin memilih tanggal di masa lalu
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        // 4. Logika Tombol Simpan
        binding.btnSimpanLab.setOnClickListener {
            val nama = binding.spinnerNamaLab.selectedItem.toString()
            val tanggal = binding.etTanggalAdmin.text.toString().trim()
            val waktu = binding.spinnerWaktuAdmin.selectedItem.toString()

            if (tanggal.isEmpty()) {
                Toast.makeText(requireContext(), "Silakan pilih tanggal terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dbRef = FirebaseDatabase.getInstance().getReference("jadwal_lab")
            val id = dbRef.push().key ?: ""

            val jadwal = JadwalLab(id, nama, tanggal, waktu, "Tersedia", "")

            dbRef.child(id).setValue(jadwal).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Jadwal $nama berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}