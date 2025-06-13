package com.example.absenku;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.absenku.adapters.AbsensiAdapter;
import com.example.absenku.api.ApiClient;
import com.example.absenku.api.ApiService;
import com.example.absenku.models.AbsensiResponse;
import com.example.absenku.models.KelasResponse;
import com.example.absenku.models.SiswaListResponse;
import com.example.absenku.models.SiswaAbsensiResponse;
import com.example.absenku.models.AbsensiRequest;
import com.example.absenku.models.GuruProfileResponse;
import com.example.absenku.utils.SessionManager;
import com.example.absenku.utils.SweetAlertHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AbsenActivity extends AppCompatActivity {

    // UI Components
    private ImageView btnBack;
    private EditText etTanggal;
    private CardView cardKelas;
    private Spinner spinnerKelas;
    private Button btnLoadAbsensi, btnSubmitAbsensi, btnClearData;
    private RecyclerView rvAbsensi;
    private BottomNavigationView bottomNav;

    // Services
    private ApiService apiService;
    private SessionManager sessionManager;

    // Data
    private String userRole;
    private String selectedDate;
    private int selectedKelasId = -1;
    private String guruId = null; // Store actual guru ID from profile
    private List<KelasResponse.KelasData> kelasList = new ArrayList<>();
    private AbsensiAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen);

        initializeServices();
        initializeViews();
        setupListeners();
        setupBottomNavigation();

        // Set default date to today
        setTodayDate();

        // Load initial data based on user role
        loadInitialData();
    }

    private void initializeServices() {
        try {
            apiService = ApiClient.getApiService();
            sessionManager = new SessionManager(this);
            userRole = sessionManager.getRole();
        } catch (Exception e) {
            SweetAlertHelper.showError(this, "Error", "Gagal menginisialisasi service");
            finish();
        }
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        etTanggal = findViewById(R.id.etTanggal);
        cardKelas = findViewById(R.id.cardKelas);
        spinnerKelas = findViewById(R.id.spinnerKelas);
        btnLoadAbsensi = findViewById(R.id.btnLoadAbsensi);
        btnSubmitAbsensi = findViewById(R.id.btnSubmitAbsensi);
        btnClearData = findViewById(R.id.btnClearData);
        rvAbsensi = findViewById(R.id.rvAbsensi);
        bottomNav = findViewById(R.id.bottomNav);

        // Setup RecyclerView
        rvAbsensi.setLayoutManager(new LinearLayoutManager(this));

        // Show/hide components based on user role
        if ("guru".equals(userRole)) {
            cardKelas.setVisibility(View.VISIBLE);
            btnSubmitAbsensi.setVisibility(View.VISIBLE);
            btnClearData.setVisibility(View.VISIBLE); // Show clear button for guru
        } else {
            cardKelas.setVisibility(View.GONE);
            btnSubmitAbsensi.setVisibility(View.GONE);
            btnClearData.setVisibility(View.GONE); // Hide clear button for siswa
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        etTanggal.setOnClickListener(v -> showDatePicker());

        btnLoadAbsensi.setOnClickListener(v -> loadAbsensiData());

        btnSubmitAbsensi.setOnClickListener(v -> submitAbsensi());

        btnClearData.setOnClickListener(v -> {
            Log.d("AbsenActivity", "Clear Data button clicked");
            clearRecyclerView();
            SweetAlertHelper.showSuccess(this, "Berhasil", "Data berhasil dibersihkan", null);
        });
    }

    private void setupBottomNavigation() {
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_dashboard) {
                    navigateToDashboard();
                    return true;
                } else if (itemId == R.id.nav_absen) {
                    // Already in absen activity
                    return true;
                } else if (itemId == R.id.nav_profil) {
                    navigateToProfile();
                    return true;
                }
                return false;
            });

            // Set current item as selected
            bottomNav.setSelectedItemId(R.id.nav_absen);
        }
    }

    private void setTodayDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        etTanggal.setText(displayFormat.format(calendar.getTime()));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = dateFormat.format(selectedCalendar.getTime());

                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                etTanggal.setText(displayFormat.format(selectedCalendar.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadInitialData() {
        if ("guru".equals(userRole)) {
            loadGuruProfile(); // Load guru profile first to get guru_id
        } else {
            // For siswa, automatically load their absensi
            loadAbsensiData();
        }
    }

    private void loadGuruProfile() {
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Token tidak ditemukan");
            return;
        }

        Log.d("AbsenActivity", "Loading guru profile to get guru_id");
        Call<GuruProfileResponse> call = apiService.getGuruProfile("Bearer " + token);
        call.enqueue(new Callback<GuruProfileResponse>() {
            @Override
            public void onResponse(Call<GuruProfileResponse> call, Response<GuruProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GuruProfileResponse profile = response.body();
                    if (profile.getProfile() != null) {
                        guruId = profile.getProfile().getId();
                        Log.d("AbsenActivity", "Got guru_id: " + guruId);
                        loadKelasList(); // Now load kelas with correct guru_id
                    } else {
                        Log.e("AbsenActivity", "Profile data is null");
                        useDefaultGuruId();
                    }
                } else {
                    Log.e("AbsenActivity", "Failed to load guru profile, using default");
                    useDefaultGuruId();
                }
            }

            @Override
            public void onFailure(Call<GuruProfileResponse> call, Throwable t) {
                Log.e("AbsenActivity", "Error loading guru profile: " + t.getMessage());
                useDefaultGuruId();
            }
        });
    }

    private void useDefaultGuruId() {
        guruId = "1"; // Default fallback
        Log.d("AbsenActivity", "Using default guru_id: " + guruId);
        loadKelasList();
    }

    private void loadKelasList() {
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Token tidak ditemukan");
            return;
        }

        if (guruId == null) {
            SweetAlertHelper.showError(this, "Error", "Guru ID tidak ditemukan");
            return;
        }
        Log.d("AbsenActivity", "Loading kelas for guru: " + guruId);

        // Try first endpoint: /api/guru/kelas
        Call<KelasResponse> call = apiService.getGuruKelas("Bearer " + token, guruId);
        call.enqueue(new Callback<KelasResponse>() {
            @Override
            public void onResponse(Call<KelasResponse> call, Response<KelasResponse> response) {
                Log.d("AbsenActivity", "API Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    KelasResponse kelasResponse = response.body();
                    Log.d("AbsenActivity", "Kelas response: " + kelasResponse.getMessage());
                    if (kelasResponse.getKelas() != null && !kelasResponse.getKelas().isEmpty()) {
                        kelasList = kelasResponse.getKelas();
                        Log.d("AbsenActivity", "Found " + kelasList.size() + " kelas");
                        setupKelasSpinner();
                    } else {
                        Log.d("AbsenActivity", "No kelas found, trying alternative endpoint");
                        tryAlternativeKelasEndpoint(token, guruId);
                    }
                } else {
                    Log.d("AbsenActivity", "First endpoint failed, trying alternative");
                    tryAlternativeKelasEndpoint(token, guruId);
                }
            }

            @Override
            public void onFailure(Call<KelasResponse> call, Throwable t) {
                Log.e("AbsenActivity", "First endpoint error: " + t.getMessage());
                tryAlternativeKelasEndpoint(token, guruId);
            }
        });
    }

    private void tryAlternativeKelasEndpoint(String token, String guruId) {
        Log.d("AbsenActivity", "Trying alternative endpoint: /api/kelas");
        Call<KelasResponse> call = apiService.getKelasByGuru("Bearer " + token, guruId);
        call.enqueue(new Callback<KelasResponse>() {
            @Override
            public void onResponse(Call<KelasResponse> call, Response<KelasResponse> response) {
                Log.d("AbsenActivity", "Alternative API Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    KelasResponse kelasResponse = response.body();
                    Log.d("AbsenActivity", "Alternative kelas response: " + kelasResponse.getMessage());
                    if (kelasResponse.getKelas() != null && !kelasResponse.getKelas().isEmpty()) {
                        kelasList = kelasResponse.getKelas();
                        Log.d("AbsenActivity", "Found " + kelasList.size() + " kelas from alternative");
                        setupKelasSpinner();
                    } else {
                        Log.d("AbsenActivity", "No kelas found in alternative endpoint");
                        tryGetAllKelas(token);
                    }
                } else {
                    Log.d("AbsenActivity", "Alternative endpoint also failed");
                    tryGetAllKelas(token);
                }
            }

            @Override
            public void onFailure(Call<KelasResponse> call, Throwable t) {
                Log.e("AbsenActivity", "Alternative endpoint error: " + t.getMessage());
                tryGetAllKelas(token);
            }
        });
    }

    private void tryGetAllKelas(String token) {
        Log.d("AbsenActivity", "Trying get all kelas endpoint");
        Call<KelasResponse> call = apiService.getAllKelas("Bearer " + token);
        call.enqueue(new Callback<KelasResponse>() {
            @Override
            public void onResponse(Call<KelasResponse> call, Response<KelasResponse> response) {
                Log.d("AbsenActivity", "All kelas API Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    KelasResponse kelasResponse = response.body();
                    Log.d("AbsenActivity", "All kelas response: " + kelasResponse.getMessage());
                    if (kelasResponse.getKelas() != null && !kelasResponse.getKelas().isEmpty()) {
                        kelasList = kelasResponse.getKelas();
                        Log.d("AbsenActivity", "Found " + kelasList.size() + " kelas from all kelas");
                        setupKelasSpinner();
                    } else {
                        Log.d("AbsenActivity", "No kelas found, using mock data");
                        setupMockKelasData();
                    }
                } else {
                    Log.d("AbsenActivity", "API failed, using mock data");
                    setupMockKelasData();
                }
            }

            @Override
            public void onFailure(Call<KelasResponse> call, Throwable t) {
                Log.e("AbsenActivity", "All kelas endpoint error: " + t.getMessage());
                Log.d("AbsenActivity", "Using mock data as fallback");
                setupMockKelasData();
            }
        });
    }

    private void setupMockKelasData() {
        Log.d("AbsenActivity", "Setting up mock kelas data");
        kelasList = new ArrayList<>();

        // Create mock kelas data
        KelasResponse.KelasData kelas1 = new KelasResponse.KelasData();
        kelas1.setId_kelas(1);
        kelas1.setNama_kelas("X-IPA");
        kelas1.setGuru_id("1");
        kelas1.setNama_guru("Joko");
        kelas1.setJumlah_siswa(30);

        KelasResponse.KelasData kelas2 = new KelasResponse.KelasData();
        kelas2.setId_kelas(2);
        kelas2.setNama_kelas("X-IPS");
        kelas2.setGuru_id("1");
        kelas2.setNama_guru("Joko");
        kelas2.setJumlah_siswa(25);

        kelasList.add(kelas1);
        kelasList.add(kelas2);

        setupKelasSpinner();
        SweetAlertHelper.showInfo(this, "Info", "Menggunakan data mock untuk testing");
    }

    private void setupKelasSpinner() {
        Log.d("AbsenActivity", "Setting up kelas spinner with " + kelasList.size() + " items");
        List<String> kelasNames = new ArrayList<>();
        for (KelasResponse.KelasData kelas : kelasList) {
            kelasNames.add(kelas.getNama_kelas());
            Log.d("AbsenActivity", "Added kelas: " + kelas.getNama_kelas());
        }

        if (kelasNames.isEmpty()) {
            kelasNames.add("Tidak ada kelas tersedia");
            Log.d("AbsenActivity", "No kelas available, adding placeholder");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kelasNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKelas.setAdapter(adapter);
        Log.d("AbsenActivity", "Spinner adapter set successfully");
    }

    private void loadAbsensiData() {
        Log.d("AbsenActivity", "loadAbsensiData() called");

        if (selectedDate == null || selectedDate.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Pilih tanggal terlebih dahulu");
            return;
        }

        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Token tidak ditemukan");
            return;
        }

        // Clear existing adapter and RecyclerView completely
        Log.d("AbsenActivity", "Clearing existing data to prevent duplication");
        clearRecyclerView();

        // Show loading state
        btnLoadAbsensi.setEnabled(false);
        btnLoadAbsensi.setText("Memuat...");

        if ("guru".equals(userRole)) {
            // Guru needs to select a class
            if (spinnerKelas.getSelectedItemPosition() < 0) {
                resetLoadButton();
                SweetAlertHelper.showError(this, "Error", "Pilih kelas terlebih dahulu");
                return;
            }
            selectedKelasId = kelasList.get(spinnerKelas.getSelectedItemPosition()).getId_kelas();
            Log.d("AbsenActivity", "Loading absensi for kelas: " + selectedKelasId + ", tanggal: " + selectedDate);
            loadAbsensiByKelas(token, selectedKelasId, selectedDate);
        } else {
            // Siswa loads their own absensi
            loadSiswaAbsensi(token);
        }
    }

    private void resetLoadButton() {
        btnLoadAbsensi.setEnabled(true);
        btnLoadAbsensi.setText("Muat Data Absensi");
    }

    private void clearRecyclerView() {
        Log.d("AbsenActivity", "Clearing RecyclerView completely");

        // Clear adapter
        if (adapter != null) {
            adapter = null;
        }

        // Set null adapter to RecyclerView
        rvAbsensi.setAdapter(null);

        // Force RecyclerView to refresh
        rvAbsensi.removeAllViews();
        rvAbsensi.getRecycledViewPool().clear();

        // Notify that data has been cleared
        Log.d("AbsenActivity", "RecyclerView cleared successfully");
    }

    private List<SiswaListResponse.SiswaData> removeDuplicateSiswa(List<SiswaListResponse.SiswaData> siswaList) {
        Log.d("AbsenActivity", "Removing duplicate siswa from API response");

        // Use LinkedHashMap to maintain order and remove duplicates by ID (String)
        Map<String, SiswaListResponse.SiswaData> uniqueSiswaMap = new LinkedHashMap<>();

        for (SiswaListResponse.SiswaData siswa : siswaList) {
            String siswaId = siswa.getId();
            if (!uniqueSiswaMap.containsKey(siswaId)) {
                uniqueSiswaMap.put(siswaId, siswa);
                Log.d("AbsenActivity", "Added unique siswa: " + siswa.getNama() + " (ID: " + siswaId + ")");
            } else {
                Log.d("AbsenActivity", "Skipped duplicate siswa: " + siswa.getNama() + " (ID: " + siswaId + ")");
            }
        }

        List<SiswaListResponse.SiswaData> uniqueList = new ArrayList<>(uniqueSiswaMap.values());
        Log.d("AbsenActivity", "Duplicate removal complete: " + siswaList.size() + " -> " + uniqueList.size());

        return uniqueList;
    }

    private List<AbsensiResponse.AbsensiData> removeDuplicateAbsensi(List<AbsensiResponse.AbsensiData> absensiList) {
        Log.d("AbsenActivity", "Removing duplicate absensi from API response");

        // Use LinkedHashMap to maintain order and remove duplicates by siswa_id (String)
        Map<String, AbsensiResponse.AbsensiData> uniqueAbsensiMap = new LinkedHashMap<>();

        for (AbsensiResponse.AbsensiData absensi : absensiList) {
            String siswaId = absensi.getId_siswa();
            if (!uniqueAbsensiMap.containsKey(siswaId)) {
                uniqueAbsensiMap.put(siswaId, absensi);
                Log.d("AbsenActivity", "Added unique absensi: " + absensi.getNama_siswa() + " (SiswaID: " + siswaId + ")");
            } else {
                Log.d("AbsenActivity", "Skipped duplicate absensi: " + absensi.getNama_siswa() + " (SiswaID: " + siswaId + ")");
            }
        }

        List<AbsensiResponse.AbsensiData> uniqueList = new ArrayList<>(uniqueAbsensiMap.values());
        Log.d("AbsenActivity", "Absensi duplicate removal complete: " + absensiList.size() + " -> " + uniqueList.size());

        return uniqueList;
    }

    private List<SiswaAbsensiResponse.AbsensiItem> removeDuplicateSiswaAbsensi(List<SiswaAbsensiResponse.AbsensiItem> absensiList) {
        Log.d("AbsenActivity", "Removing duplicate siswa absensi by tanggal (keeping latest)");

        // Use LinkedHashMap to maintain order and remove duplicates by tanggal
        // Keep the latest entry (highest ID) for each tanggal
        Map<String, SiswaAbsensiResponse.AbsensiItem> uniqueAbsensiMap = new LinkedHashMap<>();

        for (SiswaAbsensiResponse.AbsensiItem absensi : absensiList) {
            String tanggal = absensi.getTanggal();

            if (!uniqueAbsensiMap.containsKey(tanggal)) {
                // First occurrence for this tanggal
                uniqueAbsensiMap.put(tanggal, absensi);
                Log.d("AbsenActivity", "Added unique absensi: Tanggal=" + tanggal +
                      ", Status=" + absensi.getStatus() + ", ID=" + absensi.getId());
            } else {
                // Check if this entry is newer (higher ID = more recent)
                SiswaAbsensiResponse.AbsensiItem existing = uniqueAbsensiMap.get(tanggal);
                if (absensi.getId() > existing.getId()) {
                    // Replace with newer entry
                    uniqueAbsensiMap.put(tanggal, absensi);
                    Log.d("AbsenActivity", "Updated absensi for tanggal " + tanggal +
                          ": Old ID=" + existing.getId() + " -> New ID=" + absensi.getId() +
                          ", Status=" + absensi.getStatus());
                } else {
                    // Keep existing (older entry has higher ID, which shouldn't happen, but just in case)
                    Log.d("AbsenActivity", "Skipped older absensi: Tanggal=" + tanggal +
                          ", ID=" + absensi.getId() + " (keeping ID=" + existing.getId() + ")");
                }
            }
        }

        List<SiswaAbsensiResponse.AbsensiItem> uniqueList = new ArrayList<>(uniqueAbsensiMap.values());
        Log.d("AbsenActivity", "Siswa absensi duplicate removal complete: " + absensiList.size() + " -> " + uniqueList.size());

        return uniqueList;
    }

    private void loadAbsensiByKelas(String token, int kelasId, String tanggal) {
        Call<AbsensiResponse> call = apiService.getAbsensi("Bearer " + token, kelasId, tanggal);
        call.enqueue(new Callback<AbsensiResponse>() {
            @Override
            public void onResponse(Call<AbsensiResponse> call, Response<AbsensiResponse> response) {
                resetLoadButton(); // Reset button state

                if (response.isSuccessful() && response.body() != null) {
                    AbsensiResponse absensiResponse = response.body();
                    if (absensiResponse.getData() != null && !absensiResponse.getData().isEmpty()) {
                        // Show existing absensi
                        List<AbsensiResponse.AbsensiData> absensiList = absensiResponse.getData();
                        Log.d("AbsenActivity", "API Response - Raw existing absensi count: " + absensiList.size());

                        // Debug: Print all absensi from API
                        for (int i = 0; i < absensiList.size(); i++) {
                            AbsensiResponse.AbsensiData absensi = absensiList.get(i);
                            Log.d("AbsenActivity", "API Absensi[" + i + "]: " + absensi.getNama_siswa() +
                                  " (ID: " + absensi.getId() + ", SiswaID: " + absensi.getId_siswa() +
                                  ", Status: " + absensi.getStatus() + ")");
                        }

                        // Remove duplicates based on siswa_id for the same date
                        List<AbsensiResponse.AbsensiData> uniqueAbsensiList = removeDuplicateAbsensi(absensiList);
                        Log.d("AbsenActivity", "After removing duplicates: " + uniqueAbsensiList.size() + " unique absensi");

                        adapter = new AbsensiAdapter(AbsenActivity.this, uniqueAbsensiList, true);
                        rvAbsensi.setAdapter(adapter);
                        Log.d("AbsenActivity", "Existing absensi adapter set successfully with " + uniqueAbsensiList.size() + " records");
                    } else {
                        // No absensi data, load students for new absensi
                        Log.d("AbsenActivity", "No existing absensi, loading students for new absensi");
                        loadSiswaForNewAbsensi(token, kelasId);
                    }
                } else {
                    // No absensi data, load students for new absensi
                    Log.d("AbsenActivity", "API response not successful, loading students for new absensi");
                    loadSiswaForNewAbsensi(token, kelasId);
                }
            }

            @Override
            public void onFailure(Call<AbsensiResponse> call, Throwable t) {
                resetLoadButton(); // Reset button state
                Log.e("AbsenActivity", "Failed to load absensi: " + t.getMessage());
                SweetAlertHelper.showError(AbsenActivity.this, "Error", "Gagal terhubung ke server: " + t.getMessage());
            }
        });
    }

    private void loadSiswaForNewAbsensi(String token, int kelasId) {
        // Note: Button already reset in parent method, no need to reset again
        Call<SiswaListResponse> call = apiService.getSiswaByKelas("Bearer " + token, kelasId);
        call.enqueue(new Callback<SiswaListResponse>() {
            @Override
            public void onResponse(Call<SiswaListResponse> call, Response<SiswaListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SiswaListResponse.SiswaData> siswaList = response.body().getSiswa();
                    Log.d("AbsenActivity", "API Response - Raw siswa count: " + siswaList.size());

                    // Debug: Print all siswa from API
                    for (int i = 0; i < siswaList.size(); i++) {
                        SiswaListResponse.SiswaData siswa = siswaList.get(i);
                        Log.d("AbsenActivity", "API Siswa[" + i + "]: " + siswa.getNama() + " (ID: " + siswa.getId() + ", NIS: " + siswa.getNis() + ")");
                    }

                    // Remove duplicates based on ID
                    List<SiswaListResponse.SiswaData> uniqueSiswaList = removeDuplicateSiswa(siswaList);
                    Log.d("AbsenActivity", "After removing duplicates: " + uniqueSiswaList.size() + " unique siswa");

                    // Create fresh adapter for new absensi
                    adapter = AbsensiAdapter.createForNewAbsensi(AbsenActivity.this, uniqueSiswaList);
                    rvAbsensi.setAdapter(adapter);
                    Log.d("AbsenActivity", "New absensi adapter set successfully with " + uniqueSiswaList.size() + " siswa");
                } else {
                    Log.e("AbsenActivity", "Failed to load siswa data - Response code: " + response.code());
                    SweetAlertHelper.showError(AbsenActivity.this, "Error", "Gagal memuat data siswa");
                }
            }

            @Override
            public void onFailure(Call<SiswaListResponse> call, Throwable t) {
                Log.e("AbsenActivity", "Error loading siswa: " + t.getMessage());
                SweetAlertHelper.showError(AbsenActivity.this, "Error", "Gagal terhubung ke server: " + t.getMessage());
            }
        });
    }

    private void loadSiswaAbsensi(String token) {
        // Clear existing data to prevent duplication
        Log.d("AbsenActivity", "Clearing existing data before loading siswa absensi");
        clearRecyclerView();

        Call<SiswaAbsensiResponse> call = apiService.getSiswaAbsensi("Bearer " + token);
        call.enqueue(new Callback<SiswaAbsensiResponse>() {
            @Override
            public void onResponse(Call<SiswaAbsensiResponse> call, Response<SiswaAbsensiResponse> response) {
                resetLoadButton(); // Reset button state

                if (response.isSuccessful() && response.body() != null) {
                    SiswaAbsensiResponse absensiResponse = response.body();
                    if (absensiResponse.getAbsensi() != null && !absensiResponse.getAbsensi().isEmpty()) {
                        List<SiswaAbsensiResponse.AbsensiItem> absensiList = absensiResponse.getAbsensi();
                        Log.d("AbsenActivity", "API Response - Raw siswa absensi count: " + absensiList.size());

                        // Debug: Print all absensi from API
                        for (int i = 0; i < absensiList.size(); i++) {
                            SiswaAbsensiResponse.AbsensiItem absensi = absensiList.get(i);
                            Log.d("AbsenActivity", "API Absensi[" + i + "]: Tanggal=" + absensi.getTanggal() +
                                  ", Status=" + absensi.getStatus() + ", ID=" + absensi.getId() +
                                  ", Waktu=" + absensi.getFormattedTime());
                        }

                        // Remove duplicates based on tanggal (keep latest by created_at/id)
                        List<SiswaAbsensiResponse.AbsensiItem> uniqueAbsensiList = removeDuplicateSiswaAbsensi(absensiList);
                        Log.d("AbsenActivity", "After removing duplicates: " + uniqueAbsensiList.size() + " unique absensi");

                        // Create fresh adapter for siswa history
                        adapter = AbsensiAdapter.createForSiswaHistory(AbsenActivity.this, uniqueAbsensiList);
                        rvAbsensi.setAdapter(adapter);
                        Log.d("AbsenActivity", "Siswa history adapter set successfully with " + uniqueAbsensiList.size() + " records");
                    } else {
                        Log.d("AbsenActivity", "No absensi data found for siswa");
                        SweetAlertHelper.showInfo(AbsenActivity.this, "Info", "Belum ada data absensi");
                    }
                } else {
                    Log.e("AbsenActivity", "Failed to load siswa absensi data - Response code: " + response.code());
                    SweetAlertHelper.showError(AbsenActivity.this, "Error", "Gagal memuat data absensi");
                }
            }

            @Override
            public void onFailure(Call<SiswaAbsensiResponse> call, Throwable t) {
                resetLoadButton(); // Reset button state
                Log.e("AbsenActivity", "Error loading siswa absensi: " + t.getMessage());
                SweetAlertHelper.showError(AbsenActivity.this, "Error", "Gagal terhubung ke server: " + t.getMessage());
            }
        });
    }

    private void submitAbsensi() {
        Log.d("AbsenActivity", "Submit absensi clicked");

        if (!"guru".equals(userRole)) {
            SweetAlertHelper.showError(this, "Error", "Hanya guru yang dapat submit absensi");
            return;
        }

        if (adapter == null) {
            SweetAlertHelper.showError(this, "Error", "Adapter tidak ditemukan");
            return;
        }

        if (selectedKelasId == -1) {
            SweetAlertHelper.showError(this, "Error", "Pilih kelas terlebih dahulu");
            return;
        }

        if (selectedDate == null || selectedDate.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Pilih tanggal terlebih dahulu");
            return;
        }

        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Token tidak ditemukan");
            return;
        }

        // Check adapter mode and prepare data accordingly
        List<AbsensiRequest.AbsensiItem> absensiItems = new ArrayList<>();

        if (adapter.getSiswaList() != null && !adapter.getSiswaList().isEmpty()) {
            // Mode: Creating new absensi (MODE_GURU_CREATE)
            Log.d("AbsenActivity", "Submitting new absensi for " + adapter.getSiswaList().size() + " students");
            List<SiswaListResponse.SiswaData> siswaList = adapter.getSiswaList();
            List<String> statusList = adapter.getAllStatusSelections();

            for (int i = 0; i < siswaList.size(); i++) {
                SiswaListResponse.SiswaData siswa = siswaList.get(i);
                String status = statusList.get(i);
                absensiItems.add(new AbsensiRequest.AbsensiItem(siswa.getId(), status));
                Log.d("AbsenActivity", "Student: " + siswa.getNama() + " - Status: " + status);
            }
        } else if (adapter.getAbsensiList() != null && !adapter.getAbsensiList().isEmpty()) {
            // Mode: Updating existing absensi (MODE_GURU_VIEW)
            Log.d("AbsenActivity", "Updating existing absensi for " + adapter.getAbsensiList().size() + " students");
            List<AbsensiResponse.AbsensiData> absensiList = adapter.getAbsensiList();
            List<String> statusList = adapter.getAllStatusSelections();

            for (int i = 0; i < absensiList.size(); i++) {
                AbsensiResponse.AbsensiData absensi = absensiList.get(i);
                String status = statusList.get(i);
                absensiItems.add(new AbsensiRequest.AbsensiItem(absensi.getId_siswa(), status));
                Log.d("AbsenActivity", "Student: " + absensi.getNama_siswa() + " - Status: " + status);
            }
        } else {
            SweetAlertHelper.showError(this, "Error", "Data siswa tidak ditemukan. Silakan muat data terlebih dahulu.");
            return;
        }

        if (absensiItems.isEmpty()) {
            SweetAlertHelper.showError(this, "Error", "Tidak ada data absensi untuk disimpan");
            return;
        }

        if (guruId == null) {
            SweetAlertHelper.showError(this, "Error", "Guru ID tidak ditemukan");
            return;
        }

        AbsensiRequest request = new AbsensiRequest(selectedKelasId, selectedDate, guruId, absensiItems);

        Log.d("AbsenActivity", "Submitting absensi request: kelas=" + selectedKelasId + ", tanggal=" + selectedDate + ", guru=" + guruId);

        // Show loading
        btnSubmitAbsensi.setEnabled(false);
        btnSubmitAbsensi.setText("Menyimpan...");

        Call<AbsensiResponse> call = apiService.submitAbsensi("Bearer " + token, request);
        call.enqueue(new Callback<AbsensiResponse>() {
            @Override
            public void onResponse(Call<AbsensiResponse> call, Response<AbsensiResponse> response) {
                btnSubmitAbsensi.setEnabled(true);
                btnSubmitAbsensi.setText("Simpan Absensi");

                Log.d("AbsenActivity", "Submit response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AbsenActivity", "Submit successful: " + response.body().getInserted() + " records");
                    SweetAlertHelper.showSuccess(AbsenActivity.this, "Berhasil",
                        "Absensi berhasil disimpan untuk " + response.body().getInserted() + " siswa",
                        () -> {
                            // Reload data to show submitted absensi
                            loadAbsensiData();
                        });
                } else {
                    Log.e("AbsenActivity", "Submit failed with code: " + response.code());
                    SweetAlertHelper.showError(AbsenActivity.this, "Error", "Gagal menyimpan absensi");
                }
            }

            @Override
            public void onFailure(Call<AbsensiResponse> call, Throwable t) {
                btnSubmitAbsensi.setEnabled(true);
                btnSubmitAbsensi.setText("Simpan Absensi");
                Log.e("AbsenActivity", "Submit error: " + t.getMessage());
                SweetAlertHelper.showError(AbsenActivity.this, "Error", "Gagal terhubung ke server: " + t.getMessage());
            }
        });
    }

    private void navigateToDashboard() {
        Intent intent;
        if ("guru".equals(userRole)) {
            intent = new Intent(this, DashboardActivityGuru.class);
        } else {
            intent = new Intent(this, DashboardActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void navigateToProfile() {
        Intent intent;
        if ("guru".equals(userRole)) {
            intent = new Intent(this, ProfilGuruActivity.class);
        } else {
            intent = new Intent(this, ProfilActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
