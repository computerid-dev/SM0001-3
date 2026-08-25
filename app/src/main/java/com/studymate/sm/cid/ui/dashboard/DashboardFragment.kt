package com.studymate.sm.cid.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.studymate.sm.cid.StudyMateApp
import com.studymate.sm.cid.databinding.FragmentDashboardBinding
import com.studymate.sm.cid.util.DateUtil
import com.studymate.sm.cid.util.PreferencesManager
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy { (requireActivity().application as StudyMateApp).repository }
    private lateinit var prefs: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PreferencesManager(requireContext())

        val nama = prefs.namaPengguna
        binding.textSapaan.text = if (nama.isNotBlank()) "Halo, $nama 👋" else "Halo, Sobat Pelajar 👋"

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { muatJadwalHariIni() }
                launch { muatRingkasanTugasUjian() }
            }
        }
    }

    private suspend fun muatJadwalHariIni() {
        repository.observeJadwal().collect { list ->
            if (_binding == null) return@collect
            val hariIni = DateUtil.namaHari[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]
            val jadwalHariIni = list.filter { it.hari.equals(hariIni, ignoreCase = true) }
                .sortedBy { it.jamMulai }
            val pelajaranMap = repository.getPelajaranList().associateBy { it.id }
            binding.textJadwalHariIni.text = if (jadwalHariIni.isEmpty()) {
                "Tidak ada jadwal untuk hari $hariIni."
            } else {
                jadwalHariIni.joinToString("\n") {
                    val nama = pelajaranMap[it.pelajaranId]?.nama ?: "Pelajaran"
                    "• ${it.jamMulai}-${it.jamSelesai}  $nama"
                }
            }
        }
    }

    private suspend fun muatRingkasanTugasUjian() {
        repository.observeTugas().collect { tugasList ->
            if (_binding == null) return@collect
            val aktif = tugasList.filter { !it.selesai }
            binding.textJumlahTugas.text = aktif.size.toString()
            val terdekat = aktif.sortedBy { it.deadline ?: Long.MAX_VALUE }.take(3)
            binding.textTugasTerdekat.text = if (terdekat.isEmpty()) {
                "Tidak ada tugas aktif. Kerja bagus!"
            } else {
                terdekat.joinToString("\n") {
                    val deadlineStr = it.deadline?.let { d -> DateUtil.formatTanggal(d) } ?: "-"
                    "• ${it.judul} (deadline: $deadlineStr)"
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            val sekarang = System.currentTimeMillis()
            val ujianMendatang = repository.getUjianList().filter { it.tanggal >= sekarang }
                .sortedBy { it.tanggal }.take(3)
            if (_binding == null) return@launch
            binding.textJumlahUjian.text = ujianMendatang.size.toString()
            binding.textUjianTerdekat.text = if (ujianMendatang.isEmpty()) {
                "Tidak ada ujian mendatang."
            } else {
                val pelajaranMap = repository.getPelajaranList().associateBy { it.id }
                ujianMendatang.joinToString("\n") {
                    val nama = pelajaranMap[it.pelajaranId]?.nama ?: "Umum"
                    "• $nama (${it.jenis}) - ${DateUtil.formatTanggal(it.tanggal)}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
