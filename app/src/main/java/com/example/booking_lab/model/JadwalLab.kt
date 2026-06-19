package com.example.booking_lab.model

data class JadwalLab(
    val id: String = "",
    val namaLab: String = "",
    val tanggal: String = "",
    val waktu: String = "",
    val status: String = "Tersedia", // Default saat admin baru menambahkan
    val dipesanOleh: String = "" // Akan diisi nama mahasiswa jika sudah dibooking
)