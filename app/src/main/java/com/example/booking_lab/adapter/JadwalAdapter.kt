package com.example.booking_lab.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.booking_lab.databinding.ItemJadwalBinding
import com.example.booking_lab.model.JadwalLab

class JadwalAdapter(
    private val listJadwal: List<JadwalLab>,
    private val lokasiLab: String,
    private val onItemClick: (JadwalLab) -> Unit
) : RecyclerView.Adapter<JadwalAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemJadwalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemJadwalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jadwal = listJadwal[position]
        holder.binding.tvJadwalNamaLab.text = jadwal.namaLab
        holder.binding.tvJadwalLokasi.text = lokasiLab
        holder.binding.tvJadwalWaktu.text = "${jadwal.tanggal} | Pukul ${jadwal.waktu}"
        holder.binding.tvJadwalStatus.text = jadwal.status

        if (jadwal.status == "Tersedia") {
            holder.binding.tvJadwalStatus.setTextColor(Color.parseColor("#1565C0")) // Warna biru
            holder.itemView.alpha = 1.0f
            holder.itemView.setOnClickListener { onItemClick(jadwal) }
        } else {
            holder.binding.tvJadwalStatus.setTextColor(Color.GRAY)
            holder.binding.tvJadwalStatus.text = "Tidak Tersedia"
            holder.itemView.alpha = 0.7f // Efek redup jika tidak tersedia
            holder.itemView.setOnClickListener(null) // Matikan klik
        }
    }

    override fun getItemCount() = listJadwal.size
}