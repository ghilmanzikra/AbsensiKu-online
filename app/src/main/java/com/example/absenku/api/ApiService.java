package com.example.absenku.api;

import com.example.absenku.models.LoginRequest;
import com.example.absenku.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
