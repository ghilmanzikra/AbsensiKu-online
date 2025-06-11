# 🧪 Panduan Testing Login Siswa dengan API

## ⚠️ PERBAIKAN MASALAH 404 API

**Masalah**: Backend API di Railway sudah tidak aktif (Error 404)
**Solusi**: Menggunakan Mock Login Service untuk testing offline

## 🔧 Mode Testing

### **Mode Mock (Default - Aktif)**

-    Set `AppConfig.USE_MOCK_LOGIN = true`
-    Login bekerja offline tanpa internet
-    Data tersimpan lokal untuk testing

### **Mode Real API (Jika backend aktif)**

-    Set `AppConfig.USE_MOCK_LOGIN = false`
-    Memerlukan backend API yang aktif

## 📋 Akun Testing yang Tersedia

Akun siswa yang bisa digunakan untuk testing (Mock Mode):

### **Akun Siswa:**

1. **Username**: `siswa1` | **Password**: `siswa1`
2. **Username**: `siswa2` | **Password**: `siswa2`
3. **Username**: `siswa3` | **Password**: `siswa3`

### **Akun Guru (untuk testing cross-validation):**

-    **Username**: `guru1` | **Password**: `guru1`

## 🔧 Konfigurasi API

### **Production URL (Default):**

```
https://absensi-backend-gabungan.up.railway.app/
```

### **Development URL (Jika menggunakan server lokal):**

Uncomment di `ApiClient.java`:

```java
// private static final String BASE_URL = "http://10.0.2.2:5000/"; // untuk emulator
// private static final String BASE_URL = "http://192.168.1.100:5000/"; // untuk device fisik
```

## 🧪 Skenario Testing

### **1. Login Berhasil (Siswa)**

-    **Input**: Username: `siswa1`, Password: `siswa1`
-    **Expected**:
     -    Toast: "Login berhasil! Selamat datang siswa1"
     -    Redirect ke DashboardActivity
     -    Session tersimpan

### **2. Login Gagal - Username Salah**

-    **Input**: Username: `siswaxx`, Password: `siswa1`
-    **Expected**: Toast: "Username tidak ditemukan"

### **3. Login Gagal - Password Salah**

-    **Input**: Username: `siswa1`, Password: `wrongpass`
-    **Expected**: Toast: "Password salah"

### **4. Login dengan Akun Guru**

-    **Input**: Username: `guru1`, Password: `guru1`
-    **Expected**: Toast: "Akun ini bukan akun siswa. Silakan gunakan login guru."

### **5. Field Kosong**

-    **Input**: Username: `, Password: `
-    **Expected**: Toast: "Username dan Password harus diisi"

### **6. Koneksi Internet Bermasalah**

-    **Test**: Matikan internet/WiFi
-    **Expected**: Toast: "Gagal terhubung ke server. Periksa koneksi internet Anda."

## 🔍 Fitur yang Diimplementasi

### ✅ **API Integration**

-    Retrofit untuk HTTP requests
-    Gson untuk JSON parsing
-    Error handling yang comprehensive

### ✅ **Session Management**

-    JWT token disimpan di SharedPreferences
-    Auto-login jika session masih valid
-    Logout functionality

### ✅ **User Experience**

-    Loading indicator (ProgressBar)
-    Button disabled saat loading
-    Error messages yang informatif
-    Input validation

### ✅ **Security**

-    Network security config untuk development
-    HTTPS support untuk production
-    Token-based authentication

## 🚀 Cara Testing

1. **Build aplikasi** dengan dependencies baru
2. **Install di device/emulator**
3. **Test dengan akun siswa** yang valid
4. **Verify redirect** ke dashboard
5. **Test error scenarios** untuk validasi

## 🔧 Troubleshooting

### **Jika API tidak bisa diakses:**

1. Cek koneksi internet
2. Verify URL di `ApiClient.java`
3. Check network security config

### **Jika build error:**

1. Sync project dengan Gradle files
2. Clean dan rebuild project
3. Check dependencies di `build.gradle.kts`

### **Jika login selalu gagal:**

1. Check logcat untuk error details
2. Verify API endpoint masih aktif
3. Test dengan Postman/curl terlebih dahulu
