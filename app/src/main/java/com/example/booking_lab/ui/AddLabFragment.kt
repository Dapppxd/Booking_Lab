package com.example.booking_lab.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.booking_lab.databinding.FragmentAddLabBinding
import com.example.booking_lab.model.JadwalLab
import com.google.firebase.database.FirebaseDatabase

class AddLabFragment : Fragment() {
    private var _binding: FragmentAddLabBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddLabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSimpanLab.setOnClickListener {
            val nama = binding.etNamaLabAdmin.text.toString().trim()
            val tanggal = binding.etTanggalAdmin.text.toString().trim()
            val waktu = binding.etWaktuAdmin.text.toString().trim()

            if (nama.isEmpty() || tanggal.isEmpty() || waktu.isEmpty()) {
                Toast.makeText(requireContext(), "Isi semua data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dbRef = FirebaseDatabase.getInstance().getReference("jadwal_lab")
            val id = dbRef.push().key ?: ""

            val jadwal = JadwalLab(id, nama, tanggal, waktu, "Tersedia", "")

            dbRef.child(id).setValue(jadwal).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Jadwal berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }
}