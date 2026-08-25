package com.studymate.sm.cid.ui.ai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.studymate.sm.cid.R
import com.studymate.sm.cid.ai.GeminiClient
import com.studymate.sm.cid.databinding.FragmentAiAsistenBinding
import com.studymate.sm.cid.util.PreferencesManager
import kotlinx.coroutines.launch

class AiAsistenFragment : Fragment() {

    private var _binding: FragmentAiAsistenBinding? = null
    private val binding get() = _binding!!
    private val geminiClient = GeminiClient()
    private lateinit var prefs: PreferencesManager
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiAsistenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PreferencesManager(requireContext())

        binding.toolbarInclude.toolbar.title = "AI Asisten Belajar"
        binding.toolbarInclude.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarInclude.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        adapter = ChatAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        adapter.tambahPesan(
            ChatMessage(
                "Halo! Aku AI Asisten Study Mate. Ceritakan materi yang sedang kamu pelajari, " +
                    "nanti aku bantu jelaskan konsepnya pelan-pelan.",
                dariPengguna = false
            )
        )

        binding.btnKirim.setOnClickListener { kirimPesan() }
    }

    private fun kirimPesan() {
        val teks = binding.inputPesan.text?.toString()?.trim().orEmpty()
        if (teks.isEmpty()) return

        val apiKey = prefs.geminiApiKey
        if (apiKey.isBlank()) {
            Toast.makeText(requireContext(), "Atur dulu API key Gemini di menu Pengaturan", Toast.LENGTH_LONG).show()
            return
        }

        adapter.tambahPesan(ChatMessage(teks, dariPengguna = true))
        binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
        binding.inputPesan.setText("")
        binding.progressLoading.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            val hasil = geminiClient.kirimPesan(apiKey, null, teks)
            if (_binding == null) return@launch
            binding.progressLoading.visibility = View.GONE
            hasil.onSuccess { jawaban ->
                adapter.tambahPesan(ChatMessage(jawaban, dariPengguna = false))
            }.onFailure { error ->
                adapter.tambahPesan(ChatMessage("Maaf, terjadi kendala: ${error.message}", dariPengguna = false))
            }
            binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
