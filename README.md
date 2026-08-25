# Study Mate (SM) — Teman Belajar Digital

Aplikasi Android native (Kotlin) untuk pendamping belajar pelajar: kelola pelajaran,
tugas/PR, jadwal, catatan materi, ujian/ulangan, keuangan sekolah, target belajar,
kalender akademik, backup/import data, dan AI Asisten (Gemini) — semua bekerja offline
kecuali fitur AI Asisten yang butuh koneksi internet.

## Cara build APK lewat GitHub Actions

1. Buat repository baru di GitHub, lalu push seluruh isi folder ini ke branch `main`.
   ```bash
   git init
   git add .
   git commit -m "Initial commit: Study Mate"
   git branch -M main
   git remote add origin https://github.com/<username>/<repo>.git
   git push -u origin main
   ```
2. Buka tab **Actions** di repository → workflow **"Build Study Mate APK"** akan
   otomatis berjalan setiap push ke `main` (atau jalankan manual lewat tombol
   **Run workflow**).
3. Setelah build selesai (tanda centang hijau ✔), buka hasil run tersebut →
   bagian **Artifacts** → unduh `app-debug` (berisi `app-debug.apk`).
4. Install `app-debug.apk` ke perangkat Android (aktifkan "Izinkan dari sumber
   tidak dikenal" bila diminta). Ikon aplikasi Study Mate akan muncul otomatis
   di layar instalasi maupun setelah terpasang.

## Fitur Utama

- Dashboard ringkasan harian (jadwal hari ini, tugas & ujian terdekat)
- Manajemen Pelajaran + halaman Detail Pelajaran
- Tugas/PR dengan status selesai & deadline
- Jadwal Pelajaran per hari
- Ujian & Ulangan (UH, UTS, UAS, dll)
- Catatan Materi per bab/halaman
- Kalender Akademik (ujian, libur, acara, deadline)
- Target Belajar dengan progress
- Keuangan Sekolah (uang sangu & pengeluaran)
- Kategori Pelajaran
- AI Asisten belajar (Gemini API — API key diisi sendiri di menu Pengaturan)
- Backup & Import seluruh data dalam format JSON
- Info Developer

## Catatan Teknis

- Package: `com.studymate.sm.cid`
- Database lokal: Room (SQLite), semua data tersimpan di perangkat
- AI Asisten memakai Gemini API (`gemini-flash-latest`) lewat OkHttp; pengguna
  perlu memasukkan API key pribadi dari [Google AI Studio](https://aistudio.google.com)
  di menu **Pengaturan**
- Minimum SDK: 24 (Android 7.0), Target SDK: 34
- Ikon aplikasi dibuat dari logo Study Mate (adaptive icon + legacy icon semua
  densitas), sehingga tampil normal di semua versi Android termasuk saat proses
  instalasi

## Developer

Nugroho Y.R. — GitHub: [computerid-dev](https://github.com/computerid-dev)
