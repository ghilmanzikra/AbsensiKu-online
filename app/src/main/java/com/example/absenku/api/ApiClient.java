package com.example.absenku.api;

import com.example.absenku.config.AppConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    // Base URL untuk API - menggunakan production API
    private static final String BASE_URL = "https://absensi-backend-gabungan-production.up.railway.app/";

    // URL alternatif jika Railway tidak aktif
    // private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

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
                    .connectTimeout(AppConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(AppConfig.READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(AppConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
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
