package com.example.booking_lab.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booking_lab.adapter.JadwalAdapter
import com.example.booking_lab.databinding.FragmentPilihJadwalBinding
import com.example.booking_lab.model.JadwalLab
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PilihJadwalFragment : Fragment() {
    private var _binding: FragmentPilihJadwalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPilihJadwalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menerima data nama dan lokasi lab yang diklik dari Dashboard
        val namaLab = arguments?.getString("namaLab") ?: ""
        val lokasiLab = arguments?.getString("lokasiLab") ?: ""
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        binding.tvDetailNamaLab.text = namaLab

        // Mengambil hanya jadwal untuk Lab yang dipilih
        FirebaseDatabase.getInstance().getReference("jadwal_lab")
            .orderByChild("namaLab").equalTo(namaLab)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<JadwalLab>()
                    for (data in snapshot.children) {
                        val jadwal = data.getValue(JadwalLab::class.java)
                        if (jadwal != null) list.add(jadwal)
                    }
                    if (_binding != null) {
                        binding.rvDaftarJadwal.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvDaftarJadwal.adapter = JadwalAdapter(list, lokasiLab) { jadwalTerpilih ->
                            tampilkanDialogBooking(jadwalTerpilih, userId)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun tampilkanDialogBooking(jadwal: JadwalLab, userId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Reservasi")
            .setMessage("Reservasi ${jadwal.namaLab} pada ${jadwal.tanggal} pukul ${jadwal.waktu}?")
            .setPositiveButton("Ya, Reservasi") { _, _ ->
                val dbRef = FirebaseDatabase.getInstance().getReference("jadwal_lab").child(jadwal.id)
                dbRef.child("status").setValue("Dipesan")
                dbRef.child("dipesanOleh").setValue(userId).addOnCompleteListener {
                    Toast.makeText(requireContext(), "Berhasil mereservasi lab!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() // Kembali ke Dashboard setelah sukses
                }
            }
            .setNegativeButton("Batal", null).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}