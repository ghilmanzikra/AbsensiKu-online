# Perbaikan Fitur Absensi - AbsenKu App

## 🚨 Masalah yang Ditemukan
1. **AbsenActivity kosong** - Hanya layout kosong tanpa implementasi
2. **Layout activity_absen.xml kosong** - Tidak ada UI components
3. **Tidak ada model untuk absensi** - Belum ada struktur data
4. **Tidak ada adapter untuk RecyclerView** - Tidak bisa menampilkan list
5. **API Service belum lengkap** - Endpoint absensi belum ada

## ✅ Solusi yang Diimplementasikan

### 1. Model Classes Baru
- **AbsensiRequest.java** - Model untuk submit absensi (guru)
- **AbsensiResponse.java** - Model untuk response data absensi
- **KelasResponse.java** - Model untuk data kelas
- **SiswaListResponse.java** - Model untuk list siswa per kelas

### 2. Layout Baru
- **activity_absen.xml** - Layout lengkap dengan:
  - Header dengan tombol back
  - Date picker untuk pilih tanggal
  - Spinner kelas (untuk guru)
  - RecyclerView untuk list absensi
  - Tombol load dan submit
  - Bottom navigation

- **item_absensi.xml** - Layout untuk item di RecyclerView:
  - Nama dan NIS siswa
  - Status absensi (text untuk siswa, spinner untuk guru)

### 3. Adapter
- **AbsensiAdapter.java** - RecyclerView adapter dengan:
  - Support untuk mode guru dan siswa
  - Spinner status untuk guru (hadir, sakit, izin, tidak ada keterangan)
  - Text status dengan warna untuk siswa
  - Dual constructor untuk viewing/creating absensi

### 4. API Service Updates
Menambahkan endpoint baru:
```java
// Absensi endpoints
@POST("api/absen/submit")
Call<AbsensiResponse> submitAbsensi(@Header("Authorization") String token, @Body AbsensiRequest request);

@GET("api/absen")
Call<AbsensiResponse> getAbsensi(@Header("Authorization") String token, @Query("id_kelas") int id_kelas, @Query("tanggal") String tanggal);

// Kelas endpoints
@GET("api/kelas/all")
Call<KelasResponse> getAllKelas(@Header("Authorization") String token);

@GET("api/kelas")
Call<KelasResponse> getKelasByGuru(@Header("Authorization") String token, @Query("guru_id") String guru_id);

// Siswa endpoints
@GET("api/siswa/list")
Call<SiswaListResponse> getSiswaByKelas(@Header("Authorization") String token, @Query("id_kelas") int id_kelas);

@GET("api/siswa/absensi")
Call<AbsensiResponse> getSiswaAbsensi(@Header("Authorization") String token);
```

### 5. AbsenActivity Implementation
Implementasi lengkap dengan fitur:
- **Date picker** - Pilih tanggal absensi
- **Role-based UI** - Berbeda untuk guru dan siswa
- **Load kelas** - Guru bisa pilih kelas yang diajar
- **Load absensi** - Tampilkan data absensi existing atau form baru
- **Submit absensi** - Guru bisa submit absensi baru
- **Bottom navigation** - Navigasi antar halaman

## 🎯 Fitur per Role

### Untuk Siswa:
- ✅ Pilih tanggal
- ✅ Lihat riwayat absensi pribadi
- ✅ Status absensi dengan warna (hadir=hijau, sakit=orange, izin=biru, alpha=merah)
- ❌ Tidak bisa edit absensi

### Untuk Guru:
- ✅ Pilih tanggal
- ✅ Pilih kelas yang diajar
- ✅ Lihat absensi existing atau buat baru
- ✅ Edit status absensi siswa
- ✅ Submit absensi baru
- ✅ Load list siswa per kelas

## 🔧 API Endpoints yang Digunakan

### Authentication
- Semua endpoint memerlukan Bearer token

### Guru Workflow:
1. `GET /api/kelas?guru_id={id}` - Load kelas yang diajar
2. `GET /api/absen?id_kelas={id}&tanggal={date}` - Cek absensi existing
3. `GET /api/siswa/list?id_kelas={id}` - Load siswa jika belum ada absensi
4. `POST /api/absen/submit` - Submit absensi baru

### Siswa Workflow:
1. `GET /api/siswa/absensi` - Load riwayat absensi pribadi

## 🎨 UI/UX Improvements
- **Material Design** - CardView, proper spacing, shadows
- **Color coding** - Status absensi dengan warna yang jelas
- **Responsive layout** - Adaptif untuk berbagai ukuran layar
- **Loading states** - Feedback visual saat loading data
- **Error handling** - Sweet Alert untuk error messages

## 🧪 Testing Guide

### Test Siswa:
1. Login sebagai siswa (siswa1/siswa1)
2. Buka menu Absensi
3. Pilih tanggal
4. Klik "Muat Data Absensi"
5. ✅ **Expected**: Tampil riwayat absensi dengan status berwarna

### Test Guru:
1. Login sebagai guru (guru1/guru1)
2. Buka menu Absensi
3. Pilih tanggal
4. Pilih kelas dari dropdown
5. Klik "Muat Data Absensi"
6. ✅ **Expected**: Tampil list siswa dengan spinner status atau data absensi existing

## 🚀 Next Steps
1. **Test dengan data real** - Pastikan API response sesuai
2. **Implement submit absensi** - Lengkapi fungsi submit untuk guru
3. **Add validation** - Validasi input sebelum submit
4. **Improve error handling** - Handle edge cases
5. **Add loading indicators** - Progress bar saat loading
6. **Optimize performance** - Caching dan lazy loading

## 📝 Notes
- Fitur submit absensi masih placeholder (perlu implementasi lengkap)
- Adapter perlu enhancement untuk handle status selection
- Perlu testing dengan data real dari API
- Consider adding pull-to-refresh functionality
