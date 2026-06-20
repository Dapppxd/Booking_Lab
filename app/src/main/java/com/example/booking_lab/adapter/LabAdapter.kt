package com.example.booking_lab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.booking_lab.databinding.ItemBookingBinding

data class RuanganLab(val nama: String, val lokasi: String)

class LabAdapter(
    private val listLab: List<RuanganLab>,
    private val onItemClick: (RuanganLab) -> Unit
) : RecyclerView.Adapter<LabAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lab = listLab[position]
        holder.binding.tvNamaLabItem.text = lab.nama
        holder.binding.tvWaktuItem.text = lab.lokasi
        holder.itemView.setOnClickListener { onItemClick(lab) }
    }

    override fun getItemCount() = listLab.size
}