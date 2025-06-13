# Quick Test Guide - Fitur Absensi

## 🚀 Langkah Testing Cepat

### 1. Build & Run
```bash
# Build aplikasi
./gradlew assembleDebug

# Install ke device/emulator
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 2. Test Login
- **Siswa**: username=`siswa1`, password=`siswa1`
- **Guru**: username=`guru1`, password=`guru1`

### 3. Test Menu Absensi

#### Test Siswa (Riwayat Absensi):
1. ✅ Login sebagai siswa
2. ✅ Tap menu "Absensi" di bottom navigation
3. ✅ **Expected**: Halaman absensi terbuka (tidak putih lagi!)
4. ✅ Pilih tanggal (default hari ini)
5. ✅ Tap "Muat Data Absensi"
6. ✅ **Expected**: Tampil riwayat absensi siswa atau pesan "tidak ada data"

#### Test Guru (Kelola Absensi):
1. ✅ Login sebagai guru
2. ✅ Tap menu "Absensi" di bottom navigation
3. ✅ **Expected**: Halaman absensi terbuka dengan dropdown kelas
4. ✅ Pilih tanggal
5. ✅ Pilih kelas dari dropdown
6. ✅ Tap "Muat Data Absensi"
7. ✅ **Expected**: Tampil list siswa dengan spinner status atau data absensi existing

## 🔍 Checklist Perbaikan

### ❌ Masalah Sebelumnya:
- [x] Halaman absensi putih kosong (siswa)
- [x] Force close saat buka absensi (guru)
- [x] Layout activity_absen.xml kosong
- [x] AbsenActivity tidak ada implementasi

### ✅ Perbaikan yang Dilakukan:
- [x] Layout lengkap dengan UI components
- [x] AbsenActivity dengan implementasi penuh
- [x] Model classes untuk absensi
- [x] Adapter untuk RecyclerView
- [x] API endpoints untuk absensi
- [x] Role-based functionality (guru vs siswa)

## 🎯 Expected Results

### Siswa Mode:
```
┌─────────────────────────┐
│ ← Absensi              │
├─────────────────────────┤
│ Pilih Tanggal           │
│ [13 Juni 2025      ▼]  │
├─────────────────────────┤
│ [Muat Data Absensi]     │
├─────────────────────────┤
│ Riwayat Absensi:        │
│ ┌─────────────────────┐ │
│ │ Aldi                │ │
│ │ NIS: 123456    HADIR│ │
│ └─────────────────────┘ │
└─────────────────────────┘
```

### Guru Mode:
```
┌─────────────────────────┐
│ ← Absensi              │
├─────────────────────────┤
│ Pilih Tanggal           │
│ [13 Juni 2025      ▼]  │
├─────────────────────────┤
│ Pilih Kelas             │
│ [X-IPA             ▼]  │
├─────────────────────────┤
│ [Muat Data Absensi]     │
├─────────────────────────┤
│ Daftar Siswa:           │
│ ┌─────────────────────┐ │
│ │ Aldi           [▼]  │ │
│ │ NIS: 123456    Hadir│ │
│ └─────────────────────┘ │
│ [Simpan Absensi]        │
└─────────────────────────┘
```

## 🚨 Troubleshooting

### Jika masih error:
1. **Clean & Rebuild**:
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

2. **Check Logcat** untuk error details:
   ```bash
   adb logcat | grep "com.example.absenku"
   ```

3. **Common Issues**:
   - **Network error**: Check internet connection
   - **Token expired**: Re-login
   - **API server down**: Check Railway server status

### Fallback Testing:
Jika API bermasalah, bisa temporary enable mock:
```java
// Di AppConfig.java
public static final boolean USE_MOCK_LOGIN = true;
public static final boolean USE_MOCK_PROFILE = true;
```

## 📊 Success Metrics
- [ ] Halaman absensi tidak putih lagi
- [ ] Tidak ada force close
- [ ] UI components tampil dengan benar
- [ ] Date picker berfungsi
- [ ] Dropdown kelas berfungsi (guru)
- [ ] RecyclerView menampilkan data
- [ ] Bottom navigation berfungsi
- [ ] Error handling dengan Sweet Alert

## 🎉 Hasil yang Diharapkan
Setelah perbaikan ini:
1. **Siswa** bisa melihat riwayat absensi mereka
2. **Guru** bisa melihat dan mengelola absensi kelas
3. **UI** responsive dan user-friendly
4. **No more crashes** atau halaman putih
5. **Proper error handling** dengan pesan yang jelas

## 📞 Support
Jika masih ada masalah:
1. Check file `ABSENSI_FEATURE_FIX.md` untuk detail teknis
2. Review API documentation di `API_DOCUMENTATION.md`
3. Test dengan credentials yang benar
4. Pastikan API server online
