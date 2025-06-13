package com.example.absenku.models;

import java.util.List;

public class SiswaListResponse {
    private String message;
    private int id_kelas;
    private int total_siswa;
    private List<SiswaData> siswa;

    public SiswaListResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId_kelas() {
        return id_kelas;
    }

    public void setId_kelas(int id_kelas) {
        this.id_kelas = id_kelas;
    }

    public int getTotal_siswa() {
        return total_siswa;
    }

    public void setTotal_siswa(int total_siswa) {
        this.total_siswa = total_siswa;
    }

    public List<SiswaData> getSiswa() {
        return siswa;
    }

    public void setSiswa(List<SiswaData> siswa) {
        this.siswa = siswa;
    }

    public static class SiswaData {
        private String id;
        private String nama;
        private String nis;
        private String jenis_kelamin;
        private String username;

        public SiswaData() {
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

        public String getJenis_kelamin() {
            return jenis_kelamin;
        }

        public void setJenis_kelamin(String jenis_kelamin) {
            this.jenis_kelamin = jenis_kelamin;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
