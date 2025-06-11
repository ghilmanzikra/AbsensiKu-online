package com.example.absenku;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.absenku.utils.SweetAlertHelper;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginSiswaActivity extends AppCompatActivity {

    EditText etNis, etPassword;
    Button btnLoginSiswa;
    ProgressBar progressBar;
    ImageView ivPasswordToggle;
    private boolean isPasswordVisible = false;

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
        ivPasswordToggle = findViewById(R.id.ivPasswordToggle);

        // Validate views
        if (etNis == null || etPassword == null || btnLoginSiswa == null) {
            SweetAlertHelper.showError(this, "Error", "Layout tidak ditemukan");
            finish();
            return;
        }

        // Initialize API service and session manager
        try {
            apiService = ApiClient.getApiService();
            sessionManager = new SessionManager(this);
        } catch (Exception e) {
            SweetAlertHelper.showError(this, "Error", "Gagal menginisialisasi service");
            finish();
            return;
        }

        // Note: Auto-login disabled - always show login form
        // User harus login manual setiap kali membuka LoginSiswaActivity

        // Clear any existing session to force fresh login
        // Uncomment baris di bawah jika ingin clear session setiap kali buka login
        sessionManager.logout(); // Force clear session untuk fresh login

        // Set up click listener
        btnLoginSiswa.setOnClickListener(v -> attemptLogin());

        // Set up password toggle
        if (ivPasswordToggle != null) {
            ivPasswordToggle.setOnClickListener(v -> togglePasswordVisibility());
        }

        // Set up keyboard handling
        etNis.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                etPassword.requestFocus();
                return true;
            }
            return false;
        });

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                attemptLogin();
                return true;
            }
            return false;
        });
    }

    private void attemptLogin() {
        if (validateInput()) {
            String username = etNis.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            performLogin(username, password);
        }
    }

    private boolean validateInput() {
        String username = etNis.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Reset error states
        etNis.setError(null);
        etPassword.setError(null);

        if (username.isEmpty()) {
            etNis.setError("Username tidak boleh kosong");
            etNis.requestFocus();
            return false;
        }

        if (username.length() < 3) {
            etNis.setError("Username minimal 3 karakter");
            etNis.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 3) {
            etPassword.setError("Password minimal 3 karakter");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void performLogin(String username, String password) {
        // Show loading state
        setLoadingState(true);

        // Clear any previous errors
        etNis.setError(null);
        etPassword.setError(null);

        if (AppConfig.USE_MOCK_LOGIN) {
            // Gunakan mock login untuk testing offline
            performMockLogin(username, password);
        } else {
            // Gunakan real API
            performRealApiLogin(username, password);
        }
    }

    private void setLoadingState(boolean isLoading) {
        btnLoginSiswa.setEnabled(!isLoading);
        btnLoginSiswa.setText(isLoading ? "Logging in..." : "Login");

        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }

        // Disable input fields during loading
        etNis.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
    }

    private void performMockLogin(String username, String password) {
        // Simulasi delay untuk UX yang realistis
        new android.os.Handler().postDelayed(() -> {
            try {
                // Gunakan mock login service
                MockLoginService.LoginResult result = MockLoginService.performLogin(username, password);

                // Hide loading state
                setLoadingState(false);

                if (result != null && result.success) {
                    LoginResponse loginResponse = result.loginResponse;

                    if (loginResponse != null && loginResponse.getUser() != null) {
                        // Check if user is a student
                        if ("siswa".equals(loginResponse.getRole())) {
                            // Save session
                            sessionManager.createLoginSession(
                                loginResponse.getToken(),
                                loginResponse.getUser().getUsername(),
                                loginResponse.getRole()
                            );

                            SweetAlertHelper.showLoginSuccess(LoginSiswaActivity.this,
                                loginResponse.getUser().getUsername(),
                                () -> navigateToDashboard());
                        } else {
                            SweetAlertHelper.showWarning(LoginSiswaActivity.this,
                                "Akses Ditolak",
                                "Akun ini bukan akun siswa. Silakan gunakan login guru.");
                        }
                    } else {
                        SweetAlertHelper.showError(LoginSiswaActivity.this,
                            "Error", "Data login tidak valid");
                    }
                } else {
                    // Handle error
                    String errorMessage = (result != null) ? result.errorMessage : "Login gagal";
                    handleMockErrorResponse(errorMessage);
                }
            } catch (Exception e) {
                setLoadingState(false);
                SweetAlertHelper.showError(LoginSiswaActivity.this,
                    "Error", e.getMessage());
            }
        }, 1000); // Delay 1 detik untuk simulasi network request
    }

    private void performRealApiLogin(String username, String password) {
        try {
            LoginRequest loginRequest = new LoginRequest(username, password);

            Call<LoginResponse> call = apiService.login(loginRequest);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    // Hide loading state
                    setLoadingState(false);

                    if (response.isSuccessful() && response.body() != null) {
                        LoginResponse loginResponse = response.body();

                        if (loginResponse.getUser() != null) {
                            // Check if user is a student
                            if ("siswa".equals(loginResponse.getRole())) {
                                // Save session
                                sessionManager.createLoginSession(
                                    loginResponse.getToken(),
                                    loginResponse.getUser().getUsername(),
                                    loginResponse.getRole()
                                );

                                SweetAlertHelper.showLoginSuccess(LoginSiswaActivity.this,
                                    loginResponse.getUser().getUsername(),
                                    () -> navigateToDashboard());
                            } else {
                                SweetAlertHelper.showWarning(LoginSiswaActivity.this,
                                    "Akses Ditolak",
                                    "Akun ini bukan akun siswa. Silakan gunakan login guru.");
                            }
                        } else {
                            SweetAlertHelper.showError(LoginSiswaActivity.this,
                                "Error", "Data user tidak valid");
                        }
                    } else {
                        // Handle error response
                        handleErrorResponse(response);
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    // Hide loading state
                    setLoadingState(false);

                    SweetAlertHelper.showNetworkError(LoginSiswaActivity.this);
                }
            });
        } catch (Exception e) {
            setLoadingState(false);
            SweetAlertHelper.showError(this, "Error", e.getMessage());
        }
    }

    private void handleMockErrorResponse(String errorMessage) {
        if (errorMessage != null) {
            if (errorMessage.contains("User not found")) {
                SweetAlertHelper.showLoginError(this, "Username tidak ditemukan");
            } else if (errorMessage.contains("Invalid password")) {
                SweetAlertHelper.showLoginError(this, "Password salah");
            } else if (errorMessage.contains("harus diisi")) {
                SweetAlertHelper.showValidationError(this, errorMessage);
            } else {
                SweetAlertHelper.showLoginError(this, errorMessage);
            }
        } else {
            SweetAlertHelper.showLoginError(this, "Username atau password salah");
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
                        SweetAlertHelper.showLoginError(this, "Username tidak ditemukan");
                    } else if (errorMessage.contains("Invalid password")) {
                        SweetAlertHelper.showLoginError(this, "Password salah");
                    } else {
                        SweetAlertHelper.showLoginError(this, errorMessage);
                    }
                } else {
                    SweetAlertHelper.showLoginError(this, "Username atau password salah");
                }
            } else {
                SweetAlertHelper.showLoginError(this, "Username atau password salah");
            }
        } catch (Exception e) {
            SweetAlertHelper.showLoginError(this, "Username atau password salah");
        }
    }

    private void navigateToDashboard() {
        try {
            Intent intent = new Intent(LoginSiswaActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // agar tombol back tidak kembali ke login
        } catch (Exception e) {
            SweetAlertHelper.showError(this, "Error",
                "Gagal membuka dashboard - " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Reset loading state if activity is destroyed
        if (btnLoginSiswa != null) {
            setLoadingState(false);
        }
    }

    @Override
    public void onBackPressed() {
        // If loading, don't allow back press
        if (btnLoginSiswa != null && !btnLoginSiswa.isEnabled()) {
            SweetAlertHelper.showInfo(this, "Mohon Tunggu", "Proses login sedang berlangsung");
            return;
        }
        super.onBackPressed();
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
            isPasswordVisible = false;
        } else {
            // Show password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.ic_visibility);
            isPasswordVisible = true;
        }

        // Move cursor to end of text
        etPassword.setSelection(etPassword.getText().length());
    }
}
