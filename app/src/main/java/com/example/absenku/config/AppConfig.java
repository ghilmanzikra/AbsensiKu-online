package com.example.absenku.config;

public class AppConfig {

    // Set ke true untuk menggunakan mock login (offline)
    // Set ke false untuk menggunakan real API
    public static final boolean USE_MOCK_LOGIN = false;

    // Set ke true untuk menggunakan mock profile (offline)
    // Set ke false untuk menggunakan real API
    public static final boolean USE_MOCK_PROFILE = false;

    // Set ke true untuk menggunakan mock password service (offline)
    // Set ke false untuk menggunakan real API
    public static final boolean USE_MOCK_PASSWORD = false;

    // URL API yang berbeda untuk testing
    public static final String PRODUCTION_API_URL = "https://absensi-backend-gabungan-production.up.railway.app/";
    public static final String ALTERNATIVE_API_URL = "https://your-alternative-api.herokuapp.com/";
    public static final String LOCAL_API_URL = "http://10.0.2.2:5000/"; // untuk emulator

    // Timeout settings
    public static final int CONNECT_TIMEOUT = 30; // seconds
    public static final int READ_TIMEOUT = 30; // seconds
    public static final int WRITE_TIMEOUT = 30; // seconds
}
