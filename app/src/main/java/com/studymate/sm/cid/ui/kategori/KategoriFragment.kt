package com.studymate.sm.cid.ui.kategori

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
import com.studymate.sm.cid.StudyMateApp
import com.studymate.sm.cid.data.entity.Kategori
import com.studymate.sm.cid.databinding.DialogKategoriBinding
import com.studymate.sm.cid.databinding.FragmentGenericListBinding
import com.studymate.sm.cid.ui.common.GenericListAdapter
import kotlinx.coroutines.launch

class KategoriFragment : Fragment() {

    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { (requireActivity().application as StudyMateApp).repository }
    private lateinit var adapter: GenericListAdapter<Kategori>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarInclude.toolbar.title = "Kategori Pelajaran"
        binding.toolbarInclude.toolbar.setNavigationIcon(com.studymate.sm.cid.R.drawable.ic_back)
        binding.toolbarInclude.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        adapter = GenericListAdapter(
            onDelete = { item ->
                viewLifecycleOwner.lifecycleScope.launch { repository.deleteKategori(item) }
            }
        ) { item, itemBinding ->
            itemBinding.textTitle.text = item.nama
            itemBinding.textSubtitle.visibility = View.GONE
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.fabAdd.setOnClickListener { tampilkanDialogTambah() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.observeKategori().collect { list ->
                    adapter.submitList(list)
                    binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun tampilkanDialogTambah() {
        val dialogBinding = DialogKategoriBinding.inflate(layoutInflater)
        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Kategori")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = dialogBinding.inputNama.text?.toString()?.trim().orEmpty()
                if (nama.isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        repository.saveKategori(Kategori(nama = nama))
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
