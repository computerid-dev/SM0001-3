package com.studymate.sm.cid.ui.materi

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
import com.studymate.sm.cid.data.entity.CatatanMateri
import com.studymate.sm.cid.data.entity.Pelajaran
import com.studymate.sm.cid.databinding.DialogMateriBinding
import com.studymate.sm.cid.databinding.FragmentGenericListBinding
import com.studymate.sm.cid.ui.common.GenericListAdapter
import com.studymate.sm.cid.ui.common.SpinnerUtil
import com.studymate.sm.cid.util.DateUtil
import kotlinx.coroutines.launch

class MateriFragment : Fragment() {

    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { (requireActivity().application as StudyMateApp).repository }
    private lateinit var adapter: GenericListAdapter<CatatanMateri>
    private var daftarPelajaran: List<Pelajaran> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarInclude.toolbar.title = "Catatan Materi"
        binding.toolbarInclude.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarInclude.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        adapter = GenericListAdapter(
            onClick = { item -> tampilkanDialog(item) },
            onDelete = { item -> viewLifecycleOwner.lifecycleScope.launch { repository.deleteMateri(item) } }
        ) { item, itemBinding ->
            itemBinding.textTitle.text = item.judul
            val namaPelajaran = daftarPelajaran.find { it.id == item.pelajaranId }?.nama ?: "Umum"
            itemBinding.textSubtitle.text = if (item.bab.isNotBlank()) "$namaPelajaran • Bab ${item.bab}" else namaPelajaran
            itemBinding.textMeta.visibility = View.VISIBLE
            itemBinding.textMeta.text = DateUtil.formatTanggal(item.tanggalDibuat)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { tampilkanDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { repository.observePelajaran().collect { daftarPelajaran = it } }
                repository.observeMateri().collect { list ->
                    adapter.submitList(list)
                    binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun tampilkanDialog(existing: CatatanMateri?) {
        val dialogBinding = DialogMateriBinding.inflate(layoutInflater)
        val opsiPelajaran = listOf<Pelajaran?>(null) + daftarPelajaran
        SpinnerUtil.isi(requireContext(), dialogBinding.spinnerPelajaran, opsiPelajaran) { it?.nama ?: "Umum (tanpa pelajaran)" }

        existing?.let {
            dialogBinding.inputJudul.setText(it.judul)
            dialogBinding.inputBab.setText(it.bab)
            dialogBinding.inputHalaman.setText(it.halaman)
            dialogBinding.inputIsi.setText(it.isi)
            dialogBinding.inputCatatan.setText(it.catatan)
            val idx = opsiPelajaran.indexOfFirst { p -> p?.id == it.pelajaranId }
            if (idx >= 0) dialogBinding.spinnerPelajaran.setSelection(idx)
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Tambah Catatan Materi" else "Edit Catatan Materi")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val judul = dialogBinding.inputJudul.text?.toString()?.trim().orEmpty()
                if (judul.isEmpty()) return@setPositiveButton
                val pelajaranTerpilih = opsiPelajaran.getOrNull(dialogBinding.spinnerPelajaran.selectedItemPosition)
                val item = CatatanMateri(
                    id = existing?.id ?: 0,
                    judul = judul,
                    pelajaranId = pelajaranTerpilih?.id,
                    bab = dialogBinding.inputBab.text?.toString().orEmpty(),
                    halaman = dialogBinding.inputHalaman.text?.toString().orEmpty(),
                    isi = dialogBinding.inputIsi.text?.toString().orEmpty(),
                    catatan = dialogBinding.inputCatatan.text?.toString().orEmpty(),
                    tanggalDibuat = existing?.tanggalDibuat ?: System.currentTimeMillis()
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    if (existing == null) repository.saveMateri(item) else repository.updateMateri(item)
                }
            }
            .setNegativeButton("Batal", null)

        if (existing != null) {
            builder.setNeutralButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch { repository.deleteMateri(existing) }
            }
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
