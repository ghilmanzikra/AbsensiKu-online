package com.example.absenku.api;

import com.example.absenku.models.LoginRequest;
import com.example.absenku.models.LoginResponse;
import com.example.absenku.models.ProfileResponse;
import com.example.absenku.models.GuruProfileResponse;
import com.example.absenku.models.ChangePasswordRequest;
import com.example.absenku.models.ChangePasswordResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("api/siswa/profile")
    Call<ProfileResponse> getStudentProfile(@Header("Authorization") String token);

    @GET("api/guru/profile")
    Call<GuruProfileResponse> getGuruProfile(@Header("Authorization") String token);

    @POST("api/siswa/change-password")
    Call<ChangePasswordResponse> changeStudentPassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);

    @POST("api/guru/change-password")
    Call<ChangePasswordResponse> changeGuruPassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);
}
