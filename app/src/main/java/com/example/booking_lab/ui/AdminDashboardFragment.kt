package com.example.booking_lab.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.booking_lab.R
import com.example.booking_lab.databinding.FragmentAdminDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardFragment : Fragment() {
    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTambahLab.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_addLabFragment)
        }

        binding.btnLogoutAdmin.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_adminDashboardFragment_to_loginFragment)
        }
    }
}