package com.example.absenku.api;

import com.example.absenku.models.LoginRequest;
import com.example.absenku.models.LoginResponse;
import com.example.absenku.models.ProfileResponse;
import com.example.absenku.models.GuruProfileResponse;
import com.example.absenku.models.UpdateProfileRequest;
import com.example.absenku.models.UpdateProfileResponse;
import com.example.absenku.models.AbsensiRequest;
import com.example.absenku.models.AbsensiResponse;
import com.example.absenku.models.KelasResponse;
import com.example.absenku.models.SiswaListResponse;
import com.example.absenku.models.SiswaAbsensiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("api/siswa/profile")
    Call<ProfileResponse> getStudentProfile(@Header("Authorization") String token);

    @GET("api/guru/profile")
    Call<GuruProfileResponse> getGuruProfile(@Header("Authorization") String token);

    @PUT("api/siswa/profile")
    Call<UpdateProfileResponse> updateStudentProfile(@Header("Authorization") String token, @Body UpdateProfileRequest request);

    @PUT("api/guru/profile")
    Call<UpdateProfileResponse> updateGuruProfile(@Header("Authorization") String token, @Body UpdateProfileRequest request);

    // Absensi endpoints
    @POST("api/absen/submit")
    Call<AbsensiResponse> submitAbsensi(@Header("Authorization") String token, @Body AbsensiRequest request);

    @GET("api/absen")
    Call<AbsensiResponse> getAbsensi(@Header("Authorization") String token, @Query("id_kelas") int id_kelas, @Query("tanggal") String tanggal);

    // Kelas endpoints
    @GET("api/kelas/all")
    Call<KelasResponse> getAllKelas(@Header("Authorization") String token);

    @GET("api/kelas")
    Call<KelasResponse> getKelasByGuru(@Header("Authorization") String token, @Query("guru_id") String guru_id);

    @GET("api/guru/kelas")
    Call<KelasResponse> getGuruKelas(@Header("Authorization") String token, @Query("guru_id") String guru_id);

    // Siswa endpoints
    @GET("api/siswa/list")
    Call<SiswaListResponse> getSiswaByKelas(@Header("Authorization") String token, @Query("id_kelas") int id_kelas);

    @GET("api/siswa/absensi")
    Call<SiswaAbsensiResponse> getSiswaAbsensi(@Header("Authorization") String token);
}
