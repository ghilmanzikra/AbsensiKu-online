package com.example.absenku.models;

import java.util.List;

public class AbsensiRequest {
    private int id_kelas;
    private String tanggal;
    private String guru_id;
    private List<AbsensiItem> absensi;

    public AbsensiRequest() {
    }

    public AbsensiRequest(int id_kelas, String tanggal, String guru_id, List<AbsensiItem> absensi) {
        this.id_kelas = id_kelas;
        this.tanggal = tanggal;
        this.guru_id = guru_id;
        this.absensi = absensi;
    }

    public int getId_kelas() {
        return id_kelas;
    }

    public void setId_kelas(int id_kelas) {
        this.id_kelas = id_kelas;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getGuru_id() {
        return guru_id;
    }

    public void setGuru_id(String guru_id) {
        this.guru_id = guru_id;
    }

    public List<AbsensiItem> getAbsensi() {
        return absensi;
    }

    public void setAbsensi(List<AbsensiItem> absensi) {
        this.absensi = absensi;
    }

    public static class AbsensiItem {
        private String id_siswa;
        private String status;

        public AbsensiItem() {
        }

        public AbsensiItem(String id_siswa, String status) {
            this.id_siswa = id_siswa;
            this.status = status;
        }

        public String getId_siswa() {
            return id_siswa;
        }

        public void setId_siswa(String id_siswa) {
            this.id_siswa = id_siswa;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
