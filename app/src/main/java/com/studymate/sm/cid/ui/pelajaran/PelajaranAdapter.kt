package com.studymate.sm.cid.ui.pelajaran

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studymate.sm.cid.data.entity.Kategori
import com.studymate.sm.cid.data.entity.Pelajaran
import com.studymate.sm.cid.databinding.ItemPelajaranCardBinding

class PelajaranAdapter(
    private val onClick: (Pelajaran) -> Unit,
    private val onDelete: (Pelajaran) -> Unit
) : RecyclerView.Adapter<PelajaranAdapter.ViewHolder>() {

    private val items = mutableListOf<Pelajaran>()
    private var kategoriMap: Map<Long, Kategori> = emptyMap()

    fun submitList(newItems: List<Pelajaran>, kategoriList: List<Kategori>) {
        items.clear()
        items.addAll(newItems)
        kategoriMap = kategoriList.associateBy { it.id }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPelajaranCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.textInitial.text = item.nama.take(1).uppercase()
        holder.binding.textNama.text = item.nama
        val namaKategori = kategoriMap[item.kategoriId]?.nama
        holder.binding.textGuruKategori.text = when {
            item.guru.isNotBlank() && namaKategori != null -> "${item.guru} • $namaKategori"
            item.guru.isNotBlank() -> item.guru
            namaKategori != null -> namaKategori
            else -> "Belum ada detail"
        }
        holder.binding.rootClickable.setOnClickListener { onClick(item) }
        holder.binding.btnDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val binding: ItemPelajaranCardBinding) : RecyclerView.ViewHolder(binding.root)
}
