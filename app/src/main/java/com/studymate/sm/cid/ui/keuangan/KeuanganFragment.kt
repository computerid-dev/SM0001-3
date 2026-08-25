package com.studymate.sm.cid.ui.keuangan

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
import com.studymate.sm.cid.data.entity.KeuanganEntry
import com.studymate.sm.cid.databinding.DialogKeuanganBinding
import com.studymate.sm.cid.databinding.FragmentGenericListBinding
import com.studymate.sm.cid.ui.common.GenericListAdapter
import com.studymate.sm.cid.util.DateUtil
import kotlinx.coroutines.launch
import java.util.Calendar

class KeuanganFragment : Fragment() {

    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { (requireActivity().application as StudyMateApp).repository }
    private lateinit var adapter: GenericListAdapter<KeuanganEntry>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarInclude.toolbar.title = "Keuangan Sekolah"
        binding.toolbarInclude.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarInclude.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        adapter = GenericListAdapter(
            onClick = { item -> tampilkanDialog(item) },
            onDelete = { item -> viewLifecycleOwner.lifecycleScope.launch { repository.deleteKeuangan(item) } }
        ) { item, itemBinding ->
            val sisa = item.uangSangu - item.pengeluaran
            itemBinding.textTitle.text = DateUtil.formatTanggal(item.tanggal)
            itemBinding.textSubtitle.text = "Sangu ${DateUtil.formatRupiah(item.uangSangu)} • Keluar ${DateUtil.formatRupiah(item.pengeluaran)}"
            if (item.keterangan.isNotBlank()) {
                itemBinding.textMeta.visibility = View.VISIBLE
                itemBinding.textMeta.text = "${item.keterangan} • Sisa ${DateUtil.formatRupiah(sisa)}"
            } else {
                itemBinding.textMeta.visibility = View.VISIBLE
                itemBinding.textMeta.text = "Sisa ${DateUtil.formatRupiah(sisa)}"
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { tampilkanDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.observeKeuangan().collect { list ->
                    adapter.submitList(list)
                    binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun tampilkanDialog(existing: KeuanganEntry?) {
        val dialogBinding = DialogKeuanganBinding.inflate(layoutInflater)
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
            dialogBinding.inputSangu.setText(it.uangSangu.toString())
            dialogBinding.inputPengeluaran.setText(it.pengeluaran.toString())
            dialogBinding.inputKeterangan.setText(it.keterangan)
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "Tambah Catatan Keuangan" else "Edit Catatan Keuangan")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val sangu = dialogBinding.inputSangu.text?.toString()?.toLongOrNull() ?: 0
                val pengeluaran = dialogBinding.inputPengeluaran.text?.toString()?.toLongOrNull() ?: 0
                val item = KeuanganEntry(
                    id = existing?.id ?: 0,
                    tanggal = tanggalTerpilih,
                    uangSangu = sangu,
                    pengeluaran = pengeluaran,
                    keterangan = dialogBinding.inputKeterangan.text?.toString().orEmpty()
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    if (existing == null) repository.saveKeuangan(item) else repository.updateKeuangan(item)
                }
            }
            .setNegativeButton("Batal", null)

        if (existing != null) {
            builder.setNeutralButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch { repository.deleteKeuangan(existing) }
            }
        }
        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
