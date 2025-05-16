package com.example.absenku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginSiswaActivity extends AppCompatActivity {

    EditText etNis, etPassword;
    Button btnLoginSiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_siswa);

        etNis = findViewById(R.id.etNis);
        etPassword = findViewById(R.id.etPassword);
        btnLoginSiswa = findViewById(R.id.btnLoginSiswa);

        btnLoginSiswa.setOnClickListener(v -> {
            String nis = etNis.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (nis.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "NIS dan Password harus diisi", Toast.LENGTH_SHORT).show();
            } else {
                // Setelah login berhasil
                Intent intent = new Intent(LoginSiswaActivity.this, DashboardActivity.class);

                startActivity(intent);
                finish(); // agar tombol back tidak kembali ke login
            }
        });

    }
}
