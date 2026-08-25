package com.studymate.sm.cid.ui.tugas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studymate.sm.cid.data.entity.Tugas
import com.studymate.sm.cid.databinding.ItemTugasCardBinding

class TugasAdapter(
    private val onClick: (Tugas) -> Unit,
    private val onDelete: (Tugas) -> Unit,
    private val onToggleSelesai: (Tugas, Boolean) -> Unit,
    private val binder: (Tugas, ItemTugasCardBinding) -> Unit
) : RecyclerView.Adapter<TugasAdapter.ViewHolder>() {

    private val items = mutableListOf<Tugas>()

    fun submitList(newItems: List<Tugas>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTugasCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.checkboxSelesai.setOnCheckedChangeListener(null)
        holder.binding.checkboxSelesai.isChecked = item.selesai
        binder(item, holder.binding)
        holder.binding.rootClickable.setOnClickListener { onClick(item) }
        holder.binding.btnDelete.setOnClickListener { onDelete(item) }
        holder.binding.checkboxSelesai.setOnCheckedChangeListener { _, isChecked ->
            onToggleSelesai(item, isChecked)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val binding: ItemTugasCardBinding) : RecyclerView.ViewHolder(binding.root)
}
