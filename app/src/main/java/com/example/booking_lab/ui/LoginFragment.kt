package com.example.booking_lab.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.booking_lab.R
import com.example.booking_lab.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Deklarasi variabel Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Aksi saat tombol "Masuk" diklik
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()
            val password = binding.etPasswordLogin.text.toString().trim()

            // Validasi: Cek apakah ada kolom yang kosong
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Hentikan proses jika kosong
            }

            // Ubah tampilan tombol saat proses loading
            binding.btnLogin.text = "Memeriksa..."
            binding.btnLogin.isEnabled = false

            // Proses pencocokan data di Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""
                        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

                        dbRef.get().addOnSuccessListener { snapshot ->
                            val role = snapshot.child("role").value.toString()
                            Toast.makeText(requireContext(), "Login sebagai ${role.uppercase()}", Toast.LENGTH_SHORT).show()
                            kembalikanTombol()

                            // Arahkan ke dashboard yang sesuai
                            if (role == "admin") {
                                findNavController().navigate(R.id.action_loginFragment_to_adminDashboardFragment)
                            } else {
                                findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
                            }
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Gagal mengambil role", Toast.LENGTH_SHORT).show()
                            kembalikanTombol()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Login Gagal: Email atau Password salah", Toast.LENGTH_SHORT).show()
                        kembalikanTombol()
                    }
                }
        }

        // Aksi saat teks "Belum punya akun?" diklik
        binding.tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    // Fungsi untuk mengembalikan tombol ke keadaan semula
    private fun kembalikanTombol() {
        binding.btnLogin.text = "Masuk"
        binding.btnLogin.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}