package com.example.absenku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginGuruActivity extends AppCompatActivity {

    EditText etIdGuru, etPassword;
    Button btnLoginGuru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_guru);

        etIdGuru = findViewById(R.id.etIdGuru);
        etPassword = findViewById(R.id.etPassword);
        btnLoginGuru = findViewById(R.id.btnLoginGuru);

        btnLoginGuru.setOnClickListener(v -> {
            String idGuru = etIdGuru.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (idGuru.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "ID Guru dan Password harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    // Setelah login berhasil
                    Intent intent = new Intent(LoginGuruActivity.this, DashboardActivityGuru.class);
                    startActivity(intent);
                    finish(); // agar tombol back tidak kembali ke login
                } catch (Exception e) {
                    Toast.makeText(this, "Terjadi kesalahan: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
