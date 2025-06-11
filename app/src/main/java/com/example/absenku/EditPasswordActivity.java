package com.example.absenku;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.absenku.api.ApiClient;
import com.example.absenku.api.ApiService;
import com.example.absenku.api.MockPasswordService;
import com.example.absenku.config.AppConfig;
import com.example.absenku.models.ChangePasswordRequest;
import com.example.absenku.models.ChangePasswordResponse;
import com.example.absenku.utils.SessionManager;
import com.example.absenku.utils.SweetAlertHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPasswordActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    SessionManager sessionManager;
    ApiService apiService;
    
    // UI Elements
    ImageView btnBack;
    EditText etPasswordLama, etPasswordBaru, etUlangiPasswordBaru;
    ImageView ivPasswordLamaToggle, ivPasswordBaruToggle, ivUlangiPasswordToggle;
    Button btnSimpan;

    // Password visibility states
    private boolean isPasswordLamaVisible = false;
    private boolean isPasswordBaruVisible = false;
    private boolean isUlangiPasswordVisible = false;
    
    // User info
    String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        // Initialize views
        initializeViews();
        
        // Initialize services
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();
        
        // Get user role
        userRole = sessionManager.getRole();

        // Setup listeners
        setupListeners();
    }
    
    private void initializeViews() {
        bottomNav = findViewById(R.id.bottomNav);
        btnBack = findViewById(R.id.btnBack);

        // Input fields
        etPasswordLama = findViewById(R.id.etPasswordLama);
        etPasswordBaru = findViewById(R.id.etPasswordBaru);
        etUlangiPasswordBaru = findViewById(R.id.etUlangiPasswordBaru);

        // Password toggle buttons
        ivPasswordLamaToggle = findViewById(R.id.ivPasswordLamaToggle);
        ivPasswordBaruToggle = findViewById(R.id.ivPasswordBaruToggle);
        ivUlangiPasswordToggle = findViewById(R.id.ivUlangiPasswordToggle);

        // Button
        btnSimpan = findViewById(R.id.btnSimpan);
    }
    
    private void setupListeners() {
        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
        
        // Simpan button
        if (btnSimpan != null) {
            btnSimpan.setOnClickListener(v -> performChangePassword());
        }

        // Password toggle listeners
        if (ivPasswordLamaToggle != null) {
            ivPasswordLamaToggle.setOnClickListener(v -> togglePasswordLamaVisibility());
        }
        if (ivPasswordBaruToggle != null) {
            ivPasswordBaruToggle.setOnClickListener(v -> togglePasswordBaruVisibility());
        }
        if (ivUlangiPasswordToggle != null) {
            ivUlangiPasswordToggle.setOnClickListener(v -> toggleUlangiPasswordVisibility());
        }

        // Bottom navigation
        if (bottomNav != null) {
            // Set active menu based on user role
            bottomNav.setSelectedItemId(R.id.nav_profil);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_dashboard) {
                    if ("guru".equals(userRole)) {
                        startActivity(new Intent(this, DashboardActivityGuru.class));
                    } else {
                        startActivity(new Intent(this, DashboardActivity.class));
                    }
                } else if (id == R.id.nav_absen) {
                    startActivity(new Intent(this, AbsenActivity.class));
                } else if (id == R.id.nav_profil) {
                    // Navigate back to appropriate profile
                    if ("guru".equals(userRole)) {
                        startActivity(new Intent(this, ProfilGuruActivity.class));
                    } else {
                        startActivity(new Intent(this, ProfilActivity.class));
                    }
                }
                overridePendingTransition(0, 0);
                return true;
            });
        }
    }
    
    private void performChangePassword() {
        // Get input values
        String passwordLama = etPasswordLama.getText() != null ? etPasswordLama.getText().toString().trim() : "";
        String passwordBaru = etPasswordBaru.getText() != null ? etPasswordBaru.getText().toString().trim() : "";
        String ulangiPasswordBaru = etUlangiPasswordBaru.getText() != null ? etUlangiPasswordBaru.getText().toString().trim() : "";
        
        // Validation
        if (passwordLama.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Password lama harus diisi");
            return;
        }
        
        if (passwordBaru.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Password baru harus diisi");
            return;
        }
        
        if (ulangiPasswordBaru.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Ulangi password baru harus diisi");
            return;
        }
        
        if (!passwordBaru.equals(ulangiPasswordBaru)) {
            SweetAlertHelper.showError(this, "Error", "Password baru dan ulangi password baru harus sama");
            return;
        }
        
        if (passwordBaru.length() < 6) {
            SweetAlertHelper.showError(this, "Error", "Password baru minimal 6 karakter");
            return;
        }
        
        if (passwordLama.equals(passwordBaru)) {
            SweetAlertHelper.showError(this, "Error", "Password baru harus berbeda dengan password lama");
            return;
        }
        
        // Disable button during process
        btnSimpan.setEnabled(false);
        btnSimpan.setText("Menyimpan...");
        
        // Perform password change
        if (AppConfig.USE_MOCK_PASSWORD) {
            changePasswordMock(passwordLama, passwordBaru);
        } else {
            changePasswordReal(passwordLama, passwordBaru);
        }
    }
    
    private void changePasswordMock(String passwordLama, String passwordBaru) {
        String username = sessionManager.getUsername();
        
        // Simulasi delay untuk UX yang realistis
        new android.os.Handler().postDelayed(() -> {
            try {
                MockPasswordService.PasswordChangeResult result = MockPasswordService.changePassword(username, passwordLama, passwordBaru, userRole);
                
                if (result != null && result.success) {
                    SweetAlertHelper.showSuccess(this, "Berhasil", "Password berhasil diubah", () -> {
                        // Navigate back to profile
                        navigateBackToProfile();
                    });
                } else {
                    String errorMessage = (result != null) ? result.errorMessage : "Gagal mengubah password";
                    SweetAlertHelper.showError(this, "Error", errorMessage);
                    resetButton();
                }
            } catch (Exception e) {
                SweetAlertHelper.showError(this, "Error", "Error: " + e.getMessage());
                resetButton();
            }
        }, 1000); // Delay 1 detik
    }
    
    private void changePasswordReal(String passwordLama, String passwordBaru) {
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Token tidak ditemukan");
            resetButton();
            return;
        }
        
        ChangePasswordRequest request = new ChangePasswordRequest(passwordLama, passwordBaru);
        
        Call<ChangePasswordResponse> call;
        if ("guru".equals(userRole)) {
            call = apiService.changeGuruPassword("Bearer " + token, request);
        } else {
            call = apiService.changeStudentPassword("Bearer " + token, request);
        }
        
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SweetAlertHelper.showSuccess(EditPasswordActivity.this, "Berhasil", "Password berhasil diubah", () -> {
                        navigateBackToProfile();
                    });
                } else {
                    SweetAlertHelper.showError(EditPasswordActivity.this, "Error", "Gagal mengubah password");
                    resetButton();
                }
            }
            
            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                SweetAlertHelper.showError(EditPasswordActivity.this, "Error", "Gagal terhubung ke server: " + t.getMessage());
                resetButton();
            }
        });
    }
    
    private void resetButton() {
        btnSimpan.setEnabled(true);
        btnSimpan.setText("Simpan");
    }
    
    private void navigateBackToProfile() {
        Intent intent;
        if ("guru".equals(userRole)) {
            intent = new Intent(this, ProfilGuruActivity.class);
        } else {
            intent = new Intent(this, ProfilActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void togglePasswordLamaVisibility() {
        if (isPasswordLamaVisible) {
            // Hide password
            etPasswordLama.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivPasswordLamaToggle.setImageResource(R.drawable.ic_visibility_off);
            isPasswordLamaVisible = false;
        } else {
            // Show password
            etPasswordLama.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivPasswordLamaToggle.setImageResource(R.drawable.ic_visibility);
            isPasswordLamaVisible = true;
        }
        // Move cursor to end of text
        etPasswordLama.setSelection(etPasswordLama.getText().length());
    }

    private void togglePasswordBaruVisibility() {
        if (isPasswordBaruVisible) {
            // Hide password
            etPasswordBaru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivPasswordBaruToggle.setImageResource(R.drawable.ic_visibility_off);
            isPasswordBaruVisible = false;
        } else {
            // Show password
            etPasswordBaru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivPasswordBaruToggle.setImageResource(R.drawable.ic_visibility);
            isPasswordBaruVisible = true;
        }
        // Move cursor to end of text
        etPasswordBaru.setSelection(etPasswordBaru.getText().length());
    }

    private void toggleUlangiPasswordVisibility() {
        if (isUlangiPasswordVisible) {
            // Hide password
            etUlangiPasswordBaru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivUlangiPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
            isUlangiPasswordVisible = false;
        } else {
            // Show password
            etUlangiPasswordBaru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivUlangiPasswordToggle.setImageResource(R.drawable.ic_visibility);
            isUlangiPasswordVisible = true;
        }
        // Move cursor to end of text
        etUlangiPasswordBaru.setSelection(etUlangiPasswordBaru.getText().length());
    }
}
