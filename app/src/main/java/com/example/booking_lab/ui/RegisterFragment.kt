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

    // Deklarasi variabel Firebase
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

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Aksi saat tombol "Daftar" diklik
        binding.btnRegister.setOnClickListener {
            val nama = binding.etNamaRegister.text.toString().trim()
            val email = binding.etEmailRegister.text.toString().trim()
            val password = binding.etPasswordRegister.text.toString().trim()

            // Menentukan role berdasarkan RadioButton yang dipilih
            val role = if (binding.rbAdmin.isChecked) "admin" else "mahasiswa"

            // Validasi: Cek kolom kosong
            if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Semua data diri harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Hentikan proses jika ada yang kosong
            }

            // Validasi: Password minimal 6 karakter (syarat wajib dari Firebase)
            if (password.length < 6) {
                Toast.makeText(requireContext(), "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ubah tampilan tombol saat proses loading
            binding.btnRegister.text = "Mendaftar..."
            binding.btnRegister.isEnabled = false

            // Proses pembuatan akun di Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Jika akun berhasil dibuat, simpan data tambahan (nama & role) ke Realtime Database
                        val userId = auth.currentUser?.uid ?: ""
                        val user = User(nama, email, role) // Memanggil Data Class yang sudah kita buat

                        val database = FirebaseDatabase.getInstance().getReference("users")
                        database.child(userId).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(requireContext(), "Pendaftaran Berhasil!", Toast.LENGTH_SHORT).show()
                                    // Pindah otomatis ke halaman Login setelah sukses
                                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                } else {
                                    Toast.makeText(requireContext(), "Gagal menyimpan data pengguna", Toast.LENGTH_SHORT).show()
                                    kembalikanTombol()
                                }
                            }
                    } else {
                        // Jika gagal (misal email sudah terdaftar atau format salah)
                        Toast.makeText(requireContext(), "Gagal mendaftar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        kembalikanTombol()
                    }
                }
        }

        // Aksi saat teks "Sudah punya akun?" diklik
        binding.tvGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    // Fungsi untuk mengembalikan tombol ke keadaan semula jika terjadi error
    private fun kembalikanTombol() {
        binding.btnRegister.text = "Daftar"
        binding.btnRegister.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}