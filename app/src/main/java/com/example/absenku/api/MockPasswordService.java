package com.example.absenku.api;

import java.util.HashMap;
import java.util.Map;

public class MockPasswordService {
    
    // Mock database untuk menyimpan password users
    private static final Map<String, String> MOCK_PASSWORDS = new HashMap<>();
    
    static {
        // Initialize mock passwords untuk siswa
        MOCK_PASSWORDS.put("siswa1", "siswa1");
        MOCK_PASSWORDS.put("siswa2", "siswa2");
        MOCK_PASSWORDS.put("siswa3", "siswa3");
        
        // Initialize mock passwords untuk guru
        MOCK_PASSWORDS.put("guru1", "guru1");
        MOCK_PASSWORDS.put("guru2", "guru2");
        MOCK_PASSWORDS.put("guru3", "guru3");
    }
    
    // Result class untuk response
    public static class PasswordChangeResult {
        public boolean success;
        public String errorMessage;
        public String message;
        
        public PasswordChangeResult(boolean success, String errorMessage, String message) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.message = message;
        }
    }
    
    /**
     * Change password untuk user
     * @param username Username user
     * @param oldPassword Password lama
     * @param newPassword Password baru
     * @param userRole Role user (siswa/guru)
     * @return PasswordChangeResult
     */
    public static PasswordChangeResult changePassword(String username, String oldPassword, String newPassword, String userRole) {
        try {
            // Validasi input
            if (username == null || username.trim().isEmpty()) {
                return new PasswordChangeResult(false, "Username tidak valid", null);
            }
            
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                return new PasswordChangeResult(false, "Password lama tidak boleh kosong", null);
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return new PasswordChangeResult(false, "Password baru tidak boleh kosong", null);
            }
            
            // Trim input
            username = username.trim();
            oldPassword = oldPassword.trim();
            newPassword = newPassword.trim();
            
            // Validasi password baru
            if (newPassword.length() < 6) {
                return new PasswordChangeResult(false, "Password baru minimal 6 karakter", null);
            }
            
            if (oldPassword.equals(newPassword)) {
                return new PasswordChangeResult(false, "Password baru harus berbeda dengan password lama", null);
            }
            
            // Cek apakah user ada
            if (!MOCK_PASSWORDS.containsKey(username)) {
                return new PasswordChangeResult(false, "User tidak ditemukan", null);
            }
            
            // Cek password lama
            String currentPassword = MOCK_PASSWORDS.get(username);
            if (!currentPassword.equals(oldPassword)) {
                return new PasswordChangeResult(false, "Password lama salah", null);
            }
            
            // Validasi role (optional, untuk keamanan tambahan)
            if (userRole != null) {
                if ("siswa".equals(userRole) && !username.startsWith("siswa")) {
                    return new PasswordChangeResult(false, "Role tidak sesuai dengan username", null);
                }
                if ("guru".equals(userRole) && !username.startsWith("guru")) {
                    return new PasswordChangeResult(false, "Role tidak sesuai dengan username", null);
                }
            }
            
            // Update password
            MOCK_PASSWORDS.put(username, newPassword);
            
            return new PasswordChangeResult(true, null, "Password berhasil diubah");
            
        } catch (Exception e) {
            return new PasswordChangeResult(false, "Error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Verify password untuk user (untuk keperluan testing)
     * @param username Username user
     * @param password Password untuk diverifikasi
     * @return true jika password benar
     */
    public static boolean verifyPassword(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        
        String storedPassword = MOCK_PASSWORDS.get(username.trim());
        return storedPassword != null && storedPassword.equals(password.trim());
    }
    
    /**
     * Get current password untuk user (untuk keperluan testing)
     * @param username Username user
     * @return Current password atau null jika user tidak ada
     */
    public static String getCurrentPassword(String username) {
        if (username == null) {
            return null;
        }
        return MOCK_PASSWORDS.get(username.trim());
    }
    
    /**
     * Reset password ke default (untuk keperluan testing)
     * @param username Username user
     * @return true jika berhasil reset
     */
    public static boolean resetPasswordToDefault(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        username = username.trim();
        
        // Reset ke password default (sama dengan username)
        if (MOCK_PASSWORDS.containsKey(username)) {
            MOCK_PASSWORDS.put(username, username);
            return true;
        }
        
        return false;
    }
    
    /**
     * Get all users (untuk keperluan debugging)
     * @return Map of username -> password
     */
    public static Map<String, String> getAllUsers() {
        return new HashMap<>(MOCK_PASSWORDS);
    }
}
