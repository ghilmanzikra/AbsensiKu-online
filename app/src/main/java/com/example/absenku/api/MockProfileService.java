package com.example.absenku.api;

import com.example.absenku.models.ProfileResponse;
import java.util.HashMap;
import java.util.Map;

public class MockProfileService {
    
    // Mock database untuk profile siswa
    private static final Map<String, ProfileData> MOCK_PROFILES = new HashMap<>();
    
    static {
        // Data siswa sesuai dengan API documentation
        MOCK_PROFILES.put("siswa1", new ProfileData(
            "1", "Aldi", "123456", "L", null, null, "siswa1", 1, "X-IPA"
        ));
        
        MOCK_PROFILES.put("siswa2", new ProfileData(
            "2", "Sari", "123457", "P", "Jl. Merdeka No. 10", "081234567890", "siswa2", 1, "X-IPA"
        ));
        
        MOCK_PROFILES.put("siswa3", new ProfileData(
            "3", "Budi", "123458", "L", "Jl. Sudirman No. 15", "081234567891", "siswa3", 2, "X-IPS"
        ));
    }
    
    public static class ProfileResult {
        public boolean success;
        public String errorMessage;
        public ProfileResponse profileResponse;
        
        public ProfileResult(boolean success, String errorMessage, ProfileResponse profileResponse) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.profileResponse = profileResponse;
        }
    }
    
    public static class ProfileData {
        public String id;
        public String nama;
        public String nis;
        public String jenis_kelamin;
        public String alamat;
        public String no_hp;
        public String username;
        public int id_kelas;
        public String nama_kelas;
        
        public ProfileData(String id, String nama, String nis, String jenis_kelamin, 
                          String alamat, String no_hp, String username, int id_kelas, String nama_kelas) {
            this.id = id;
            this.nama = nama;
            this.nis = nis;
            this.jenis_kelamin = jenis_kelamin;
            this.alamat = alamat;
            this.no_hp = no_hp;
            this.username = username;
            this.id_kelas = id_kelas;
            this.nama_kelas = nama_kelas;
        }
    }
    
    public static ProfileResult getStudentProfile(String username) {
        try {
            // Validasi input
            if (username == null || username.trim().isEmpty()) {
                return new ProfileResult(false, "Username tidak valid", null);
            }
            
            // Trim input
            username = username.trim();
            
            // Cek apakah profile ada
            ProfileData profileData = MOCK_PROFILES.get(username);
            if (profileData == null) {
                return new ProfileResult(false, "Profile tidak ditemukan", null);
            }
            
            // Buat response sukses
            ProfileResponse response = new ProfileResponse();
            response.setMessage("Profile siswa berhasil diambil");
            
            ProfileResponse.Profile profile = new ProfileResponse.Profile();
            profile.setId(profileData.id);
            profile.setNama(profileData.nama);
            profile.setNis(profileData.nis);
            profile.setJenis_kelamin(profileData.jenis_kelamin);
            profile.setAlamat(profileData.alamat);
            profile.setNo_hp(profileData.no_hp);
            profile.setUsername(profileData.username);
            profile.setId_kelas(profileData.id_kelas);
            profile.setNama_kelas(profileData.nama_kelas);
            
            response.setProfile(profile);
            
            return new ProfileResult(true, null, response);
            
        } catch (Exception e) {
            return new ProfileResult(false, "Error: " + e.getMessage(), null);
        }
    }

    public static ProfileResult getProfile(String username, String role) {
        // Untuk saat ini, hanya support siswa
        if ("siswa".equals(role)) {
            return getStudentProfile(username);
        } else {
            return new ProfileResult(false, "Role tidak didukung", null);
        }
    }

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

    public static UpdateResult updateProfile(String username, String role, String namaLengkap,
                                           String nis, String jenisKelamin, String alamat,
                                           String nomorHp, String kelas) {
        try {
            // Validasi input
            if (username == null || username.trim().isEmpty()) {
                return new UpdateResult(false, "Username tidak valid", null);
            }

            if (!"siswa".equals(role)) {
                return new UpdateResult(false, "Role tidak didukung", null);
            }

            // Trim input
            username = username.trim();

            // Cek apakah profile ada
            ProfileData profileData = MOCK_PROFILES.get(username);
            if (profileData == null) {
                return new UpdateResult(false, "Profile tidak ditemukan", null);
            }

            // Update data
            profileData.nama = namaLengkap != null ? namaLengkap.trim() : profileData.nama;
            profileData.nis = nis != null ? nis.trim() : profileData.nis;
            profileData.jenis_kelamin = jenisKelamin != null ?
                (jenisKelamin.equals("Laki-laki") ? "L" : "P") : profileData.jenis_kelamin;
            profileData.alamat = alamat != null ? alamat.trim() : profileData.alamat;
            profileData.no_hp = nomorHp != null ? nomorHp.trim() : profileData.no_hp;
            profileData.nama_kelas = kelas != null ? kelas.trim() : profileData.nama_kelas;

            // Update di map
            MOCK_PROFILES.put(username, profileData);

            return new UpdateResult(true, null, "Profile berhasil diupdate");

        } catch (Exception e) {
            return new UpdateResult(false, "Error: " + e.getMessage(), null);
        }
    }
}
