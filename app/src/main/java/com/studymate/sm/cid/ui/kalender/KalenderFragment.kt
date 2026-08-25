package com.studymate.sm.cid.ui.kalender

import android.app.DatePickerDialog
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
import com.studymate.sm.cid.data.entity.KalenderEvent
import com.studymate.sm.cid.databinding.DialogKalenderBinding
import com.studymate.sm.cid.databinding.FragmentGenericListBinding
import com.studymate.sm.cid.ui.common.GenericListAdapter
import com.studymate.sm.cid.ui.common.SpinnerUtil
import com.studymate.sm.cid.util.DateUtil
import kotlinx.coroutines.launch
import java.util.Calendar

class KalenderFragment : Fragment() {

    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { (requireActivity().application as StudyMateApp).repository }
    private lateinit var adapter: GenericListAdapter<KalenderEvent>
    private val kategoriList = listOf("Ujian", "Libur", "Acara Sekolah", "Deadline", "Kegiatan", "Lainnya")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarInclude.toolbar.title = "Kalender Akademik"
        binding.toolbarInclude.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarInclude.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        adapter = GenericListAdapter(
            onClick = { item -> tampilkanDialog(item) },
            onDelete = { item -> viewLifecycleOwner.lifecycleScope.launch { repository.deleteKalender(item) } }
        ) { item, itemBinding ->
            itemBinding.textTitle.text = item.nama
            itemBinding.textSubtitle.text = item.kategori
            itemBinding.textMeta.visibility = View.VISIBLE
            itemBinding.textMeta.text = DateUtil.formatTanggal(item.tanggal)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { tampilkanDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.observeKalender().collect { list ->
                    adapter.submitList(list)
                    binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun tampilkanDialog(existing: KalenderEvent?) {
        val dialogBinding = DialogKalenderBinding.inflate(layoutInflater)
        SpinnerUtil.isi(requireContext(), dialogBinding.spinnerKategori, kategoriList) { it }

        var tanggalTerpilih = existing?.tanggal ?: System.currentTimeMillis()
        dialogBinding.inputTanggal.setText(DateUtil.formatTanggal(tanggalTerpilih))
        dialogBinding.inputTanggal.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = tanggalTerpilih }
            DatePickerDialog(requireContext(), { _, y, m, d ->
                cal.set(y, m, d)
                tanggalTerpilih = cal.timeInMillis
                dialogBinding.inputTanggal.setText(DateUtil.formatTanggal(tanggalTerpilih))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        existing?.let {
            dialogBinding.inputNama.setText(it.nama)
            dialogBinding.inputDeskripsi.setText(it.deskripsi)
            val idx = kategoriList.indexOf(it.kategori)
            if (idx >= 0) dialogBinding.spinnerKategori.setSelection(idx)
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Tambah Kegiatan" else "Edit Kegiatan")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = dialogBinding.inputNama.text?.toString()?.trim().orEmpty()
                if (nama.isEmpty()) return@setPositiveButton
                val item = KalenderEvent(
                    id = existing?.id ?: 0,
                    nama = nama,
                    tanggal = tanggalTerpilih,
                    deskripsi = dialogBinding.inputDeskripsi.text?.toString().orEmpty(),
                    kategori = kategoriList.getOrNull(dialogBinding.spinnerKategori.selectedItemPosition) ?: kategoriList[0]
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    if (existing == null) repository.saveKalender(item) else repository.updateKalender(item)
                }
            }
            .setNegativeButton("Batal", null)

        if (existing != null) {
            builder.setNeutralButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch { repository.deleteKalender(existing) }
            }
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
