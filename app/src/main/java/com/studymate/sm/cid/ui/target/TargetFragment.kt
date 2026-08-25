package com.studymate.sm.cid.ui.target

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.studymate.sm.cid.R
import com.studymate.sm.cid.StudyMateApp
import com.studymate.sm.cid.data.entity.TargetBelajar
import com.studymate.sm.cid.databinding.DialogTargetBinding
import com.studymate.sm.cid.databinding.FragmentGenericListBinding
import com.studymate.sm.cid.ui.common.GenericListAdapter
import kotlinx.coroutines.launch

class TargetFragment : Fragment() {

    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { (requireActivity().application as StudyMateApp).repository }
    private lateinit var adapter: GenericListAdapter<TargetBelajar>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarInclude.toolbar.title = "Target Belajar"
        binding.toolbarInclude.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarInclude.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        adapter = GenericListAdapter(
            onClick = { item -> tampilkanDialog(item) },
            onDelete = { item -> viewLifecycleOwner.lifecycleScope.launch { repository.deleteTarget(item) } }
        ) { item, itemBinding ->
            itemBinding.textTitle.text = item.nama
            itemBinding.textSubtitle.text = if (item.deskripsi.isNotBlank()) item.deskripsi else "Target: ${item.targetJumlah}"
            itemBinding.textMeta.visibility = View.VISIBLE
            val persen = if (item.targetJumlah > 0) (item.progress * 100 / item.targetJumlah).coerceIn(0, 100) else 0
            itemBinding.textMeta.text = if (item.selesai) "Selesai ✔" else "Progress: ${item.progress}/${item.targetJumlah} ($persen%)"
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { tampilkanDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.observeTarget().collect { list ->
                    adapter.submitList(list)
                    binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun tampilkanDialog(existing: TargetBelajar?) {
        val dialogBinding = DialogTargetBinding.inflate(layoutInflater)
        existing?.let {
            dialogBinding.inputNama.setText(it.nama)
            dialogBinding.inputDeskripsi.setText(it.deskripsi)
            dialogBinding.inputTarget.setText(it.targetJumlah.toString())
            dialogBinding.inputProgress.setText(it.progress.toString())
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Tambah Target Belajar" else "Edit Target Belajar")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = dialogBinding.inputNama.text?.toString()?.trim().orEmpty()
                if (nama.isEmpty()) return@setPositiveButton
                val target = dialogBinding.inputTarget.text?.toString()?.toIntOrNull() ?: 100
                val progress = dialogBinding.inputProgress.text?.toString()?.toIntOrNull() ?: 0
                val item = TargetBelajar(
                    id = existing?.id ?: 0,
                    nama = nama,
                    deskripsi = dialogBinding.inputDeskripsi.text?.toString().orEmpty(),
                    targetJumlah = target,
                    progress = progress,
                    selesai = progress >= target
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    if (existing == null) repository.saveTarget(item) else repository.updateTarget(item)
                }
            }
            .setNegativeButton("Batal", null)

        if (existing != null) {
            builder.setNeutralButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch { repository.deleteTarget(existing) }
            }
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
