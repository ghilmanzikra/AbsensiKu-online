package com.example.absenku;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.absenku.api.ApiClient;
import com.example.absenku.api.ApiService;
import com.example.absenku.api.MockGuruProfileService;
import com.example.absenku.config.AppConfig;
import com.example.absenku.models.GuruProfileResponse;
import com.example.absenku.models.UpdateProfileRequest;
import com.example.absenku.models.UpdateProfileResponse;
import com.example.absenku.utils.SessionManager;
import com.example.absenku.utils.SweetAlertHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditInfoPribadiGuruActivity extends AppCompatActivity {

    // UI Elements
    ImageView btnBack;
    EditText etNamaLengkap, etNip, etMataPelajaran, etAlamat, etNomorHp;
    Spinner spinnerJenisKelamin;
    Button btnSimpan;
    BottomNavigationView bottomNav;

    // Services
    private ApiService apiService;
    private SessionManager sessionManager;

    // Data
    private String[] jenisKelaminOptions = {"Laki-laki", "Perempuan"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info_pribadi_guru);

        initializeViews();
        initializeServices();
        setupSpinner();
        setupClickListeners();
        setupBottomNavigation();
        loadCurrentProfile();
    }

    private void initializeViews() {
        bottomNav = findViewById(R.id.bottomNav);
        btnBack = findViewById(R.id.btnBack);
        
        // Input fields
        etNamaLengkap = findViewById(R.id.etNamaLengkap);
        etNip = findViewById(R.id.etNip);
        etMataPelajaran = findViewById(R.id.etMataPelajaran);
        etAlamat = findViewById(R.id.etAlamat);
        etNomorHp = findViewById(R.id.etNomorHp);
        spinnerJenisKelamin = findViewById(R.id.spinnerJenisKelamin);
        
        // Button
        btnSimpan = findViewById(R.id.btnSimpan);
    }

    private void initializeServices() {
        try {
            apiService = ApiClient.getApiService();
            sessionManager = new SessionManager(this);
        } catch (Exception e) {
            SweetAlertHelper.showError(this, "Error", "Gagal menginisialisasi service");
            finish();
        }
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, 
            R.layout.spinner_item, 
            jenisKelaminOptions
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerJenisKelamin.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> navigateBackToProfile());
        }

        // Simpan button
        if (btnSimpan != null) {
            btnSimpan.setOnClickListener(v -> performUpdateProfile());
        }
    }

    private void setupBottomNavigation() {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_profil); // set menu aktif

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_dashboard) {
                    startActivity(new Intent(this, DashboardActivityGuru.class));
                } else if (id == R.id.nav_absen) {
                    startActivity(new Intent(this, AbsenActivity.class));
                } else if (id == R.id.nav_profil) {
                    startActivity(new Intent(this, ProfilGuruActivity.class));
                }
                overridePendingTransition(0, 0);
                return true;
            });
        }
    }

    private void loadCurrentProfile() {
        if (!sessionManager.isLoggedIn()) {
            SweetAlertHelper.showError(this, "Error", "Session tidak valid");
            finish();
            return;
        }

        String username = sessionManager.getUsername();
        String role = sessionManager.getRole();

        if (AppConfig.USE_MOCK_PROFILE) {
            loadMockProfile(username, role);
        } else {
            loadRealProfile(username, role);
        }
    }

    private void loadMockProfile(String username, String role) {
        try {
            MockGuruProfileService.GuruProfileResult result = MockGuruProfileService.getGuruProfile(username);

            if (result != null && result.success && result.guruProfileResponse != null) {
                populateFields(result.guruProfileResponse);
            } else {
                String errorMessage = (result != null) ? result.errorMessage : "Gagal memuat profil guru";
                SweetAlertHelper.showError(this, "Error", errorMessage);
            }
        } catch (Exception e) {
            SweetAlertHelper.showError(this, "Error", "Error: " + e.getMessage());
        }
    }

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

    private void loadRealProfile(String username, String role) {
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
                    populateFields(response.body());
                } else {
                    SweetAlertHelper.showError(EditInfoPribadiGuruActivity.this, "Error", "Gagal memuat profil");
                }
            }

            @Override
            public void onFailure(Call<GuruProfileResponse> call, Throwable t) {
                SweetAlertHelper.showError(EditInfoPribadiGuruActivity.this, "Error", "Gagal terhubung ke server: " + t.getMessage());
            }
        });
    }

    private void performUpdateProfile() {
        if (!validateInput()) {
            return;
        }

        // Get input values
        String namaLengkap = etNamaLengkap.getText().toString().trim();
        String nip = etNip.getText().toString().trim();
        String mataPelajaran = etMataPelajaran.getText().toString().trim();
        String jenisKelamin = spinnerJenisKelamin.getSelectedItem().toString();
        String alamat = etAlamat.getText().toString().trim();
        String nomorHp = etNomorHp.getText().toString().trim();

        String username = sessionManager.getUsername();
        String role = sessionManager.getRole();

        if (AppConfig.USE_MOCK_PROFILE) {
            performMockUpdate(username, role, namaLengkap, nip, mataPelajaran, jenisKelamin, alamat, nomorHp);
        } else {
            performRealUpdate(username, role, namaLengkap, nip, mataPelajaran, jenisKelamin, alamat, nomorHp);
        }
    }

    private boolean validateInput() {
        // Validate nama lengkap
        if (etNamaLengkap.getText().toString().trim().isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Nama lengkap harus diisi");
            return false;
        }

        // Validate NIP
        if (etNip.getText().toString().trim().isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "NIP harus diisi");
            return false;
        }

        // Validate mata pelajaran
        if (etMataPelajaran.getText().toString().trim().isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Mata pelajaran harus diisi");
            return false;
        }

        // Validate alamat
        if (etAlamat.getText().toString().trim().isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Alamat harus diisi");
            return false;
        }

        // Validate nomor HP
        String nomorHp = etNomorHp.getText().toString().trim();
        if (nomorHp.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Nomor HP harus diisi");
            return false;
        }

        if (nomorHp.length() < 10) {
            SweetAlertHelper.showError(this, "Error", "Nomor HP minimal 10 digit");
            return false;
        }

        return true;
    }

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

    private void resetButton() {
        btnSimpan.setEnabled(true);
        btnSimpan.setText("Simpan");
    }

    private void performRealUpdate(String username, String role, String namaLengkap, String nip,
                                 String mataPelajaran, String jenisKelamin, String alamat, String nomorHp) {
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Token tidak ditemukan");
            return;
        }

        UpdateProfileRequest request = new UpdateProfileRequest(namaLengkap, alamat, nomorHp);

        Call<UpdateProfileResponse> call = apiService.updateGuruProfile("Bearer " + token, request);
        call.enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SweetAlertHelper.showSuccess(EditInfoPribadiGuruActivity.this, "Berhasil", "Profil berhasil diperbarui", () -> {
                        navigateBackToProfile();
                    });
                } else {
                    SweetAlertHelper.showError(EditInfoPribadiGuruActivity.this, "Error", "Gagal memperbarui profil");
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                SweetAlertHelper.showError(EditInfoPribadiGuruActivity.this, "Error", "Gagal terhubung ke server: " + t.getMessage());
            }
        });
    }

    private void navigateBackToProfile() {
        try {
            Intent intent = new Intent(EditInfoPribadiGuruActivity.this, ProfilGuruActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            SweetAlertHelper.showError(this, "Error", 
                "Gagal kembali ke profil - " + e.getMessage());
        }
    }
}
