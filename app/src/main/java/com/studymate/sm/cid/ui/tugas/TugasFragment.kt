package com.studymate.sm.cid.ui.tugas

import android.app.DatePickerDialog
import android.graphics.Color
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
import com.studymate.sm.cid.data.entity.Pelajaran
import com.studymate.sm.cid.data.entity.Tugas
import com.studymate.sm.cid.databinding.DialogTugasBinding
import com.studymate.sm.cid.databinding.FragmentGenericListBinding
import com.studymate.sm.cid.ui.common.SpinnerUtil
import com.studymate.sm.cid.util.DateUtil
import kotlinx.coroutines.launch
import java.util.Calendar

class TugasFragment : Fragment() {

    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { (requireActivity().application as StudyMateApp).repository }
    private lateinit var adapter: TugasAdapter
    private var daftarPelajaran: List<Pelajaran> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarInclude.toolbar.title = "Tugas & PR"

        adapter = TugasAdapter(
            onClick = { item -> tampilkanDialog(item) },
            onDelete = { item -> viewLifecycleOwner.lifecycleScope.launch { repository.deleteTugas(item) } },
            onToggleSelesai = { item, checked ->
                viewLifecycleOwner.lifecycleScope.launch { repository.updateTugas(item.copy(selesai = checked)) }
            }
        ) { item, itemBinding ->
            itemBinding.textJudul.text = item.judul
            itemBinding.textJudul.paintFlags = if (item.selesai) {
                itemBinding.textJudul.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                itemBinding.textJudul.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            itemBinding.textPelajaran.text = daftarPelajaran.find { it.id == item.pelajaranId }?.nama ?: "Umum"
            if (item.deadline != null) {
                itemBinding.textDeadline.visibility = View.VISIBLE
                itemBinding.textDeadline.text = "Deadline: ${DateUtil.formatTanggal(item.deadline)}"
                val terlambat = !item.selesai && item.deadline < System.currentTimeMillis()
                itemBinding.textDeadline.setTextColor(
                    if (terlambat) Color.parseColor("#C0392B") else Color.parseColor("#1E7A4C")
                )
            } else {
                itemBinding.textDeadline.visibility = View.GONE
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { tampilkanDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { repository.observePelajaran().collect { daftarPelajaran = it } }
                repository.observeTugas().collect { list ->
                    adapter.submitList(list)
                    binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun tampilkanDialog(existing: Tugas?) {
        val dialogBinding = DialogTugasBinding.inflate(layoutInflater)
        val opsiPelajaran = listOf<Pelajaran?>(null) + daftarPelajaran
        SpinnerUtil.isi(requireContext(), dialogBinding.spinnerPelajaran, opsiPelajaran) { it?.nama ?: "Umum (tanpa pelajaran)" }

        var deadlineTerpilih = existing?.deadline ?: System.currentTimeMillis()
        dialogBinding.inputDeadline.setText(DateUtil.formatTanggal(deadlineTerpilih))
        dialogBinding.inputDeadline.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = deadlineTerpilih }
            DatePickerDialog(requireContext(), { _, y, m, d ->
                cal.set(y, m, d)
                deadlineTerpilih = cal.timeInMillis
                dialogBinding.inputDeadline.setText(DateUtil.formatTanggal(deadlineTerpilih))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        existing?.let {
            dialogBinding.inputJudul.setText(it.judul)
            dialogBinding.inputDeskripsi.setText(it.deskripsi)
            dialogBinding.inputCatatan.setText(it.catatan)
            val idx = opsiPelajaran.indexOfFirst { p -> p?.id == it.pelajaranId }
            if (idx >= 0) dialogBinding.spinnerPelajaran.setSelection(idx)
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Tambah Tugas" else "Edit Tugas")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val judul = dialogBinding.inputJudul.text?.toString()?.trim().orEmpty()
                if (judul.isEmpty()) return@setPositiveButton
                val pelajaranTerpilih = opsiPelajaran.getOrNull(dialogBinding.spinnerPelajaran.selectedItemPosition)
                val item = Tugas(
                    id = existing?.id ?: 0,
                    judul = judul,
                    pelajaranId = pelajaranTerpilih?.id,
                    deskripsi = dialogBinding.inputDeskripsi.text?.toString().orEmpty(),
                    tanggalDibuat = existing?.tanggalDibuat ?: System.currentTimeMillis(),
                    deadline = deadlineTerpilih,
                    selesai = existing?.selesai ?: false,
                    catatan = dialogBinding.inputCatatan.text?.toString().orEmpty()
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    if (existing == null) repository.saveTugas(item) else repository.updateTugas(item)
                }
            }
            .setNegativeButton("Batal", null)

        if (existing != null) {
            builder.setNeutralButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch { repository.deleteTugas(existing) }
            }
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
