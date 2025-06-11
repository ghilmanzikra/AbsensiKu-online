# 📝 Edit Info Pribadi Guru - Same Pattern as Siswa Guide

## ✅ EDIT INFO PRIBADI GURU SUDAH DIUPDATE - SAME PATTERN AS SISWA!

Edit info pribadi guru sekarang menggunakan pattern yang sama persis dengan siswa: load data dari mock service, populate form, dan update database saat simpan.

## 🔄 Pattern Consistency - Siswa vs Guru

### **1. Same Implementation Pattern**
| Step | Siswa | Guru | Status |
|------|-------|------|--------|
| **Load Profile** | MockProfileService.getProfile() | MockGuruProfileService.getGuruProfile() | ✅ Same pattern |
| **Populate Form** | populateFields(ProfileResponse) | populateFields(GuruProfileResponse) | ✅ Same pattern |
| **Validate Input** | validateInput() | validateInput() | ✅ Same pattern |
| **Update Database** | MockProfileService.updateProfile() | MockGuruProfileService.updateGuruProfile() | ✅ Same pattern |
| **Success Flow** | SweetAlert → Navigate back | SweetAlert → Navigate back | ✅ Same pattern |

### **2. Code Structure Comparison**
```java
// SISWA PATTERN
private void loadMockProfile(String username, String role) {
    MockProfileService.ProfileResult result = MockProfileService.getProfile(username, role);
    if (result != null && result.success && result.profileResponse != null) {
        populateFields(result.profileResponse);
    }
}

// GURU PATTERN (NOW SAME)
private void loadMockProfile(String username, String role) {
    MockGuruProfileService.GuruProfileResult result = MockGuruProfileService.getGuruProfile(username);
    if (result != null && result.success && result.guruProfileResponse != null) {
        populateFields(result.guruProfileResponse);
    }
}
```

## 📋 MockGuruProfileService Enhancement

### **1. Added Update Method**
```java
public static class UpdateResult {
    public boolean success;
    public String errorMessage;
    public String successMessage;

    public UpdateResult(boolean success, String errorMessage, String successMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.successMessage = successMessage;
    }
}

public static UpdateResult updateGuruProfile(String username, String namaLengkap, String nip, 
                                           String mataPelajaran, String jenisKelamin, String alamat, String nomorHp) {
    try {
        // Validasi input
        if (username == null || username.trim().isEmpty()) {
            return new UpdateResult(false, "Username tidak valid", null);
        }

        // Cek apakah profile ada
        GuruProfileData profileData = MOCK_GURU_PROFILES.get(username);
        if (profileData == null) {
            return new UpdateResult(false, "Profile guru tidak ditemukan", null);
        }

        // Update data
        profileData.nama = namaLengkap != null ? namaLengkap.trim() : profileData.nama;
        profileData.nip = nip != null ? nip.trim() : profileData.nip;
        profileData.mata_pelajaran = mataPelajaran != null ? mataPelajaran.trim() : profileData.mata_pelajaran;
        profileData.jenis_kelamin = jenisKelamin != null ?
            (jenisKelamin.equals("Laki-laki") ? "L" : "P") : profileData.jenis_kelamin;
        profileData.alamat = alamat != null ? alamat.trim() : profileData.alamat;
        profileData.no_hp = nomorHp != null ? nomorHp.trim() : profileData.no_hp;

        // Update di map
        MOCK_GURU_PROFILES.put(username, profileData);

        return new UpdateResult(true, null, "Profile guru berhasil diupdate");

    } catch (Exception e) {
        return new UpdateResult(false, "Error: " + e.getMessage(), null);
    }
}
```

### **2. Mock Data Structure**
```java
// Existing guru mock data
MOCK_GURU_PROFILES.put("guru1", new GuruProfileData(
    "1", "Pak Ahmad", "198501012010011001", "L", "Jl. Pendidikan No. 1", "081234567892", "guru1", "Matematika"
));

MOCK_GURU_PROFILES.put("guru2", new GuruProfileData(
    "2", "Bu Sari", "198502022010012002", "P", "Jl. Guru No. 5", "081234567893", "guru2", "Bahasa Indonesia"
));

MOCK_GURU_PROFILES.put("guru3", new GuruProfileData(
    "3", "Pak Budi", "198503032010011003", "L", "Jl. Sekolah No. 10", "081234567894", "guru3", "Fisika"
));
```

## 🔄 Form Population - Same as Siswa

### **1. populateFields Method - Guru Version**
```java
private void populateFields(GuruProfileResponse profileResponse) {
    if (profileResponse == null || profileResponse.getProfile() == null) return;

    try {
        GuruProfileResponse.GuruProfile profile = profileResponse.getProfile();
        
        // Populate fields dengan data profil guru
        if (etNamaLengkap != null && profile.getNama() != null) {
            etNamaLengkap.setText(profile.getNama());
        }
        
        if (etNip != null && profile.getNip() != null) {
            etNip.setText(profile.getNip());
        }
        
        if (etMataPelajaran != null && profile.getMata_pelajaran() != null) {
            etMataPelajaran.setText(profile.getMata_pelajaran());
        }
        
        if (etAlamat != null && profile.getAlamat() != null) {
            etAlamat.setText(profile.getAlamat());
        }
        
        if (etNomorHp != null && profile.getNo_hp() != null) {
            etNomorHp.setText(profile.getNo_hp());
        }
        
        // Set jenis kelamin di spinner
        if (spinnerJenisKelamin != null && profile.getJenis_kelamin() != null) {
            String jenisKelamin = profile.getJenis_kelamin();
            // Convert L/P to display format
            String displayJenisKelamin = "L".equals(jenisKelamin) ? "Laki-laki" : "Perempuan";
            
            for (int i = 0; i < jenisKelaminOptions.length; i++) {
                if (jenisKelaminOptions[i].equals(displayJenisKelamin)) {
                    spinnerJenisKelamin.setSelection(i);
                    break;
                }
            }
        }
        
    } catch (Exception e) {
        SweetAlertHelper.showError(this, "Error", "Gagal mengisi form: " + e.getMessage());
    }
}
```

### **2. Field Mapping - Guru Specific**
| Form Field | API Field | Method | Description |
|------------|-----------|--------|-------------|
| **Nama Lengkap** | `nama` | `getNama()` | Teacher full name |
| **NIP** | `nip` | `getNip()` | Teacher ID number |
| **Mata Pelajaran** | `mata_pelajaran` | `getMata_pelajaran()` | Subject taught |
| **Jenis Kelamin** | `jenis_kelamin` | `getJenis_kelamin()` | Gender (L/P → Laki-laki/Perempuan) |
| **Alamat** | `alamat` | `getAlamat()` | Address |
| **Nomor HP** | `no_hp` | `getNo_hp()` | Phone number |

## 💾 Database Update - Same Pattern as Siswa

### **1. performMockUpdate Method**
```java
private void performMockUpdate(String username, String role, String namaLengkap, String nip, 
                             String mataPelajaran, String jenisKelamin, String alamat, String nomorHp) {
    try {
        // Disable button during process
        btnSimpan.setEnabled(false);
        btnSimpan.setText("Menyimpan...");
        
        // Simulasi delay untuk UX yang realistis
        new android.os.Handler().postDelayed(() -> {
            try {
                MockGuruProfileService.UpdateResult result = MockGuruProfileService.updateGuruProfile(
                    username, namaLengkap, nip, mataPelajaran, jenisKelamin, alamat, nomorHp);
                
                if (result != null && result.success) {
                    SweetAlertHelper.showSuccess(this, "Berhasil", 
                        "Info pribadi guru berhasil diupdate", () -> navigateBackToProfile());
                } else {
                    String errorMessage = (result != null) ? result.errorMessage : "Gagal mengupdate profil guru";
                    SweetAlertHelper.showError(this, "Error", errorMessage);
                    resetButton();
                }
            } catch (Exception e) {
                SweetAlertHelper.showError(this, "Error", "Error: " + e.getMessage());
                resetButton();
            }
        }, 1000); // Delay 1 detik
        
    } catch (Exception e) {
        SweetAlertHelper.showError(this, "Error", "Error: " + e.getMessage());
        resetButton();
    }
}
```

### **2. Update Features - Same as Siswa**
- ✅ **Button State**: Disabled during update with "Menyimpan..." text
- ✅ **Realistic Delay**: 1 second delay for better UX
- ✅ **Success Feedback**: SweetAlert success message
- ✅ **Error Handling**: Proper error messages
- ✅ **Navigation**: Auto-navigate back to profile on success
- ✅ **Button Reset**: Re-enable button on error

## 🧪 Testing Flow - Same as Siswa

### **1. Form Load Test**
```
Step 1: Navigate from ProfilGuruActivity → Edit Info Pribadi
Step 2: Form loads with empty fields initially
Step 3: MockGuruProfileService.getGuruProfile() called
Step 4: Form populated with guru data from mock service
Expected: All fields show current guru data
```

### **2. Mock Data Test**
```
Login: guru1
Expected: 
- Nama: "Pak Ahmad"
- NIP: "198501012010011001"
- Mata Pelajaran: "Matematika"
- Jenis Kelamin: "Laki-laki" (from "L")
- Alamat: "Jl. Pendidikan No. 1"
- Nomor HP: "081234567892"

Login: guru2
Expected:
- Nama: "Bu Sari"
- NIP: "198502022010012002"
- Mata Pelajaran: "Bahasa Indonesia"
- Jenis Kelamin: "Perempuan" (from "P")
- Alamat: "Jl. Guru No. 5"
- Nomor HP: "081234567893"
```

### **3. Update Test**
```
Step 1: Load form with guru1 data
Step 2: Edit fields (change nama, mata pelajaran, etc.)
Step 3: Click Simpan button
Step 4: Button shows "Menyimpan..." and disabled
Step 5: After 1 second delay, success message
Step 6: Navigate back to ProfilGuruActivity
Step 7: Check if data updated in profile page
Expected: All changes saved and visible in profile
```

### **4. Validation Test**
```
Test 1: Empty nama → Error "Nama lengkap harus diisi"
Test 2: Empty NIP → Error "NIP harus diisi"
Test 3: Empty mata pelajaran → Error "Mata pelajaran harus diisi"
Test 4: Empty alamat → Error "Alamat harus diisi"
Test 5: Empty nomor HP → Error "Nomor HP harus diisi"
Test 6: Short nomor HP (< 10 digits) → Error "Nomor HP minimal 10 digit"
Test 7: All fields valid → Success update
```

## 🔄 Consistency Achieved

### **1. Same User Experience**
- ✅ **Form Loading**: Both load from mock service
- ✅ **Field Population**: Both populate from API response
- ✅ **Validation**: Both use same validation pattern
- ✅ **Update Process**: Both use same update flow
- ✅ **Success Handling**: Both use same success pattern
- ✅ **Error Handling**: Both use same error pattern

### **2. Same Code Structure**
- ✅ **Method Names**: Same naming convention
- ✅ **Error Messages**: Same message format
- ✅ **Button States**: Same button handling
- ✅ **Navigation**: Same navigation pattern
- ✅ **Delay Timing**: Same 1 second delay

### **3. Same Visual Behavior**
- ✅ **Loading State**: Button disabled during save
- ✅ **Success Feedback**: SweetAlert success message
- ✅ **Error Feedback**: SweetAlert error message
- ✅ **Form Reset**: Button re-enabled on error
- ✅ **Navigation**: Auto-navigate on success

## 🎯 Expected Results

### **1. Consistent Behavior**
- ✅ **Form Load**: Empty → Populated with current guru data
- ✅ **Edit Experience**: Same as siswa edit experience
- ✅ **Save Process**: Same loading, success, error handling
- ✅ **Navigation**: Same back-to-profile flow

### **2. Database Integration**
- ✅ **Data Persistence**: Changes saved to mock database
- ✅ **Immediate Reflection**: Changes visible in profile page
- ✅ **Cross-session**: Data persists across app sessions
- ✅ **Username-based**: Different data for different guru accounts

### **3. User Experience**
- ✅ **Familiar Interface**: Same as siswa edit page
- ✅ **Predictable Behavior**: Same patterns as other edit pages
- ✅ **Clear Feedback**: Success/error messages
- ✅ **Smooth Flow**: Load → Edit → Save → Navigate back

## 🎉 Status: GURU EDIT SAME PATTERN COMPLETE!

Edit info pribadi guru sekarang memiliki:
- 🔄 **Same Pattern as Siswa** - Identical implementation approach
- 📋 **Mock Service Integration** - Load from MockGuruProfileService
- 💾 **Database Update** - Save changes to mock database
- ✅ **Consistent Validation** - Same validation rules and messages
- 🎭 **Realistic UX** - Loading states, delays, feedback
- 🔗 **Seamless Navigation** - Same flow as siswa edit

**Test form loading dari mock service, edit fields, validation, save process, dan navigation untuk melihat complete guru edit functionality yang consistent dengan siswa!** 📝✅🔄
