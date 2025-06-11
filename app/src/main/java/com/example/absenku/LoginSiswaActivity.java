package com.example.absenku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.absenku.api.ApiClient;
import com.example.absenku.api.ApiService;
import com.example.absenku.api.MockLoginService;
import com.example.absenku.config.AppConfig;
import com.example.absenku.models.ErrorResponse;
import com.example.absenku.models.LoginRequest;
import com.example.absenku.models.LoginResponse;
import com.example.absenku.utils.SessionManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginSiswaActivity extends AppCompatActivity {

    EditText etNis, etPassword;
    Button btnLoginSiswa;
    ProgressBar progressBar;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_siswa);

        // Initialize views
        etNis = findViewById(R.id.etNis);
        etPassword = findViewById(R.id.etPassword);
        btnLoginSiswa = findViewById(R.id.btnLoginSiswa);
        progressBar = findViewById(R.id.progressBar);

        // Initialize API service and session manager
        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getRole();
            if ("siswa".equals(role)) {
                navigateToDashboard();
                return;
            }
        }

        btnLoginSiswa.setOnClickListener(v -> {
            String username = etNis.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan Password harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(username, password);
            }
        });
    }

    private void performLogin(String username, String password) {
        // Disable button and show loading
        btnLoginSiswa.setEnabled(false);
        btnLoginSiswa.setText("Logging in...");
        progressBar.setVisibility(View.VISIBLE);

        if (AppConfig.USE_MOCK_LOGIN) {
            // Gunakan mock login untuk testing offline
            performMockLogin(username, password);
        } else {
            // Gunakan real API
            performRealApiLogin(username, password);
        }
    }

    private void performMockLogin(String username, String password) {
        // Simulasi delay untuk UX yang realistis
        new android.os.Handler().postDelayed(() -> {
            // Gunakan mock login service
            MockLoginService.LoginResult result = MockLoginService.performLogin(username, password);

            // Re-enable button and hide loading
            btnLoginSiswa.setEnabled(true);
            btnLoginSiswa.setText("Login");
            progressBar.setVisibility(View.GONE);

            if (result.success) {
                LoginResponse loginResponse = result.loginResponse;

                // Check if user is a student
                if ("siswa".equals(loginResponse.getRole())) {
                    // Save session
                    sessionManager.createLoginSession(
                        loginResponse.getToken(),
                        loginResponse.getUser().getUsername(),
                        loginResponse.getRole()
                    );

                    Toast.makeText(LoginSiswaActivity.this,
                        "Login berhasil! Selamat datang " + loginResponse.getUser().getUsername(),
                        Toast.LENGTH_SHORT).show();

                    navigateToDashboard();
                } else {
                    Toast.makeText(LoginSiswaActivity.this,
                        "Akun ini bukan akun siswa. Silakan gunakan login guru.",
                        Toast.LENGTH_LONG).show();
                }
            } else {
                // Handle error
                handleMockErrorResponse(result.errorMessage);
            }
        }, 1000); // Delay 1 detik untuk simulasi network request
    }

    private void performRealApiLogin(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);

        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // Re-enable button and hide loading
                btnLoginSiswa.setEnabled(true);
                btnLoginSiswa.setText("Login");
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Check if user is a student
                    if ("siswa".equals(loginResponse.getRole())) {
                        // Save session
                        sessionManager.createLoginSession(
                            loginResponse.getToken(),
                            loginResponse.getUser().getUsername(),
                            loginResponse.getRole()
                        );

                        Toast.makeText(LoginSiswaActivity.this,
                            "Login berhasil! Selamat datang " + loginResponse.getUser().getUsername(),
                            Toast.LENGTH_SHORT).show();

                        navigateToDashboard();
                    } else {
                        Toast.makeText(LoginSiswaActivity.this,
                            "Akun ini bukan akun siswa. Silakan gunakan login guru.",
                            Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Handle error response
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Re-enable button and hide loading
                btnLoginSiswa.setEnabled(true);
                btnLoginSiswa.setText("Login");
                progressBar.setVisibility(View.GONE);

                Toast.makeText(LoginSiswaActivity.this,
                    "Gagal terhubung ke server. Periksa koneksi internet Anda.",
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleMockErrorResponse(String errorMessage) {
        if (errorMessage != null) {
            if (errorMessage.contains("User not found")) {
                Toast.makeText(this, "Username tidak ditemukan", Toast.LENGTH_LONG).show();
            } else if (errorMessage.contains("Invalid password")) {
                Toast.makeText(this, "Password salah", Toast.LENGTH_LONG).show();
            } else if (errorMessage.contains("harus diisi")) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Username atau password salah", Toast.LENGTH_LONG).show();
        }
    }

    private void handleErrorResponse(Response<LoginResponse> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Gson gson = new Gson();
                ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);

                String errorMessage = errorResponse.getMessage();
                if (errorMessage != null) {
                    if (errorMessage.contains("User not found")) {
                        Toast.makeText(this, "Username tidak ditemukan", Toast.LENGTH_LONG).show();
                    } else if (errorMessage.contains("Invalid password")) {
                        Toast.makeText(this, "Password salah", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Username atau password salah", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Username atau password salah", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Username atau password salah", Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(LoginSiswaActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish(); // agar tombol back tidak kembali ke login
    }
}
