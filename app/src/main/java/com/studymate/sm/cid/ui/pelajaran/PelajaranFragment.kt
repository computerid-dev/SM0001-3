package com.studymate.sm.cid.ui.pelajaran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.studymate.sm.cid.R
import com.studymate.sm.cid.StudyMateApp
import com.studymate.sm.cid.data.entity.Kategori
import com.studymate.sm.cid.data.entity.Pelajaran
import com.studymate.sm.cid.databinding.DialogPelajaranBinding
import com.studymate.sm.cid.databinding.FragmentPelajaranBinding
import com.studymate.sm.cid.ui.common.SpinnerUtil
import kotlinx.coroutines.launch

class PelajaranFragment : Fragment() {

    private var _binding: FragmentPelajaranBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { (requireActivity().application as StudyMateApp).repository }
    private lateinit var adapter: PelajaranAdapter
    private var daftarKategori: List<Kategori> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPelajaranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarInclude.toolbar.title = "Daftar Pelajaran"

        adapter = PelajaranAdapter(
            onClick = { item ->
                findNavController().navigate(
                    R.id.action_pelajaran_to_detail,
                    bundleOf("pelajaranId" to item.id)
                )
            },
            onDelete = { item ->
                viewLifecycleOwner.lifecycleScope.launch { repository.deletePelajaran(item) }
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { tampilkanDialogTambah() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { repository.observeKategori().collect { daftarKategori = it } }
                repository.observePelajaran().collect { list ->
                    adapter.submitList(list, daftarKategori)
                    binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun tampilkanDialogTambah() {
        val dialogBinding = DialogPelajaranBinding.inflate(layoutInflater)
        val opsiKategori = listOf<Kategori?>(null) + daftarKategori
        SpinnerUtil.isi(requireContext(), dialogBinding.spinnerKategori, opsiKategori) { it?.nama ?: "Tanpa kategori" }

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Pelajaran")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = dialogBinding.inputNama.text?.toString()?.trim().orEmpty()
                if (nama.isEmpty()) return@setPositiveButton
                val kategoriTerpilih = opsiKategori.getOrNull(dialogBinding.spinnerKategori.selectedItemPosition)
                val item = Pelajaran(
                    nama = nama,
                    guru = dialogBinding.inputGuru.text?.toString().orEmpty(),
                    kategoriId = kategoriTerpilih?.id,
                    catatan = dialogBinding.inputCatatan.text?.toString().orEmpty()
                )
                viewLifecycleOwner.lifecycleScope.launch { repository.savePelajaran(item) }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
