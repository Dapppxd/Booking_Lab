package com.example.booking_lab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.booking_lab.databinding.ItemBookingBinding
import com.example.booking_lab.model.JadwalLab

class BookingAdapter(
    private val listJadwal: List<JadwalLab>,
    private val onItemClick: (JadwalLab) -> Unit // Fungsi deteksi klik item
) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jadwal = listJadwal[position]
        holder.binding.tvNamaLabItem.text = jadwal.namaLab
        holder.binding.tvWaktuItem.text = "${jadwal.tanggal} | Pukul: ${jadwal.waktu}"
        holder.binding.tvStatusItem.text = jadwal.status

        // Pewarnaan status otomatis
        when (jadwal.status) {
            "Tersedia" -> holder.binding.tvStatusItem.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_green_dark))
            "Dipesan" -> holder.binding.tvStatusItem.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark))
            else -> holder.binding.tvStatusItem.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray))
        }

        // Pemicu aksi saat item diklik
        holder.itemView.setOnClickListener {
            onItemClick(jadwal)
        }
    }

    override fun getItemCount(): Int = listJadwal.size
}