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
import com.example.booking_lab.R
import com.example.booking_lab.adapter.BookingAdapter
import com.example.booking_lab.databinding.FragmentDashboardBinding
import com.example.booking_lab.model.JadwalLab
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (userId.isNotEmpty()) {
            // Ambil Nama User
            FirebaseDatabase.getInstance().getReference("users").child(userId)
                .get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        binding.tvGreeting.text = "Halo, ${snapshot.child("nama").value.toString()}!"
                        binding.tvRole.text = snapshot.child("role").value.toString().uppercase()
                    }
                }

            // Panggil fungsi muat data
            muatJadwalTersedia(userId)
            muatRiwayatBooking(userId)
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_dashboardFragment_to_loginFragment)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    binding.layoutHome.visibility = View.VISIBLE
                    binding.layoutRiwayat.visibility = View.GONE
                    true
                }
                R.id.nav_riwayat -> {
                    binding.layoutHome.visibility = View.GONE
                    binding.layoutRiwayat.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }
    }

    // Hanya tampilkan lab yang statusnya masih "Tersedia"
    private fun muatJadwalTersedia(userId: String) {
        FirebaseDatabase.getInstance().getReference("jadwal_lab")
            .orderByChild("status").equalTo("Tersedia")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<JadwalLab>()
                    for (data in snapshot.children) {
                        val jadwal = data.getValue(JadwalLab::class.java)
                        if (jadwal != null) list.add(jadwal)
                    }

                    if (_binding != null) {
                        binding.rvBeranda.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvBeranda.adapter = BookingAdapter(list) { jadwalTerpilih ->
                            // Jika list diklik, munculkan konfirmasi
                            tampilkanDialogBooking(jadwalTerpilih, userId)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // Hanya tampilkan lab yang sudah dibooking oleh Mahasiswa ini
    private fun muatRiwayatBooking(userId: String) {
        FirebaseDatabase.getInstance().getReference("jadwal_lab")
            .orderByChild("dipesanOleh").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<JadwalLab>()
                    for (data in snapshot.children) {
                        val jadwal = data.getValue(JadwalLab::class.java)
                        if (jadwal != null) list.add(jadwal)
                    }

                    if (_binding != null) {
                        binding.rvRiwayat.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvRiwayat.adapter = BookingAdapter(list) {
                            Toast.makeText(requireContext(), "Lab ini sudah kamu pesan.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // Proses pindah hak milik lab
    private fun tampilkanDialogBooking(jadwal: JadwalLab, userId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Booking")
            .setMessage("Apakah kamu yakin ingin membooking ${jadwal.namaLab} pada ${jadwal.tanggal} pukul ${jadwal.waktu}?")
            .setPositiveButton("Ya, Booking") { _, _ ->
                val dbRef = FirebaseDatabase.getInstance().getReference("jadwal_lab").child(jadwal.id)
                dbRef.child("status").setValue("Dipesan")
                dbRef.child("dipesanOleh").setValue(userId).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "Berhasil membooking lab!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}