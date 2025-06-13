package com.example.absenku.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.absenku.R;
import com.example.absenku.models.AbsensiResponse;
import com.example.absenku.models.SiswaListResponse;
import com.example.absenku.models.SiswaAbsensiResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbsensiAdapter extends RecyclerView.Adapter<AbsensiAdapter.ViewHolder> {

    public static final int MODE_GURU_VIEW = 1;
    public static final int MODE_GURU_CREATE = 2;
    public static final int MODE_SISWA_HISTORY = 3;

    private Context context;
    private List<AbsensiResponse.AbsensiData> absensiList;
    private List<SiswaListResponse.SiswaData> siswaList;
    private List<SiswaAbsensiResponse.AbsensiItem> siswaAbsensiList;
    private int adapterMode;
    private String[] statusOptions = {"hadir", "sakit", "izin", "tidak ada keterangan"};
    private Map<Integer, String> statusSelections = new HashMap<>();

    // Constructor for viewing existing absensi (guru)
    public AbsensiAdapter(Context context, List<AbsensiResponse.AbsensiData> absensiList, boolean isGuruMode) {
        this.context = context;
        this.absensiList = absensiList;
        this.adapterMode = MODE_GURU_VIEW;
    }

    // Static factory methods to avoid constructor conflicts
    public static AbsensiAdapter createForNewAbsensi(Context context, List<SiswaListResponse.SiswaData> siswaList) {
        AbsensiAdapter adapter = new AbsensiAdapter();
        adapter.context = context;
        adapter.siswaList = siswaList;
        adapter.adapterMode = MODE_GURU_CREATE;
        // Initialize default status for all students
        for (int i = 0; i < siswaList.size(); i++) {
            adapter.statusSelections.put(i, "hadir");
        }
        return adapter;
    }

    public static AbsensiAdapter createForSiswaHistory(Context context, List<SiswaAbsensiResponse.AbsensiItem> siswaAbsensiList) {
        AbsensiAdapter adapter = new AbsensiAdapter();
        adapter.context = context;
        adapter.siswaAbsensiList = siswaAbsensiList;
        adapter.adapterMode = MODE_SISWA_HISTORY;
        return adapter;
    }

    // Private constructor for factory methods
    private AbsensiAdapter() {
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_absensi, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (adapterMode) {
            case MODE_GURU_VIEW:
                // Viewing existing absensi (guru)
                if (absensiList != null && position < absensiList.size()) {
                    AbsensiResponse.AbsensiData absensi = absensiList.get(position);
                    holder.tvNamaSiswa.setText(absensi.getNama_siswa());
                    holder.tvNisSiswa.setText("NIS: " + absensi.getNis());

                    holder.spinnerStatus.setVisibility(View.VISIBLE);
                    holder.tvStatus.setVisibility(View.GONE);
                    setupStatusSpinner(holder.spinnerStatus, absensi.getStatus(), position);
                }
                break;

            case MODE_GURU_CREATE:
                // Creating new absensi (guru only)
                if (siswaList != null && position < siswaList.size()) {
                    SiswaListResponse.SiswaData siswa = siswaList.get(position);
                    holder.tvNamaSiswa.setText(siswa.getNama());
                    holder.tvNisSiswa.setText("NIS: " + siswa.getNis());

                    holder.spinnerStatus.setVisibility(View.VISIBLE);
                    holder.tvStatus.setVisibility(View.GONE);
                    setupStatusSpinner(holder.spinnerStatus, statusSelections.get(position), position);
                }
                break;

            case MODE_SISWA_HISTORY:
                // Siswa viewing their own absensi history
                if (siswaAbsensiList != null && position < siswaAbsensiList.size()) {
                    SiswaAbsensiResponse.AbsensiItem absensi = siswaAbsensiList.get(position);

                    // Format: "Tanggal: 13 Juni 2025"
                    holder.tvNamaSiswa.setText("Tanggal: " + formatDate(absensi.getTanggal()));

                    // Format: "Kelas: X-IPA | Guru: Joko | Waktu: 10:30"
                    String detailText = "Kelas: " + absensi.getNama_kelas() + " | Guru: " + absensi.getNama_guru();
                    String waktuSubmit = absensi.getFormattedTime();
                    if (!waktuSubmit.isEmpty()) {
                        detailText += " | Waktu: " + waktuSubmit;
                    }
                    holder.tvNisSiswa.setText(detailText);

                    holder.spinnerStatus.setVisibility(View.GONE);
                    holder.tvStatus.setVisibility(View.VISIBLE);
                    holder.tvStatus.setText(absensi.getStatus());
                    setStatusColor(holder.tvStatus, absensi.getStatus());
                }
                break;
        }
    }
    
    @Override
    public int getItemCount() {
        switch (adapterMode) {
            case MODE_GURU_VIEW:
                return absensiList != null ? absensiList.size() : 0;
            case MODE_GURU_CREATE:
                return siswaList != null ? siswaList.size() : 0;
            case MODE_SISWA_HISTORY:
                return siswaAbsensiList != null ? siswaAbsensiList.size() : 0;
            default:
                return 0;
        }
    }
    
    private void setupStatusSpinner(Spinner spinner, String currentStatus, int position) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set current selection
        for (int i = 0; i < statusOptions.length; i++) {
            if (statusOptions[i].equals(currentStatus)) {
                spinner.setSelection(i);
                break;
            }
        }

        // Set listener to track status changes
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int selectedPosition, long id) {
                String selectedStatus = statusOptions[selectedPosition];
                statusSelections.put(position, selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    
    private void setStatusColor(TextView textView, String status) {
        switch (status.toLowerCase()) {
            case "hadir":
                textView.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
                break;
            case "sakit":
                textView.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
                break;
            case "izin":
                textView.setBackgroundColor(Color.parseColor("#2196F3")); // Blue
                break;
            case "tidak ada keterangan":
                textView.setBackgroundColor(Color.parseColor("#F44336")); // Red
                break;
            default:
                textView.setBackgroundColor(Color.parseColor("#9E9E9E")); // Gray
                break;
        }
    }
    
    // Method to get selected status for each student (for guru)
    public String getStatusForPosition(int position) {
        return statusSelections.getOrDefault(position, "hadir");
    }

    // Method to get all status selections for submit
    public List<String> getAllStatusSelections() {
        List<String> selections = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            selections.add(getStatusForPosition(i));
        }
        return selections;
    }

    // Method to get siswa list for submit (new absensi)
    public List<SiswaListResponse.SiswaData> getSiswaList() {
        return siswaList;
    }

    // Method to get absensi list for submit (existing absensi)
    public List<AbsensiResponse.AbsensiData> getAbsensiList() {
        return absensiList;
    }

    // Helper method to format date
    private String formatDate(String dateString) {
        try {
            // Parse ISO date and format to readable format
            if (dateString != null && dateString.contains("T")) {
                return dateString.substring(0, 10); // Extract YYYY-MM-DD part
            }
            return dateString;
        } catch (Exception e) {
            return dateString;
        }
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaSiswa, tvNisSiswa, tvStatus;
        Spinner spinnerStatus;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaSiswa = itemView.findViewById(R.id.tvNamaSiswa);
            tvNisSiswa = itemView.findViewById(R.id.tvNisSiswa);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
        }
    }
}
