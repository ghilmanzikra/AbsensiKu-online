package com.example.absenku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    Button btnGuru, btnSiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnGuru = findViewById(R.id.btnGuru);
        btnSiswa = findViewById(R.id.btnSiswa);

        btnGuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent untuk pindah ke LoginSiswaActivity
                Intent intent = new Intent(LoginActivity.this, LoginGuruActivity.class);
                startActivity(intent);
            }
        });

        btnSiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent untuk pindah ke LoginSiswaActivity
                Intent intent = new Intent(LoginActivity.this, LoginSiswaActivity.class);
                startActivity(intent);
            }
        });
    }
}
