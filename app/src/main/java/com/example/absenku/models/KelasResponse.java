package com.example.absenku.models;

import java.util.List;

public class KelasResponse {
    private String message;
    private int total_kelas;
    private String guru_id;
    private int jumlah_kelas;
    private List<KelasData> kelas;

    public KelasResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotal_kelas() {
        return total_kelas;
    }

    public void setTotal_kelas(int total_kelas) {
        this.total_kelas = total_kelas;
    }

    public String getGuru_id() {
        return guru_id;
    }

    public void setGuru_id(String guru_id) {
        this.guru_id = guru_id;
    }

    public int getJumlah_kelas() {
        return jumlah_kelas;
    }

    public void setJumlah_kelas(int jumlah_kelas) {
        this.jumlah_kelas = jumlah_kelas;
    }

    public List<KelasData> getKelas() {
        return kelas;
    }

    public void setKelas(List<KelasData> kelas) {
        this.kelas = kelas;
    }

    public static class KelasData {
        private int id_kelas;
        private String nama_kelas;
        private String guru_id;
        private String nama_guru;
        private int jumlah_siswa;

        public KelasData() {
        }

        public int getId_kelas() {
            return id_kelas;
        }

        public void setId_kelas(int id_kelas) {
            this.id_kelas = id_kelas;
        }

        public String getNama_kelas() {
            return nama_kelas;
        }

        public void setNama_kelas(String nama_kelas) {
            this.nama_kelas = nama_kelas;
        }

        public String getGuru_id() {
            return guru_id;
        }

        public void setGuru_id(String guru_id) {
            this.guru_id = guru_id;
        }

        public String getNama_guru() {
            return nama_guru;
        }

        public void setNama_guru(String nama_guru) {
            this.nama_guru = nama_guru;
        }

        public int getJumlah_siswa() {
            return jumlah_siswa;
        }

        public void setJumlah_siswa(int jumlah_siswa) {
            this.jumlah_siswa = jumlah_siswa;
        }
    }
}
