package com.example.absenku.models;

public class UpdateProfileRequest {
    private String nama;
    private String alamat;
    private String no_hp;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String nama, String alamat, String no_hp) {
        this.nama = nama;
        this.alamat = alamat;
        this.no_hp = no_hp;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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
}
