package com.studymate.sm.cid.ui.pengaturan

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.studymate.sm.cid.R
import com.studymate.sm.cid.StudyMateApp
import com.studymate.sm.cid.databinding.FragmentPengaturanBinding
import com.studymate.sm.cid.util.JsonBackupManager
import com.studymate.sm.cid.util.PreferencesManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PengaturanFragment : Fragment() {

    private var _binding: FragmentPengaturanBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: PreferencesManager
    private val repository by lazy { (requireActivity().application as StudyMateApp).repository }
    private val backupManager by lazy { JsonBackupManager(requireContext(), repository) }

    private val pembuatBackup = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) simpanBackup(uri)
    }

    private val pemilihImport = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) konfirmasiImport(uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPengaturanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PreferencesManager(requireContext())

        binding.toolbarInclude.toolbar.title = "Pengaturan"
        binding.toolbarInclude.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarInclude.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.inputNama.setText(prefs.namaPengguna)
        binding.inputApiKey.setText(prefs.geminiApiKey)

        binding.btnSimpanNama.setOnClickListener {
            prefs.namaPengguna = binding.inputNama.text?.toString()?.trim().orEmpty()
            Toast.makeText(requireContext(), "Nama disimpan", Toast.LENGTH_SHORT).show()
        }

        binding.btnSimpanApiKey.setOnClickListener {
            prefs.geminiApiKey = binding.inputApiKey.text?.toString()?.trim().orEmpty()
            Toast.makeText(requireContext(), "API key disimpan", Toast.LENGTH_SHORT).show()
        }

        binding.btnBackup.setOnClickListener {
            val nama = "studymate_backup_${SimpleDateFormat("yyyyMMdd_HHmm", Locale("id", "ID")).format(Date())}.json"
            pembuatBackup.launch(nama)
        }

        binding.btnImport.setOnClickListener {
            pemilihImport.launch(arrayOf("application/json"))
        }
    }

    private fun simpanBackup(uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            val hasil = backupManager.simpanKeUri(uri)
            hasil.onSuccess {
                Toast.makeText(requireContext(), "Backup berhasil disimpan", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Backup gagal: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun konfirmasiImport(uri: Uri) {
        AlertDialog.Builder(requireContext())
            .setTitle("Import Data")
            .setMessage("Data yang ada sekarang akan digantikan dengan isi file backup ini. Lanjutkan?")
            .setPositiveButton("Lanjutkan") { _, _ -> jalankanImport(uri) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun jalankanImport(uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            val hasilBaca = backupManager.bacaDariUri(uri)
            hasilBaca.onSuccess { data ->
                val hasilImport = backupManager.importDanTimpaData(data)
                hasilImport.onSuccess {
                    Toast.makeText(requireContext(), "Import data berhasil", Toast.LENGTH_SHORT).show()
                }.onFailure {
                    Toast.makeText(requireContext(), "Import gagal: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }.onFailure {
                Toast.makeText(requireContext(), "Gagal membaca file: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
