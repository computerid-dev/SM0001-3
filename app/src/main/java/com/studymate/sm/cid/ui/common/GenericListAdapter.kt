package com.studymate.sm.cid.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studymate.sm.cid.databinding.ItemGenericCardBinding

/**
 * Adapter RecyclerView generik untuk daftar sederhana (Kategori, Ujian, Jadwal,
 * Catatan Materi, Kalender, Target Belajar, Keuangan). Tampilan setiap item
 * (judul/subjudul/meta) diatur lewat lambda [binder] agar satu adapter bisa
 * dipakai ulang di banyak fragment tanpa duplikasi kode.
 */
class GenericListAdapter<T : Any>(
    private val onClick: (T) -> Unit = {},
    private val onDelete: (T) -> Unit = {},
    private val binder: (item: T, binding: ItemGenericCardBinding) -> Unit
) : RecyclerView.Adapter<GenericListAdapter.ViewHolder>() {

    private val items = mutableListOf<T>()

    fun submitList(newItems: List<T>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGenericCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        binder(item, holder.binding)
        holder.binding.rootClickable.setOnClickListener { onClick(item) }
        holder.binding.btnDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val binding: ItemGenericCardBinding) : RecyclerView.ViewHolder(binding.root)
}
