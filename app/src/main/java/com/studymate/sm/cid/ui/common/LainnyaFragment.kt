package com.studymate.sm.cid.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.studymate.sm.cid.R
import com.studymate.sm.cid.databinding.FragmentLainnyaBinding
import com.studymate.sm.cid.databinding.ItemLainnyaMenuBinding

data class MenuLainnya(val label: String, val iconRes: Int, val actionId: Int)

class LainnyaFragment : Fragment() {

    private var _binding: FragmentLainnyaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLainnyaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarInclude.toolbar.title = "Lainnya"

        val menuList = listOf(
            MenuLainnya("Ujian & Ulangan", R.drawable.ic_exam, R.id.action_to_ujian2),
            MenuLainnya("Catatan Materi", R.drawable.ic_note, R.id.action_to_materi),
            MenuLainnya("Kalender Akademik", R.drawable.ic_calendar, R.id.action_to_kalender),
            MenuLainnya("Target Belajar", R.drawable.ic_target, R.id.action_to_target2),
            MenuLainnya("Keuangan Sekolah", R.drawable.ic_wallet, R.id.action_to_keuangan2),
            MenuLainnya("Kategori Pelajaran", R.drawable.ic_more, R.id.action_to_kategori),
            MenuLainnya("AI Asisten Belajar", R.drawable.ic_ai, R.id.action_to_ai),
            MenuLainnya("Pengaturan", R.drawable.ic_settings, R.id.action_to_pengaturan),
            MenuLainnya("Info Developer", R.drawable.ic_info, R.id.action_to_info)
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = object : RecyclerView.Adapter<MenuViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
                val itemBinding = ItemLainnyaMenuBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return MenuViewHolder(itemBinding)
            }

            override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
                val menu = menuList[position]
                holder.binding.textLabel.text = menu.label
                holder.binding.iconMenu.setImageResource(menu.iconRes)
                holder.binding.rootMenuItem.setOnClickListener {
                    findNavController().navigate(menu.actionId)
                }
            }

            override fun getItemCount(): Int = menuList.size
        }
    }

    class MenuViewHolder(val binding: ItemLainnyaMenuBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
