package com.studymate.sm.cid.ui.ujian

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
import com.studymate.sm.cid.data.entity.Pelajaran
import com.studymate.sm.cid.data.entity.Ujian
import com.studymate.sm.cid.databinding.DialogUjianBinding
import com.studymate.sm.cid.databinding.FragmentGenericListBinding
import com.studymate.sm.cid.ui.common.GenericListAdapter
import com.studymate.sm.cid.ui.common.SpinnerUtil
import com.studymate.sm.cid.util.DateUtil
import kotlinx.coroutines.launch
import java.util.Calendar

class UjianFragment : Fragment() {

    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { (requireActivity().application as StudyMateApp).repository }
    private lateinit var adapter: GenericListAdapter<Ujian>
    private var daftarPelajaran: List<Pelajaran> = emptyList()
    private val jenisUjian = listOf("Ulangan Harian", "Tugas Praktik", "UTS", "UAS", "Remedial", "Lainnya")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarInclude.toolbar.title = "Ujian & Ulangan"
        binding.toolbarInclude.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarInclude.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        adapter = GenericListAdapter(
            onClick = { item -> tampilkanDialog(item) },
            onDelete = { item -> viewLifecycleOwner.lifecycleScope.launch { repository.deleteUjian(item) } }
        ) { item, itemBinding ->
            itemBinding.textTitle.text = item.nama
            val namaPelajaran = daftarPelajaran.find { it.id == item.pelajaranId }?.nama ?: "Umum"
            itemBinding.textSubtitle.text = "$namaPelajaran • ${item.jenis}"
            itemBinding.textMeta.visibility = View.VISIBLE
            itemBinding.textMeta.text = DateUtil.formatTanggal(item.tanggal)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { tampilkanDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    repository.observePelajaran().collect { daftarPelajaran = it }
                }
                repository.observeUjian().collect { list ->
                    adapter.submitList(list)
                    binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun tampilkanDialog(existing: Ujian?) {
        val dialogBinding = DialogUjianBinding.inflate(layoutInflater)
        val opsiPelajaran = listOf<Pelajaran?>(null) + daftarPelajaran
        SpinnerUtil.isi(requireContext(), dialogBinding.spinnerPelajaran, opsiPelajaran) { it?.nama ?: "Umum (tanpa pelajaran)" }
        SpinnerUtil.isi(requireContext(), dialogBinding.spinnerJenis, jenisUjian) { it }

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
            dialogBinding.inputMateri.setText(it.materi)
            dialogBinding.inputCatatan.setText(it.catatan)
            val idxPelajaran = opsiPelajaran.indexOfFirst { p -> p?.id == it.pelajaranId }
            if (idxPelajaran >= 0) dialogBinding.spinnerPelajaran.setSelection(idxPelajaran)
            val idxJenis = jenisUjian.indexOf(it.jenis)
            if (idxJenis >= 0) dialogBinding.spinnerJenis.setSelection(idxJenis)
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Tambah Ujian" else "Edit Ujian")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = dialogBinding.inputNama.text?.toString()?.trim().orEmpty()
                if (nama.isEmpty()) return@setPositiveButton
                val pelajaranTerpilih = opsiPelajaran.getOrNull(dialogBinding.spinnerPelajaran.selectedItemPosition)
                val jenis = jenisUjian.getOrNull(dialogBinding.spinnerJenis.selectedItemPosition) ?: jenisUjian[0]
                val item = Ujian(
                    id = existing?.id ?: 0,
                    nama = nama,
                    pelajaranId = pelajaranTerpilih?.id,
                    jenis = jenis,
                    tanggal = tanggalTerpilih,
                    materi = dialogBinding.inputMateri.text?.toString().orEmpty(),
                    catatan = dialogBinding.inputCatatan.text?.toString().orEmpty()
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    if (existing == null) repository.saveUjian(item) else repository.updateUjian(item)
                }
            }
            .setNegativeButton("Batal", null)

        if (existing != null) {
            builder.setNeutralButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch { repository.deleteUjian(existing) }
            }
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
