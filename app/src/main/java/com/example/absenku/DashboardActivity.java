package com.example.absenku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.absenku.api.ApiClient;
import com.example.absenku.api.ApiService;
import com.example.absenku.api.MockProfileService;
import com.example.absenku.config.AppConfig;
import com.example.absenku.models.ProfileResponse;
import com.example.absenku.utils.SessionManager;
import com.example.absenku.utils.SweetAlertHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    SessionManager sessionManager;

    // Profile UI elements
    TextView tvNama, tvNis, tvKelas, tvStatus, tvJenisKelamin, tvUserInitial;

    // API service
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        bottomNav = findViewById(R.id.bottomNav);
        tvNama = findViewById(R.id.tvNama);
        tvNis = findViewById(R.id.tvNis);
        tvKelas = findViewById(R.id.tvKelas);
        tvStatus = findViewById(R.id.tvStatus);
        tvJenisKelamin = findViewById(R.id.tvJenisKelamin);
        tvUserInitial = findViewById(R.id.tvUserInitial);

        // Initialize services
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();

        bottomNav.setSelectedItemId(R.id.nav_dashboard); // set menu aktif

        // Load profile data
        loadProfileData();

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                return true;
            } else if (id == R.id.nav_absen) {
                startActivity(new Intent(this, AbsenActivity.class));
            } else if (id == R.id.nav_profil) {
                startActivity(new Intent(this, ProfilActivity.class));
            }
            overridePendingTransition(0, 0);
            return true;
        });
    }

    private void loadProfileData() {
        String username = sessionManager.getUsername();
        if (username == null || username.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Username tidak ditemukan");
            return;
        }

        if (AppConfig.USE_MOCK_LOGIN) {
            loadMockProfileData(username);
        } else {
            loadRealProfileData();
        }
    }

    private void loadMockProfileData(String username) {
        // Simulasi delay untuk UX yang realistis
        new android.os.Handler().postDelayed(() -> {
            try {
                MockProfileService.ProfileResult result = MockProfileService.getStudentProfile(username);

                if (result != null && result.success && result.profileResponse != null) {
                    ProfileResponse.Profile profile = result.profileResponse.getProfile();
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

        Call<ProfileResponse> call = apiService.getStudentProfile("Bearer " + token);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse.Profile profile = response.body().getProfile();
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
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                showProfileError("Gagal terhubung ke server: " + t.getMessage());
            }
        });
    }

    private void updateProfileUI(ProfileResponse.Profile profile) {
        try {
            if (tvNama != null) tvNama.setText(profile.getNama() != null ? profile.getNama() : "Tidak tersedia");
            if (tvNis != null) tvNis.setText(profile.getNis() != null ? profile.getNis() : "Tidak tersedia");
            if (tvKelas != null) tvKelas.setText(profile.getNama_kelas() != null ? profile.getNama_kelas() : "Tidak tersedia");
            if (tvStatus != null) tvStatus.setText(profile.getStatusDisplay());
            if (tvJenisKelamin != null) tvJenisKelamin.setText(profile.getJenisKelaminDisplay());

            // Set user initial letter
            if (tvUserInitial != null && profile.getNama() != null && !profile.getNama().isEmpty()) {
                String firstLetter = profile.getNama().substring(0, 1).toUpperCase();
                tvUserInitial.setText(firstLetter);
            }
        } catch (Exception e) {
            showProfileError("Error saat menampilkan data: " + e.getMessage());
        }
    }

    private void showProfileError(String errorMessage) {
        if (tvNama != null) tvNama.setText("Error");
        if (tvNis != null) tvNis.setText("Error");
        if (tvKelas != null) tvKelas.setText("Error");
        if (tvStatus != null) tvStatus.setText("Error");
        if (tvJenisKelamin != null) tvJenisKelamin.setText("Error");

        SweetAlertHelper.showError(this, "Error Profile", errorMessage);
    }
}
