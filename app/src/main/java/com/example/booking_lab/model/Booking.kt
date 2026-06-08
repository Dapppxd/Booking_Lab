package com.example.booking_lab.model

data class Booking(
    val userId: String = "",
    val ruanganId: String = "",
    val nama_peminjam: String = "",
    val ruangan: String = "",
    val tanggal: String = "",
    val jam_mulai: String = "",
    val jam_selesai: String = "",
    val keperluan: String = "",
    val status: String = "menunggu",
    val created_at: Long = 0L
)