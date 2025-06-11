package com.example.absenku.api;

import com.example.absenku.models.GuruProfileResponse;
import java.util.HashMap;
import java.util.Map;

public class MockGuruProfileService {
    
    // Mock database untuk profile guru
    private static final Map<String, GuruProfileData> MOCK_GURU_PROFILES = new HashMap<>();
    
    static {
        // Data guru sesuai dengan API documentation
        MOCK_GURU_PROFILES.put("guru1", new GuruProfileData(
            "1", "Pak Ahmad", "198501012010011001", "L", "Jl. Pendidikan No. 1", "081234567892", "guru1", "Matematika"
        ));
        
        MOCK_GURU_PROFILES.put("guru2", new GuruProfileData(
            "2", "Bu Sari", "198502022010012002", "P", "Jl. Guru No. 5", "081234567893", "guru2", "Bahasa Indonesia"
        ));
        
        MOCK_GURU_PROFILES.put("guru3", new GuruProfileData(
            "3", "Pak Budi", "198503032010011003", "L", "Jl. Sekolah No. 10", "081234567894", "guru3", "Fisika"
        ));
    }
    
    public static class GuruProfileResult {
        public boolean success;
        public String errorMessage;
        public GuruProfileResponse guruProfileResponse;
        
        public GuruProfileResult(boolean success, String errorMessage, GuruProfileResponse guruProfileResponse) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.guruProfileResponse = guruProfileResponse;
        }
    }
    
    public static class GuruProfileData {
        public String id;
        public String nama;
        public String nip;
        public String jenis_kelamin;
        public String alamat;
        public String no_hp;
        public String username;
        public String mata_pelajaran;
        
        public GuruProfileData(String id, String nama, String nip, String jenis_kelamin, 
                              String alamat, String no_hp, String username, String mata_pelajaran) {
            this.id = id;
            this.nama = nama;
            this.nip = nip;
            this.jenis_kelamin = jenis_kelamin;
            this.alamat = alamat;
            this.no_hp = no_hp;
            this.username = username;
            this.mata_pelajaran = mata_pelajaran;
        }
    }
    
    public static GuruProfileResult getGuruProfile(String username) {
        try {
            // Validasi input
            if (username == null || username.trim().isEmpty()) {
                return new GuruProfileResult(false, "Username tidak valid", null);
            }
            
            // Trim input
            username = username.trim();
            
            // Cek apakah profile ada
            GuruProfileData profileData = MOCK_GURU_PROFILES.get(username);
            if (profileData == null) {
                return new GuruProfileResult(false, "Profile guru tidak ditemukan", null);
            }
            
            // Buat response sukses
            GuruProfileResponse response = new GuruProfileResponse();
            response.setMessage("Profile guru berhasil diambil");
            
            GuruProfileResponse.GuruProfile profile = new GuruProfileResponse.GuruProfile();
            profile.setId(profileData.id);
            profile.setNama(profileData.nama);
            profile.setNip(profileData.nip);
            profile.setJenis_kelamin(profileData.jenis_kelamin);
            profile.setAlamat(profileData.alamat);
            profile.setNo_hp(profileData.no_hp);
            profile.setUsername(profileData.username);
            profile.setMata_pelajaran(profileData.mata_pelajaran);
            
            response.setProfile(profile);
            
            return new GuruProfileResult(true, null, response);
            
        } catch (Exception e) {
            return new GuruProfileResult(false, "Error: " + e.getMessage(), null);
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

    public static UpdateResult updateGuruProfile(String username, String namaLengkap, String nip,
                                               String mataPelajaran, String jenisKelamin, String alamat, String nomorHp) {
        try {
            // Validasi input
            if (username == null || username.trim().isEmpty()) {
                return new UpdateResult(false, "Username tidak valid", null);
            }

            // Trim input
            username = username.trim();

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
}
