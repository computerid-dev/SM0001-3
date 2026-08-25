package com.studymate.sm.cid.ui.pelajaran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.studymate.sm.cid.R
import com.studymate.sm.cid.StudyMateApp
import com.studymate.sm.cid.data.entity.Kategori
import com.studymate.sm.cid.data.entity.Pelajaran
import com.studymate.sm.cid.databinding.DialogPelajaranBinding
import com.studymate.sm.cid.databinding.FragmentPelajaranDetailBinding
import com.studymate.sm.cid.ui.common.SpinnerUtil
import kotlinx.coroutines.launch

class PelajaranDetailFragment : Fragment() {

    private var _binding: FragmentPelajaranDetailBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { (requireActivity().application as StudyMateApp).repository }
    private var pelajaran: Pelajaran? = null
    private var daftarKategori: List<Kategori> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPelajaranDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pelajaranId = arguments?.getLong("pelajaranId") ?: 0L

        binding.toolbarInclude.toolbar.title = "Detail Pelajaran"
        binding.toolbarInclude.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarInclude.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnEdit.setOnClickListener { pelajaran?.let { tampilkanDialogEdit(it) } }
        binding.btnHapus.setOnClickListener { konfirmasiHapus() }

        viewLifecycleOwner.lifecycleScope.launch {
            daftarKategori = repository.getKategoriList()
            muatData(pelajaranId)
        }
    }

    private suspend fun muatData(pelajaranId: Long) {
        val item = repository.getPelajaranById(pelajaranId) ?: return
        pelajaran = item
        binding.textNama.text = item.nama
        binding.textGuru.text = if (item.guru.isNotBlank()) "Pengajar: ${item.guru}" else "Pengajar belum diisi"
        val namaKategori = daftarKategori.find { it.id == item.kategoriId }?.nama
        binding.textKategori.text = "Kategori: ${namaKategori ?: "-"}"
        binding.textCatatan.text = item.catatan.ifBlank { "Belum ada catatan." }

        val jumlahJadwal = repository.getJadwalList().count { it.pelajaranId == item.id }
        val jumlahTugas = repository.getTugasList().count { it.pelajaranId == item.id }
        val jumlahUjian = repository.getUjianList().count { it.pelajaranId == item.id }
        val jumlahMateri = repository.getMateriList().count { it.pelajaranId == item.id }
        binding.textRingkasan.text =
            "$jumlahJadwal Jadwal • $jumlahTugas Tugas • $jumlahUjian Ujian • $jumlahMateri Catatan Materi\n\nKelola detailnya lewat menu Jadwal, Tugas, Ujian, dan Materi."
    }

    private fun tampilkanDialogEdit(item: Pelajaran) {
        val dialogBinding = DialogPelajaranBinding.inflate(layoutInflater)
        val opsiKategori = listOf<Kategori?>(null) + daftarKategori
        SpinnerUtil.isi(requireContext(), dialogBinding.spinnerKategori, opsiKategori) { it?.nama ?: "Tanpa kategori" }
        dialogBinding.inputNama.setText(item.nama)
        dialogBinding.inputGuru.setText(item.guru)
        dialogBinding.inputCatatan.setText(item.catatan)
        val idx = opsiKategori.indexOfFirst { it?.id == item.kategoriId }
        if (idx >= 0) dialogBinding.spinnerKategori.setSelection(idx)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Pelajaran")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = dialogBinding.inputNama.text?.toString()?.trim().orEmpty()
                if (nama.isEmpty()) return@setPositiveButton
                val kategoriTerpilih = opsiKategori.getOrNull(dialogBinding.spinnerKategori.selectedItemPosition)
                val updated = item.copy(
                    nama = nama,
                    guru = dialogBinding.inputGuru.text?.toString().orEmpty(),
                    kategoriId = kategoriTerpilih?.id,
                    catatan = dialogBinding.inputCatatan.text?.toString().orEmpty()
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.updatePelajaran(updated)
                    muatData(updated.id)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun konfirmasiHapus() {
        val item = pelajaran ?: return
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Pelajaran")
            .setMessage("Yakin ingin menghapus \"${item.nama}\"? Data jadwal/tugas/ujian terkait tidak otomatis terhapus.")
            .setPositiveButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.deletePelajaran(item)
                    requireActivity().onBackPressedDispatcher.onBackPressed()
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
