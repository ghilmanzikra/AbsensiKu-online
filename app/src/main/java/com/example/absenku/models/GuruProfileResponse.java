package com.example.absenku.models;

public class GuruProfileResponse {
    private String message;
    private GuruProfile profile;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GuruProfile getProfile() {
        return profile;
    }

    public void setProfile(GuruProfile profile) {
        this.profile = profile;
    }

    public static class GuruProfile {
        private String id;
        private String nama;
        private String nip;
        private String jenis_kelamin;
        private String alamat;
        private String no_hp;
        private String username;
        private String mata_pelajaran;

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

        public String getNip() {
            return nip;
        }

        public void setNip(String nip) {
            this.nip = nip;
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

        public String getMata_pelajaran() {
            return mata_pelajaran;
        }

        public void setMata_pelajaran(String mata_pelajaran) {
            this.mata_pelajaran = mata_pelajaran;
        }

        // Helper methods
        public String getJenisKelaminDisplay() {
            return "L".equals(jenis_kelamin) ? "Laki-laki" : "Perempuan";
        }

        public String getStatusDisplay() {
            return "Guru/Pengajar";
        }
    }
}
