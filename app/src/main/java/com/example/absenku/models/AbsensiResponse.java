package com.example.absenku.models;

import java.util.List;

public class AbsensiResponse {
    private String message;
    private String tanggal;
    private int id_kelas;
    private int jumlah_siswa;
    private Summary summary;
    private List<AbsensiData> data;
    private int inserted; // for submit response

    public AbsensiResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public int getId_kelas() {
        return id_kelas;
    }

    public void setId_kelas(int id_kelas) {
        this.id_kelas = id_kelas;
    }

    public int getJumlah_siswa() {
        return jumlah_siswa;
    }

    public void setJumlah_siswa(int jumlah_siswa) {
        this.jumlah_siswa = jumlah_siswa;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public List<AbsensiData> getData() {
        return data;
    }

    public void setData(List<AbsensiData> data) {
        this.data = data;
    }

    public int getInserted() {
        return inserted;
    }

    public void setInserted(int inserted) {
        this.inserted = inserted;
    }

    public static class Summary {
        private int hadir;
        private int sakit;
        private int izin;
        private int tidak_ada_keterangan;

        public Summary() {
        }

        public int getHadir() {
            return hadir;
        }

        public void setHadir(int hadir) {
            this.hadir = hadir;
        }

        public int getSakit() {
            return sakit;
        }

        public void setSakit(int sakit) {
            this.sakit = sakit;
        }

        public int getIzin() {
            return izin;
        }

        public void setIzin(int izin) {
            this.izin = izin;
        }

        public int getTidak_ada_keterangan() {
            return tidak_ada_keterangan;
        }

        public void setTidak_ada_keterangan(int tidak_ada_keterangan) {
            this.tidak_ada_keterangan = tidak_ada_keterangan;
        }
    }

    public static class AbsensiData {
        private int id;
        private String id_siswa;
        private String nama_siswa;
        private String nis;
        private String jenis_kelamin;
        private String tanggal;
        private String status;
        private String nama_kelas;
        private String nama_guru;

        public AbsensiData() {
        }

        // Getters and setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getId_siswa() {
            return id_siswa;
        }

        public void setId_siswa(String id_siswa) {
            this.id_siswa = id_siswa;
        }

        public String getNama_siswa() {
            return nama_siswa;
        }

        public void setNama_siswa(String nama_siswa) {
            this.nama_siswa = nama_siswa;
        }

        public String getNis() {
            return nis;
        }

        public void setNis(String nis) {
            this.nis = nis;
        }

        public String getJenis_kelamin() {
            return jenis_kelamin;
        }

        public void setJenis_kelamin(String jenis_kelamin) {
            this.jenis_kelamin = jenis_kelamin;
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
    }
}
