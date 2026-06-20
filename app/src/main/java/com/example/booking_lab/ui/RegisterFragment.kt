package com.example.booking_lab.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.booking_lab.R
import com.example.booking_lab.databinding.FragmentRegisterBinding
import com.example.booking_lab.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmailRegister.text.toString().trim()
            val password = binding.etPasswordRegister.text.toString().trim()
            val confirmPassword = binding.etConfirmPasswordRegister.text.toString().trim()

            val role = if (binding.rbAdmin.isChecked) "admin" else "mahasiswa"

            // Mengambil nama otomatis dari awalan email (karena form nama dihapus di desain baru)
            val nama = if (email.contains("@")) email.substringBefore("@") else "User"

            // Validasi kolom kosong
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi panjang password
            if (password.length < 6) {
                Toast.makeText(requireContext(), "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi kecocokan password dengan konfirmasi password
            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Konfirmasi password tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnRegister.text = "Mendaftar..."
            binding.btnRegister.isEnabled = false

            // Proses pembuatan akun
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""
                        val user = User(nama, email, role)

                        val database = FirebaseDatabase.getInstance().getReference("users")
                        database.child(userId).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(requireContext(), "Pendaftaran Berhasil!", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                } else {
                                    Toast.makeText(requireContext(), "Gagal menyimpan data pengguna", Toast.LENGTH_SHORT).show()
                                    kembalikanTombol()
                                }
                            }
                    } else {
                        Toast.makeText(requireContext(), "Gagal mendaftar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        kembalikanTombol()
                    }
                }
        }
    }

    private fun kembalikanTombol() {
        binding.btnRegister.text = "Daftar"
        binding.btnRegister.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}