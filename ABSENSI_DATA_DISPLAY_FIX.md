# Perbaikan Tampilan Data Absensi - AbsenKu App

## 🚨 Masalah yang Ditemukan

### 1. Response API Siswa Berbeda
**Log API Response:**
```json
{
  "message": "Riwayat absensi berhasil diambil",
  "siswa": {
    "id": "1",
    "nama": "Aldi Updated 2",
    "nis": "123456",
    "kelas": "X-IPA"
  },
  "total_records": 2,
  "absensi": [
    {
      "id": 49,
      "tanggal": "2025-06-05T00:00:00.000Z",
      "status": "izin",
      "nama_kelas": "X-IPA",
      "nama_guru": "Joko"
    }
  ]
}
```

**Masalah:** Model `AbsensiResponse` tidak sesuai dengan struktur response siswa.

### 2. Adapter Tidak Menampilkan Data
- Data berhasil di-fetch dari API (status 200)
- Tapi RecyclerView tidak menampilkan apa-apa
- Adapter tidak handle response siswa dengan benar

### 3. Submit Absensi Belum Diimplementasi
- Guru tidak bisa submit absensi baru
- Tombol "Simpan Absensi" hanya placeholder

## ✅ Solusi yang Diimplementasikan

### 1. Model Baru untuk Response Siswa
**File:** `SiswaAbsensiResponse.java`
```java
public class SiswaAbsensiResponse {
    private String message;
    private SiswaInfo siswa;           // ✅ Sesuai API response
    private int total_records;         // ✅ Sesuai API response
    private List<AbsensiItem> absensi; // ✅ Sesuai API response
    
    public static class SiswaInfo {
        private String id, nama, nis, kelas;
    }
    
    public static class AbsensiItem {
        private int id;
        private String tanggal, status, nama_kelas, nama_guru;
    }
}
```

### 2. Update ApiService
```java
// SEBELUM:
@GET("api/siswa/absensi")
Call<AbsensiResponse> getSiswaAbsensi(@Header("Authorization") String token);

// SESUDAH:
@GET("api/siswa/absensi")
Call<SiswaAbsensiResponse> getSiswaAbsensi(@Header("Authorization") String token);
```

### 3. Adapter yang Diperbaiki
**File:** `AbsensiAdapter.java`

**Fitur Baru:**
- ✅ **3 Constructor** untuk berbagai mode:
  - Siswa viewing history
  - Guru viewing existing absensi
  - Guru creating new absensi

- ✅ **Status Selection Tracking:**
```java
private Map<Integer, String> statusSelections = new HashMap<>();

// Track status changes in spinner
spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedStatus = statusOptions[position];
        statusSelections.put(position, selectedStatus);
    }
});
```

- ✅ **Methods untuk Submit:**
```java
public List<String> getAllStatusSelections()     // Get all selected status
public List<SiswaListResponse.SiswaData> getSiswaList()  // Get siswa data
```

### 4. Submit Absensi Implementation
**File:** `AbsenActivity.java`

**Fitur Submit Lengkap:**
```java
private void submitAbsensi() {
    // 1. Validation
    if (!"guru".equals(userRole)) return;
    if (adapter == null || adapter.getSiswaList() == null) return;
    
    // 2. Prepare data
    List<AbsensiRequest.AbsensiItem> absensiItems = new ArrayList<>();
    List<SiswaListResponse.SiswaData> siswaList = adapter.getSiswaList();
    List<String> statusList = adapter.getAllStatusSelections();
    
    for (int i = 0; i < siswaList.size(); i++) {
        SiswaListResponse.SiswaData siswa = siswaList.get(i);
        String status = statusList.get(i);
        absensiItems.add(new AbsensiRequest.AbsensiItem(siswa.getId(), status));
    }
    
    // 3. Submit to API
    AbsensiRequest request = new AbsensiRequest(selectedKelasId, selectedDate, guruId, absensiItems);
    apiService.submitAbsensi("Bearer " + token, request);
}
```

## 🎯 Hasil Perbaikan

### Untuk Siswa:
- ✅ **Data absensi tampil** dengan format yang benar
- ✅ **Tanggal dan kelas** ditampilkan dengan jelas
- ✅ **Status dengan warna** (hadir=hijau, sakit=orange, izin=biru, alpha=merah)
- ✅ **Format:** "Tanggal: 2025-06-05 | Kelas: X-IPA | Guru: Joko"

### Untuk Guru:
- ✅ **Load kelas** yang diajar
- ✅ **Load siswa** per kelas untuk absensi baru
- ✅ **Spinner status** untuk setiap siswa
- ✅ **Submit absensi** ke API dengan validasi lengkap
- ✅ **Loading state** saat submit
- ✅ **Success feedback** dengan jumlah siswa yang disimpan

## 🔧 API Workflow

### Siswa Workflow:
1. `GET /api/siswa/absensi` → Load riwayat absensi
2. Parse response dengan `SiswaAbsensiResponse`
3. Display di RecyclerView dengan status berwarna

### Guru Workflow:
1. `GET /api/kelas?guru_id={id}` → Load kelas yang diajar
2. `GET /api/absen?id_kelas={id}&tanggal={date}` → Cek absensi existing
3. Jika tidak ada: `GET /api/siswa/list?id_kelas={id}` → Load siswa
4. User pilih status untuk setiap siswa
5. `POST /api/absen/submit` → Submit absensi baru

## 🧪 Testing Guide

### Test Siswa:
1. ✅ Login sebagai siswa (`siswa1`/`siswa1`)
2. ✅ Buka menu "Absensi"
3. ✅ Klik "Muat Data Absensi"
4. ✅ **Expected**: Tampil riwayat absensi dengan format:
   ```
   Tanggal: 2025-06-05
   Kelas: X-IPA | Guru: Joko
   [STATUS BERWARNA]
   ```

### Test Guru:
1. ✅ Login sebagai guru (`guru1`/`guru1`)
2. ✅ Buka menu "Absensi"
3. ✅ Pilih kelas dari dropdown
4. ✅ Klik "Muat Data Absensi"
5. ✅ **Expected**: Tampil list siswa dengan spinner status
6. ✅ Ubah status beberapa siswa
7. ✅ Klik "Simpan Absensi"
8. ✅ **Expected**: Success message + reload data

## 🎉 Status: FIXED ✅

- [x] Data absensi siswa tampil dengan benar
- [x] Guru bisa submit absensi baru
- [x] Status selection berfungsi
- [x] API integration lengkap
- [x] Error handling dan validation
- [x] Loading states dan feedback

## 📝 Notes
- Response API sudah sesuai dengan model baru
- Adapter mendukung 3 mode berbeda
- Submit absensi menggunakan format yang benar sesuai API
- Date formatting otomatis untuk display yang user-friendly
