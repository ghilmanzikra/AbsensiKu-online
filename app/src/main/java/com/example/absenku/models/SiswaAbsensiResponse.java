package com.example.absenku.models;

import java.util.List;

public class SiswaAbsensiResponse {
    private String message;
    private SiswaInfo siswa;
    private int total_records;
    private List<AbsensiItem> absensi;

    public SiswaAbsensiResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SiswaInfo getSiswa() {
        return siswa;
    }

    public void setSiswa(SiswaInfo siswa) {
        this.siswa = siswa;
    }

    public int getTotal_records() {
        return total_records;
    }

    public void setTotal_records(int total_records) {
        this.total_records = total_records;
    }

    public List<AbsensiItem> getAbsensi() {
        return absensi;
    }

    public void setAbsensi(List<AbsensiItem> absensi) {
        this.absensi = absensi;
    }

    public static class SiswaInfo {
        private String id;
        private String nama;
        private String nis;
        private String kelas;

        public SiswaInfo() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }

        public String getNis() {
            return nis;
        }

        public void setNis(String nis) {
            this.nis = nis;
        }

        public String getKelas() {
            return kelas;
        }

        public void setKelas(String kelas) {
            this.kelas = kelas;
        }
    }

    public static class AbsensiItem {
        private int id;
        private String tanggal;
        private String status;
        private String nama_kelas;
        private String nama_guru;
        private String created_at; // Waktu submit absensi
        private String waktu_submit; // Alternative field name

        public AbsensiItem() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTanggal() {
            return tanggal;
        }

        public void setTanggal(String tanggal) {
            this.tanggal = tanggal;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNama_kelas() {
            return nama_kelas;
        }

        public void setNama_kelas(String nama_kelas) {
            this.nama_kelas = nama_kelas;
        }

        public String getNama_guru() {
            return nama_guru;
        }

        public void setNama_guru(String nama_guru) {
            this.nama_guru = nama_guru;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getWaktu_submit() {
            return waktu_submit;
        }

        public void setWaktu_submit(String waktu_submit) {
            this.waktu_submit = waktu_submit;
        }

        // Helper method to get formatted time
        public String getFormattedTime() {
            if (created_at != null && !created_at.isEmpty()) {
                try {
                    // Parse datetime and return time only
                    // Format: "2025-06-13 10:30:45" -> "10:30"
                    if (created_at.contains(" ")) {
                        String timePart = created_at.split(" ")[1];
                        if (timePart.contains(":")) {
                            String[] timeParts = timePart.split(":");
                            return timeParts[0] + ":" + timeParts[1]; // HH:MM
                        }
                    }
                } catch (Exception e) {
                    // Fallback to original
                }
            }

            if (waktu_submit != null && !waktu_submit.isEmpty()) {
                return waktu_submit;
            }

            return ""; // No time available
        }
    }
}
