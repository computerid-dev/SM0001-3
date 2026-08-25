package com.studymate.sm.cid.ai

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Klien sederhana untuk memanggil Gemini API (model gemini-flash-latest)
 * lewat Google AI Studio. API key disimpan pengguna sendiri di Pengaturan.
 *
 * AI Asisten berperan sebagai tutor belajar, bukan mesin penjawab soal instan,
 * sehingga system instruction diarahkan untuk membimbing pemahaman konsep.
 */
class GeminiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .build()

    private val systemInstruction = """
        Kamu adalah AI Asisten di aplikasi Study Mate. Tugasmu membantu pelajar memahami
        materi pelajaran mereka, bukan memberi jawaban instan untuk tugas atau ujian.
        Jelaskan konsep secara bertahap, gunakan bahasa sederhana, berikan contoh bila perlu,
        dan ajak pengguna berpikir dengan pertanyaan pemandu. Jika pengguna hanya meminta
        jawaban langsung untuk PR atau soal ujian, arahkan mereka untuk memahami konsepnya
        terlebih dahulu alih-alih memberi jawaban akhir secara langsung.
    """.trimIndent()

    suspend fun kirimPesan(apiKey: String, konteksMateri: String?, pesanPengguna: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                if (apiKey.isBlank()) {
                    return@withContext Result.failure(IllegalStateException("API key Gemini belum diatur. Buka Pengaturan untuk memasang API key dari Google AI Studio."))
                }

                val promptLengkap = buildString {
                    append(systemInstruction)
                    append("\n\n")
                    if (!konteksMateri.isNullOrBlank()) {
                        append("Konteks materi pengguna:\n$konteksMateri\n\n")
                    }
                    append("Pertanyaan pengguna: $pesanPengguna")
                }

                val bodyJson = JsonObject().apply {
                    add("contents", JsonParser.parseString(
                        """[{"role":"user","parts":[{"text": ${gsonEscape(promptLengkap)} }]}]"""
                    ))
                }

                // Catatan: sejak pertengahan 2026 Google memindahkan autentikasi Gemini API
                // dari parameter query "?key=" (skema lama, "Standard key" berformat AIza...)
                // ke header "x-goog-api-key" (skema baru, "Auth key" berformat AQ....).
                // Kunci format lama sudah mulai ditolak kalau tidak dibatasi (restricted),
                // dan akan ditolak total mulai September 2026. Pakai header di sini supaya
                // kedua jenis key (lama & baru) tetap bisa jalan.
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

                val request = Request.Builder()
                    .url(url)
                    .header("x-goog-api-key", apiKey)
                    .post(bodyJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).execute().use { response ->
                    val bodyString = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        val pesanRamah = when (response.code) {
                            400, 401, 403 -> "API key ditolak (kode ${response.code}). Cek lagi API key di menu Pengaturan, pastikan disalin utuh dan masih aktif di Google AI Studio."
                            429 -> "Kuota Gemini API kamu sudah habis untuk saat ini (kode 429). Coba lagi beberapa saat lagi."
                            else -> "Gagal menghubungi Gemini (kode ${response.code}): $bodyString"
                        }
                        return@withContext Result.failure(Exception(pesanRamah))
                    }
                    val json = JsonParser.parseString(bodyString).asJsonObject
                    val text = json.getAsJsonArray("candidates")
                        ?.get(0)?.asJsonObject
                        ?.getAsJsonObject("content")
                        ?.getAsJsonArray("parts")
                        ?.get(0)?.asJsonObject
                        ?.get("text")?.asString
                        ?: "Maaf, AI Asisten tidak bisa memberi jawaban saat ini."
                    Result.success(text)
                }
            } catch (e: java.net.UnknownHostException) {
                Result.failure(Exception("Tidak ada koneksi internet. Cek apakah mode pesawat aktif atau WiFi/data seluler menyala, lalu coba lagi."))
            } catch (e: java.net.SocketTimeoutException) {
                Result.failure(Exception("Koneksi ke server AI terlalu lama (timeout). Biasanya karena sinyal internet lemah atau mode pesawat aktif. Cek koneksi lalu coba lagi."))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun gsonEscape(text: String): String {
        return com.google.gson.Gson().toJson(text)
    }

    companion object {
        private const val MODEL = "gemini-flash-latest"
    }
}
