# API Migration Summary - AbsenKu App

## 🎯 Tujuan
Mengganti semua API localhost dengan API online production yang tersedia di:
`https://absensi-backend-gabungan-production.up.railway.app/`

## ✅ Perubahan yang Telah Dilakukan

### 1. Konfigurasi Utama (AppConfig.java)
```java
// SEBELUM:
public static final boolean USE_MOCK_LOGIN = true;
public static final boolean USE_MOCK_PROFILE = true;
public static final boolean USE_MOCK_PASSWORD = true;
public static final String PRODUCTION_API_URL = "https://absensi-backend-gabungan.up.railway.app/";

// SESUDAH:
public static final boolean USE_MOCK_LOGIN = false;
public static final boolean USE_MOCK_PROFILE = false;
public static final boolean USE_MOCK_PASSWORD = false;
public static final String PRODUCTION_API_URL = "https://absensi-backend-gabungan-production.up.railway.app/";
```

### 2. API Client (ApiClient.java)
```java
// SEBELUM:
private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

// SESUDAH:
private static final String BASE_URL = "https://absensi-backend-gabungan-production.up.railway.app/";
```

### 3. API Service (ApiService.java)
- ❌ Dihapus: `changeStudentPassword()` dan `changeGuruPassword()` (tidak ada di API)
- ✅ Ditambah: `updateStudentProfile()` dan `updateGuruProfile()` (sesuai dokumentasi API)

### 4. Model Classes Baru
- ✅ `UpdateProfileRequest.java` - untuk request update profile
- ✅ `UpdateProfileResponse.java` - untuk response update profile

### 5. Activity Updates
- **EditPasswordActivity**: Menggunakan mock service (API tidak memiliki endpoint change password)
- **EditInfoPribadiActivity**: Implementasi real API untuk siswa
- **EditInfoPribadiGuruActivity**: Implementasi real API untuk guru
- **ProfilActivity & ProfilGuruActivity**: Perbaikan kondisi mock/real API

## 🔧 Endpoint API yang Digunakan

### Authentication
- `POST /api/auth/login` - Login siswa dan guru

### Profile Management
- `GET /api/siswa/profile` - Get profile siswa
- `GET /api/guru/profile` - Get profile guru
- `PUT /api/siswa/profile` - Update profile siswa
- `PUT /api/guru/profile` - Update profile guru

## 📝 Catatan Penting

### 1. Change Password Feature
- API production tidak memiliki endpoint untuk change password
- Feature ini tetap menggunakan mock service untuk sementara
- Jika diperlukan, bisa ditambahkan endpoint di backend

### 2. Authentication
- Semua endpoint yang memerlukan auth menggunakan Bearer token
- Format: `Authorization: Bearer <jwt_token>`

### 3. Default Login Credentials
**Guru:**
- Username: `guru1`
- Password: `guru1`

**Siswa:**
- Username: `siswa1`, `siswa2`, `siswa3`
- Password: `siswa1`, `siswa2`, `siswa3`

## 🚀 Testing
Untuk testing aplikasi:
1. Build dan run aplikasi
2. Login menggunakan credentials di atas
3. Test fitur profile dan update profile
4. Pastikan tidak ada error koneksi

## 🔍 Troubleshooting
Jika ada masalah:
1. Pastikan internet connection aktif
2. Check API server status di Railway
3. Periksa log untuk error details
4. Jika API down, bisa temporary set mock services ke `true`

## 📋 Next Steps
1. Test semua fitur yang menggunakan API
2. Implementasi fitur absensi jika diperlukan
3. Tambah error handling yang lebih robust
4. Implementasi refresh token jika diperlukan
