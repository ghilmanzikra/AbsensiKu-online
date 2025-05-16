package com.example.absenku;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivityGuru extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_guru);

        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_dashboard); // set menu aktif

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                return true;
            } else if (id == R.id.nav_absen) {
                startActivity(new Intent(this, AbsenActivity.class));
            } else if (id == R.id.nav_tugas) {
                startActivity(new Intent(this, TugasActivity.class));
            } else if (id == R.id.nav_profil) {
                startActivity(new Intent(this, ProfilActivity.class));
            }
            overridePendingTransition(0, 0);
            return true;
        });
    }
}
