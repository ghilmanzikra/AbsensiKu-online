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
import com.example.absenku.api.MockProfileService;
import com.example.absenku.config.AppConfig;
import com.example.absenku.models.ProfileResponse;
import com.example.absenku.utils.SessionManager;
import com.example.absenku.utils.SweetAlertHelper;

public class EditInfoPribadiActivity extends AppCompatActivity {

    // UI Elements
    ImageView btnBack;
    EditText etNamaLengkap, etNis, etAlamat, etNomorHp, etKelas;
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
        setContentView(R.layout.activity_edit_info_pribadi);

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
        etNis = findViewById(R.id.etNis);
        etAlamat = findViewById(R.id.etAlamat);
        etNomorHp = findViewById(R.id.etNomorHp);
        etKelas = findViewById(R.id.etKelas);
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
                    startActivity(new Intent(this, DashboardActivity.class));
                } else if (id == R.id.nav_absen) {
                    startActivity(new Intent(this, AbsenActivity.class));
                } else if (id == R.id.nav_profil) {
                    startActivity(new Intent(this, ProfilActivity.class));
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
            MockProfileService.ProfileResult result = MockProfileService.getProfile(username, role);
            
            if (result != null && result.success && result.profileResponse != null) {
                populateFields(result.profileResponse);
            } else {
                String errorMessage = (result != null) ? result.errorMessage : "Gagal memuat profil";
                SweetAlertHelper.showError(this, "Error", errorMessage);
            }
        } catch (Exception e) {
            SweetAlertHelper.showError(this, "Error", "Error: " + e.getMessage());
        }
    }

    private void loadRealProfile(String username, String role) {
        // Implementation untuk real API jika diperlukan
        SweetAlertHelper.showInfo(this, "Info", "Real API belum diimplementasi, menggunakan mock data");
        loadMockProfile(username, role);
    }

    private void populateFields(ProfileResponse profileResponse) {
        if (profileResponse == null || profileResponse.getProfile() == null) return;

        try {
            ProfileResponse.Profile profile = profileResponse.getProfile();

            // Populate fields dengan data profil
            if (etNamaLengkap != null && profile.getNama() != null) {
                etNamaLengkap.setText(profile.getNama());
            }

            if (etNis != null && profile.getNis() != null) {
                etNis.setText(profile.getNis());
            }

            if (etAlamat != null && profile.getAlamat() != null) {
                etAlamat.setText(profile.getAlamat());
            }

            if (etNomorHp != null && profile.getNo_hp() != null) {
                etNomorHp.setText(profile.getNo_hp());
            }

            if (etKelas != null && profile.getNama_kelas() != null) {
                etKelas.setText(profile.getNama_kelas());
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

    private void performUpdateProfile() {
        if (!validateInput()) {
            return;
        }

        // Get input values
        String namaLengkap = etNamaLengkap.getText().toString().trim();
        String nis = etNis.getText().toString().trim();
        String jenisKelamin = spinnerJenisKelamin.getSelectedItem().toString();
        String alamat = etAlamat.getText().toString().trim();
        String nomorHp = etNomorHp.getText().toString().trim();
        String kelas = etKelas.getText().toString().trim();

        String username = sessionManager.getUsername();
        String role = sessionManager.getRole();

        if (AppConfig.USE_MOCK_PROFILE) {
            performMockUpdate(username, role, namaLengkap, nis, jenisKelamin, alamat, nomorHp, kelas);
        } else {
            performRealUpdate(username, role, namaLengkap, nis, jenisKelamin, alamat, nomorHp, kelas);
        }
    }

    private boolean validateInput() {
        // Validate nama lengkap
        if (etNamaLengkap.getText().toString().trim().isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Nama lengkap harus diisi");
            return false;
        }

        // Validate NIS
        if (etNis.getText().toString().trim().isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "NIS harus diisi");
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

        // Validate kelas
        if (etKelas.getText().toString().trim().isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Kelas harus diisi");
            return false;
        }

        return true;
    }

    private void performMockUpdate(String username, String role, String namaLengkap, String nis, 
                                 String jenisKelamin, String alamat, String nomorHp, String kelas) {
        try {
            MockProfileService.UpdateResult result = MockProfileService.updateProfile(
                username, role, namaLengkap, nis, jenisKelamin, alamat, nomorHp, kelas
            );
            
            if (result != null && result.success) {
                SweetAlertHelper.showSuccess(this, "Berhasil", 
                    "Info pribadi berhasil diupdate", () -> navigateBackToProfile());
            } else {
                String errorMessage = (result != null) ? result.errorMessage : "Gagal update profil";
                SweetAlertHelper.showError(this, "Error", errorMessage);
            }
        } catch (Exception e) {
            SweetAlertHelper.showError(this, "Error", "Error: " + e.getMessage());
        }
    }

    private void performRealUpdate(String username, String role, String namaLengkap, String nis, 
                                 String jenisKelamin, String alamat, String nomorHp, String kelas) {
        // Implementation untuk real API jika diperlukan
        SweetAlertHelper.showInfo(this, "Info", "Real API belum diimplementasi");
    }

    private void navigateBackToProfile() {
        try {
            Intent intent = new Intent(EditInfoPribadiActivity.this, ProfilActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            SweetAlertHelper.showError(this, "Error", 
                "Gagal kembali ke profil - " + e.getMessage());
        }
    }
}
