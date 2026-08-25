package com.studymate.sm.cid.ui.jadwal

import android.app.TimePickerDialog
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
import com.studymate.sm.cid.data.entity.JadwalItem
import com.studymate.sm.cid.data.entity.Pelajaran
import com.studymate.sm.cid.databinding.DialogJadwalBinding
import com.studymate.sm.cid.databinding.FragmentGenericListBinding
import com.studymate.sm.cid.ui.common.GenericListAdapter
import com.studymate.sm.cid.ui.common.SpinnerUtil
import com.studymate.sm.cid.util.DateUtil
import kotlinx.coroutines.launch
import java.util.Calendar

class JadwalFragment : Fragment() {

    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { (requireActivity().application as StudyMateApp).repository }
    private lateinit var adapter: GenericListAdapter<JadwalItem>
    private var daftarPelajaran: List<Pelajaran> = emptyList()
    private val hariUrutan = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarInclude.toolbar.title = "Jadwal Pelajaran"

        adapter = GenericListAdapter(
            onClick = { item -> tampilkanDialog(item) },
            onDelete = { item -> viewLifecycleOwner.lifecycleScope.launch { repository.deleteJadwal(item) } }
        ) { item, itemBinding ->
            val namaPelajaran = daftarPelajaran.find { it.id == item.pelajaranId }?.nama ?: "-"
            itemBinding.textTitle.text = namaPelajaran
            itemBinding.textSubtitle.text = "${item.hari} • ${item.jamMulai} - ${item.jamSelesai}"
            if (item.catatan.isNotBlank()) {
                itemBinding.textMeta.visibility = View.VISIBLE
                itemBinding.textMeta.text = item.catatan
            } else {
                itemBinding.textMeta.visibility = View.GONE
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener {
            if (daftarPelajaran.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Tambahkan pelajaran terlebih dahulu", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                tampilkanDialog(null)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { repository.observePelajaran().collect { daftarPelajaran = it } }
                repository.observeJadwal().collect { list ->
                    val sorted = list.sortedBy { hariUrutan.indexOf(it.hari).let { i -> if (i < 0) 99 else i } }
                    adapter.submitList(sorted)
                    binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun tampilkanDialog(existing: JadwalItem?) {
        val dialogBinding = DialogJadwalBinding.inflate(layoutInflater)
        SpinnerUtil.isi(requireContext(), dialogBinding.spinnerPelajaran, daftarPelajaran) { it.nama }
        SpinnerUtil.isi(requireContext(), dialogBinding.spinnerHari, hariUrutan) { it }

        var jamMulai = existing?.jamMulai ?: "07.00"
        var jamSelesai = existing?.jamSelesai ?: "08.30"
        dialogBinding.inputJamMulai.setText(jamMulai)
        dialogBinding.inputJamSelesai.setText(jamSelesai)

        dialogBinding.inputJamMulai.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, h, m ->
                jamMulai = String.format("%02d.%02d", h, m)
                dialogBinding.inputJamMulai.setText(jamMulai)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        dialogBinding.inputJamSelesai.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, h, m ->
                jamSelesai = String.format("%02d.%02d", h, m)
                dialogBinding.inputJamSelesai.setText(jamSelesai)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        existing?.let {
            dialogBinding.inputCatatan.setText(it.catatan)
            val idxPelajaran = daftarPelajaran.indexOfFirst { p -> p.id == it.pelajaranId }
            if (idxPelajaran >= 0) dialogBinding.spinnerPelajaran.setSelection(idxPelajaran)
            val idxHari = hariUrutan.indexOf(it.hari)
            if (idxHari >= 0) dialogBinding.spinnerHari.setSelection(idxHari)
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Tambah Jadwal" else "Edit Jadwal")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val pelajaranTerpilih = daftarPelajaran.getOrNull(dialogBinding.spinnerPelajaran.selectedItemPosition)
                    ?: return@setPositiveButton
                val item = JadwalItem(
                    id = existing?.id ?: 0,
                    pelajaranId = pelajaranTerpilih.id,
                    hari = hariUrutan.getOrNull(dialogBinding.spinnerHari.selectedItemPosition) ?: hariUrutan[0],
                    jamMulai = jamMulai,
                    jamSelesai = jamSelesai,
                    catatan = dialogBinding.inputCatatan.text?.toString().orEmpty()
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    if (existing == null) repository.saveJadwal(item) else repository.updateJadwal(item)
                }
            }
            .setNegativeButton("Batal", null)

        if (existing != null) {
            builder.setNeutralButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch { repository.deleteJadwal(existing) }
            }
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
