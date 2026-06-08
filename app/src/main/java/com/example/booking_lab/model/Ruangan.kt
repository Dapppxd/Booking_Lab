package com.example.booking_lab.model

data class Ruangan(
    val nama: String = "",
    val kapasitas: Int = 0,
    val deskripsi: String = "",
    val foto_url: String = "",
    val tersedia: Boolean = true
)