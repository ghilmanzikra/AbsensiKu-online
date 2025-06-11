package com.example.absenku.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Base URL untuk API - menggunakan mock API untuk testing
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    // URL alternatif jika Railway tidak aktif
    // private static final String BASE_URL = "https://absensi-backend-gabungan.up.railway.app/";

    // Untuk development lokal, uncomment baris di bawah dan comment yang di atas
    // private static final String BASE_URL = "http://10.0.2.2:5000/"; // untuk emulator
    // private static final String BASE_URL = "http://192.168.1.100:5000/"; // untuk device fisik
    
    private static Retrofit retrofit = null;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Logging interceptor untuk debugging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
