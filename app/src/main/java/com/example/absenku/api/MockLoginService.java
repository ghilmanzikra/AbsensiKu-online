package com.example.absenku.api;

import com.example.absenku.models.LoginResponse;
import java.util.HashMap;
import java.util.Map;

public class MockLoginService {
    
    // Mock database untuk testing
    private static final Map<String, UserData> MOCK_USERS = new HashMap<>();
    
    static {
        // Data siswa sesuai dengan API documentation
        MOCK_USERS.put("siswa1", new UserData("siswa1", "siswa1", "siswa"));
        MOCK_USERS.put("siswa2", new UserData("siswa2", "siswa2", "siswa"));
        MOCK_USERS.put("siswa3", new UserData("siswa3", "siswa3", "siswa"));
        
        // Data guru untuk testing cross-validation
        MOCK_USERS.put("guru1", new UserData("guru1", "guru1", "guru"));
        MOCK_USERS.put("guru2", new UserData("guru2", "guru2", "guru"));
        MOCK_USERS.put("guru3", new UserData("guru3", "guru3", "guru"));
    }
    
    public static class LoginResult {
        public boolean success;
        public String errorMessage;
        public LoginResponse loginResponse;
        
        public LoginResult(boolean success, String errorMessage, LoginResponse loginResponse) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.loginResponse = loginResponse;
        }
    }
    
    public static class UserData {
        public String username;
        public String password;
        public String role;
        
        public UserData(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }
    
    public static LoginResult performLogin(String username, String password) {
        try {
            // Validasi input
            if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
                return new LoginResult(false, "Username dan password harus diisi", null);
            }

            // Trim input
            username = username.trim();
            password = password.trim();

            // Validasi panjang minimum
            if (username.length() < 3) {
                return new LoginResult(false, "Username minimal 3 karakter", null);
            }

            if (password.length() < 3) {
                return new LoginResult(false, "Password minimal 3 karakter", null);
            }

            // Cek apakah user ada
            UserData userData = MOCK_USERS.get(username);
            if (userData == null) {
                return new LoginResult(false, "User not found", null);
            }

            // Cek password - gunakan MockPasswordService untuk password yang sudah diupdate
            if (!MockPasswordService.verifyPassword(username, password)) {
                return new LoginResult(false, "Invalid password", null);
            }

            // Buat response sukses
            LoginResponse response = new LoginResponse();
            response.setMessage("Login successful");
            response.setToken("mock_jwt_token_" + username + "_" + System.currentTimeMillis());
            response.setRole(userData.role);

            LoginResponse.User user = new LoginResponse.User();
            user.setUsername(userData.username);
            user.setRole(userData.role);
            response.setUser(user);

            return new LoginResult(true, null, response);

        } catch (Exception e) {
            return new LoginResult(false, "Error: " + e.getMessage(), null);
        }
    }
}
