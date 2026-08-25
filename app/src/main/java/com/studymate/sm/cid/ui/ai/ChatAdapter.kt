package com.studymate.sm.cid.ui.ai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studymate.sm.cid.databinding.ItemChatAiBinding
import com.studymate.sm.cid.databinding.ItemChatUserBinding

data class ChatMessage(val teks: String, val dariPengguna: Boolean)

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ChatMessage>()

    fun tambahPesan(pesan: ChatMessage) {
        items.add(pesan)
        notifyItemInserted(items.size - 1)
    }

    override fun getItemViewType(position: Int): Int = if (items[position].dariPengguna) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            UserViewHolder(ItemChatUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            AiViewHolder(ItemChatAiBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is UserViewHolder -> holder.binding.textPesan.text = item.teks
            is AiViewHolder -> holder.binding.textPesan.text = item.teks
        }
    }

    override fun getItemCount(): Int = items.size

    class UserViewHolder(val binding: ItemChatUserBinding) : RecyclerView.ViewHolder(binding.root)
    class AiViewHolder(val binding: ItemChatAiBinding) : RecyclerView.ViewHolder(binding.root)
}
