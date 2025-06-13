# API Testing Guide - AbsenKu App

## 🧪 Panduan Testing Setelah Migrasi API

### 📋 Pre-Testing Checklist
- [ ] Pastikan internet connection aktif
- [ ] Build aplikasi tanpa error
- [ ] API server online (check: https://absensi-backend-gabungan-production.up.railway.app/)

## 🔐 Test Login Functionality

### Test Login Siswa
1. Buka aplikasi
2. Pilih "Login Siswa"
3. Masukkan credentials:
   - Username: `siswa1`
   - Password: `siswa1`
4. ✅ **Expected**: Login berhasil, redirect ke dashboard siswa

### Test Login Guru
1. Buka aplikasi
2. Pilih "Login Guru"
3. Masukkan credentials:
   - Username: `guru1`
   - Password: `guru1`
4. ✅ **Expected**: Login berhasil, redirect ke dashboard guru

## 👤 Test Profile Functionality

### Test Profile Siswa
1. Login sebagai siswa
2. Navigasi ke "Profil"
3. ✅ **Expected**: Data profil siswa tampil (nama, NIS, kelas, dll)
4. Klik "Edit Info Pribadi"
5. ✅ **Expected**: Form edit terisi dengan data saat ini
6. Ubah nama, alamat, atau no HP
7. Klik "Simpan"
8. ✅ **Expected**: Success message, kembali ke profil dengan data terupdate

### Test Profile Guru
1. Login sebagai guru
2. Navigasi ke "Profil"
3. ✅ **Expected**: Data profil guru tampil (nama, NIP, dll)
4. Klik "Edit Info Pribadi"
5. ✅ **Expected**: Form edit terisi dengan data saat ini
6. Ubah nama, alamat, atau no HP
7. Klik "Simpan"
8. ✅ **Expected**: Success message, kembali ke profil dengan data terupdate

## 🔑 Test Password Change (Mock)
1. Login sebagai siswa atau guru
2. Navigasi ke "Profil"
3. Klik "Edit Password"
4. Masukkan:
   - Password lama: sesuai login
   - Password baru: password123
   - Ulangi password: password123
5. Klik "Simpan"
6. ✅ **Expected**: Success message (menggunakan mock service)

## 🚨 Error Scenarios Testing

### Test Invalid Login
1. Masukkan username/password salah
2. ✅ **Expected**: Error message "Invalid credentials"

### Test Network Error
1. Matikan internet
2. Coba login atau load profile
3. ✅ **Expected**: Error message "Gagal terhubung ke server"

### Test Token Expired
1. Login berhasil
2. Tunggu token expire (atau manipulasi token)
3. Coba akses profile
4. ✅ **Expected**: Error message atau redirect ke login

## 📊 API Response Validation

### Check Login Response
Login berhasil harus return:
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "siswa|guru",
  "user": {
    "username": "siswa1",
    "role": "siswa"
  }
}
```

### Check Profile Response
Profile siswa harus return:
```json
{
  "message": "Profile siswa berhasil diambil",
  "profile": {
    "id": "1",
    "nama": "Aldi",
    "nis": "123456",
    "jenis_kelamin": "L",
    "alamat": null,
    "no_hp": null,
    "username": "siswa1",
    "id_kelas": 1,
    "nama_kelas": "X-IPA"
  }
}
```

## 🔧 Debugging Tips

### Check Logs
Monitor Android Studio Logcat untuk:
- HTTP request/response logs
- Error messages
- Token validation issues

### Common Issues
1. **"Token tidak ditemukan"**: Check SessionManager implementation
2. **"Gagal terhubung ke server"**: Check internet dan API server status
3. **"Gagal memuat profil"**: Check API response format

### Fallback Testing
Jika API bermasalah, temporary enable mock:
```java
// Di AppConfig.java
public static final boolean USE_MOCK_LOGIN = true;
public static final boolean USE_MOCK_PROFILE = true;
```

## ✅ Success Criteria
- [ ] Login siswa dan guru berhasil
- [ ] Profile data loading berhasil
- [ ] Update profile berhasil
- [ ] Error handling berfungsi dengan baik
- [ ] UI responsive dan user-friendly
- [ ] Tidak ada crash atau ANR

## 📝 Test Report Template
```
Date: [DATE]
Tester: [NAME]
App Version: [VERSION]

Login Tests:
- Siswa Login: ✅/❌
- Guru Login: ✅/❌

Profile Tests:
- Load Siswa Profile: ✅/❌
- Load Guru Profile: ✅/❌
- Update Siswa Profile: ✅/❌
- Update Guru Profile: ✅/❌

Error Handling:
- Invalid Login: ✅/❌
- Network Error: ✅/❌

Issues Found:
[LIST ANY ISSUES]

Notes:
[ADDITIONAL NOTES]
```
