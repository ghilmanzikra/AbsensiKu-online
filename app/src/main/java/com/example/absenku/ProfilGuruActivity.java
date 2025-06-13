package com.example.absenku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.absenku.api.ApiClient;
import com.example.absenku.api.ApiService;
import com.example.absenku.api.MockGuruProfileService;
import com.example.absenku.config.AppConfig;
import com.example.absenku.models.GuruProfileResponse;
import com.example.absenku.utils.SessionManager;
import com.example.absenku.utils.SweetAlertHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilGuruActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    SessionManager sessionManager;
    ApiService apiService;
    
    // UI Elements
    ImageView btnBack;
    TextView tvProfileName, tvProfileRole, tvUserInitial;
    TextView tvAccountUsername, tvNamaLengkap, tvNip, tvMataPelajaran, tvJenisKelamin, tvAlamat, tvNomorHp;
    Button btnEditAccount, btnEditProfile, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_guru);

        // Initialize views
        initializeViews();
        
        // Initialize services
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();

        // Setup listeners
        setupListeners();
        
        // Load profile data
        loadProfileData();
    }
    
    private void initializeViews() {
        bottomNav = findViewById(R.id.bottomNav);
        btnBack = findViewById(R.id.btnBack);
        
        // Profile header
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileRole = findViewById(R.id.tvProfileRole);
        tvUserInitial = findViewById(R.id.tvUserInitial);
        
        // Account section
        tvAccountUsername = findViewById(R.id.tvAccountUsername);
        
        // Info pribadi section
        tvNamaLengkap = findViewById(R.id.tvNamaLengkap);
        tvNip = findViewById(R.id.tvNip);
        tvMataPelajaran = findViewById(R.id.tvMataPelajaran);
        tvJenisKelamin = findViewById(R.id.tvJenisKelamin);
        tvAlamat = findViewById(R.id.tvAlamat);
        tvNomorHp = findViewById(R.id.tvNomorHp);
        
        // Buttons
        btnEditAccount = findViewById(R.id.btnEditAccount);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
    }
    
    private void setupListeners() {
        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
        
        // Edit buttons
        if (btnEditAccount != null) {
            btnEditAccount.setOnClickListener(v -> {
                // Navigate to edit password
                Intent intent = new Intent(this, EditPasswordActivity.class);
                startActivity(intent);
            });
        }
        
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                // Navigate to edit info pribadi guru
                Intent intent = new Intent(this, EditInfoPribadiGuruActivity.class);
                startActivity(intent);
            });
        }
        
        // Logout button
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> performLogout());
        }

        // Bottom navigation
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_profil); // set menu aktif

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_dashboard) {
                    startActivity(new Intent(this, DashboardActivityGuru.class));
                } else if (id == R.id.nav_absen) {
                    startActivity(new Intent(this, AbsenActivity.class));
                } else if (id == R.id.nav_profil) {
                    return true;
                }
                overridePendingTransition(0, 0);
                return true;
            });
        }
    }
    
    private void loadProfileData() {
        String username = sessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Username tidak ditemukan");
            return;
        }
        
        if (AppConfig.USE_MOCK_PROFILE) {
            loadMockProfileData(username);
        } else {
            loadRealProfileData();
        }
    }
    
    private void loadMockProfileData(String username) {
        // Simulasi delay untuk UX yang realistis
        new android.os.Handler().postDelayed(() -> {
            try {
                MockGuruProfileService.GuruProfileResult result = MockGuruProfileService.getGuruProfile(username);
                
                if (result != null && result.success && result.guruProfileResponse != null) {
                    GuruProfileResponse.GuruProfile profile = result.guruProfileResponse.getProfile();
                    if (profile != null) {
                        updateProfileUI(profile);
                    } else {
                        showProfileError("Data profile tidak valid");
                    }
                } else {
                    String errorMessage = (result != null) ? result.errorMessage : "Gagal memuat profile";
                    showProfileError(errorMessage);
                }
            } catch (Exception e) {
                showProfileError("Error: " + e.getMessage());
            }
        }, 500); // Delay 0.5 detik
    }
    
    private void loadRealProfileData() {
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Token tidak ditemukan");
            return;
        }
        
        Call<GuruProfileResponse> call = apiService.getGuruProfile("Bearer " + token);
        call.enqueue(new Callback<GuruProfileResponse>() {
            @Override
            public void onResponse(Call<GuruProfileResponse> call, Response<GuruProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GuruProfileResponse.GuruProfile profile = response.body().getProfile();
                    if (profile != null) {
                        updateProfileUI(profile);
                    } else {
                        showProfileError("Data profile tidak valid");
                    }
                } else {
                    showProfileError("Gagal memuat profile dari server");
                }
            }
            
            @Override
            public void onFailure(Call<GuruProfileResponse> call, Throwable t) {
                showProfileError("Gagal terhubung ke server: " + t.getMessage());
            }
        });
    }
    
    private void updateProfileUI(GuruProfileResponse.GuruProfile profile) {
        try {
            // Profile header
            if (tvProfileName != null) {
                tvProfileName.setText(profile.getNama() != null ? profile.getNama() : "Tidak tersedia");
            }
            if (tvProfileRole != null) {
                tvProfileRole.setText(profile.getStatusDisplay());
            }

            // Set user initial letter
            if (tvUserInitial != null && profile.getNama() != null && !profile.getNama().isEmpty()) {
                String firstLetter = profile.getNama().substring(0, 1).toUpperCase();
                tvUserInitial.setText(firstLetter);
            }
            
            // Account section
            if (tvAccountUsername != null) {
                tvAccountUsername.setText(profile.getUsername() != null ? profile.getUsername() : "Tidak tersedia");
            }
            
            // Info pribadi section
            if (tvNamaLengkap != null) {
                tvNamaLengkap.setText(profile.getNama() != null ? profile.getNama() : "Tidak tersedia");
            }
            if (tvNip != null) {
                tvNip.setText(profile.getNip() != null ? profile.getNip() : "Tidak tersedia");
            }
            if (tvMataPelajaran != null) {
                tvMataPelajaran.setText(profile.getMata_pelajaran() != null ? profile.getMata_pelajaran() : "Tidak tersedia");
            }
            if (tvJenisKelamin != null) {
                tvJenisKelamin.setText(profile.getJenisKelaminDisplay());
            }
            if (tvAlamat != null) {
                tvAlamat.setText(profile.getAlamat() != null ? profile.getAlamat() : "Tidak tersedia");
            }
            if (tvNomorHp != null) {
                tvNomorHp.setText(profile.getNo_hp() != null ? profile.getNo_hp() : "Tidak tersedia");
            }
            
        } catch (Exception e) {
            showProfileError("Error saat menampilkan data: " + e.getMessage());
        }
    }
    
    private void showProfileError(String errorMessage) {
        // Set error text untuk semua field
        if (tvProfileName != null) tvProfileName.setText("Error");
        if (tvAccountUsername != null) tvAccountUsername.setText("Error");
        if (tvNamaLengkap != null) tvNamaLengkap.setText("Error");
        if (tvNip != null) tvNip.setText("Error");
        if (tvMataPelajaran != null) tvMataPelajaran.setText("Error");
        if (tvJenisKelamin != null) tvJenisKelamin.setText("Error");
        if (tvAlamat != null) tvAlamat.setText("Error");
        if (tvNomorHp != null) tvNomorHp.setText("Error");

        SweetAlertHelper.showError(this, "Error Profile", errorMessage);
    }
    
    private void performLogout() {
        SweetAlertHelper.showConfirmation(this, "Logout", 
            "Apakah Anda yakin ingin keluar?", 
            () -> {
                try {
                    // Clear session
                    sessionManager.logout();
                    
                    SweetAlertHelper.showSuccess(this, "Logout", "Logout berhasil", () -> {
                        // Navigate back to main login activity
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                } catch (Exception e) {
                    SweetAlertHelper.showError(this, "Error", "Error saat logout: " + e.getMessage());
                }
            }, 
            null);
    }
}
