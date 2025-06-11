package com.example.absenku.models;

public class ProfileResponse {
    private String message;
    private Profile profile;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public static class Profile {
        private String id;
        private String nama;
        private String nis;
        private String jenis_kelamin;
        private String alamat;
        private String no_hp;
        private String username;
        private int id_kelas;
        private String nama_kelas;

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

        public String getAlamat() {
            return alamat;
        }

        public void setAlamat(String alamat) {
            this.alamat = alamat;
        }

        public String getNo_hp() {
            return no_hp;
        }

        public void setNo_hp(String no_hp) {
            this.no_hp = no_hp;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
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

        // Helper methods
        public String getJenisKelaminDisplay() {
            return "L".equals(jenis_kelamin) ? "Laki-laki" : "Perempuan";
        }

        public String getStatusDisplay() {
            return "Siswa/Pelajar";
        }
    }
}
