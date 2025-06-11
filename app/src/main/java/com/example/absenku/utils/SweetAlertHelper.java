package com.example.absenku.utils;

import android.content.Context;
import es.dmoral.toasty.Toasty;

public class SweetAlertHelper {

    /**
     * Interface untuk callback
     */
    public interface OnSweetClickListener {
        void onClick();
    }

    /**
     * Menampilkan alert sukses
     */
    public static void showSuccess(Context context, String title, String message) {
        showSuccess(context, title, message, null);
    }

    public static void showSuccess(Context context, String title, String message, OnSweetClickListener onConfirm) {
        try {
            // Gunakan default Toasty success (hijau default)
            Toasty.success(context, title + ": " + message, Toasty.LENGTH_LONG, true).show();
        } catch (Exception e) {
            // Fallback ke Toast biasa jika Toasty error
            android.widget.Toast.makeText(context, title + ": " + message, android.widget.Toast.LENGTH_LONG).show();
        }

        if (onConfirm != null) {
            // Delay untuk memberikan waktu toast muncul
            new android.os.Handler().postDelayed(onConfirm::onClick, 1500);
        }
    }
    
    /**
     * Menampilkan alert error
     */
    public static void showError(Context context, String title, String message) {
        showError(context, title, message, null);
    }

    public static void showError(Context context, String title, String message, OnSweetClickListener onConfirm) {
        try {
            // Gunakan default error color (merah)
            Toasty.error(context, title + ": " + message, Toasty.LENGTH_LONG).show();
        } catch (Exception e) {
            // Fallback ke Toast biasa jika Toasty error
            android.widget.Toast.makeText(context, title + ": " + message, android.widget.Toast.LENGTH_LONG).show();
        }

        if (onConfirm != null) {
            new android.os.Handler().postDelayed(onConfirm::onClick, 1500);
        }
    }
    
    /**
     * Menampilkan alert warning
     */
    public static void showWarning(Context context, String title, String message) {
        showWarning(context, title, message, null);
    }

    public static void showWarning(Context context, String title, String message, OnSweetClickListener onConfirm) {
        try {
            // Gunakan default warning color (kuning)
            Toasty.warning(context, title + ": " + message, Toasty.LENGTH_LONG).show();
        } catch (Exception e) {
            // Fallback ke Toast biasa jika Toasty error
            android.widget.Toast.makeText(context, title + ": " + message, android.widget.Toast.LENGTH_LONG).show();
        }

        if (onConfirm != null) {
            new android.os.Handler().postDelayed(onConfirm::onClick, 1500);
        }
    }
    
    /**
     * Menampilkan alert info
     */
    public static void showInfo(Context context, String title, String message) {
        showInfo(context, title, message, null);
    }

    public static void showInfo(Context context, String title, String message, OnSweetClickListener onConfirm) {
        try {
            // Gunakan default info color (biru)
            Toasty.info(context, title + ": " + message, Toasty.LENGTH_LONG).show();
        } catch (Exception e) {
            // Fallback ke Toast biasa jika Toasty error
            android.widget.Toast.makeText(context, title + ": " + message, android.widget.Toast.LENGTH_LONG).show();
        }

        if (onConfirm != null) {
            new android.os.Handler().postDelayed(onConfirm::onClick, 1500);
        }
    }
    
    /**
     * Menampilkan alert konfirmasi dengan Yes/No
     */
    public static void showConfirmation(Context context, String title, String message,
                                      OnSweetClickListener onConfirm,
                                      OnSweetClickListener onCancel) {
        try {
            // Untuk konfirmasi, gunakan default warning color
            Toasty.warning(context, title + ": " + message, Toasty.LENGTH_LONG).show();
        } catch (Exception e) {
            // Fallback ke Toast biasa jika Toasty error
            android.widget.Toast.makeText(context, title + ": " + message, android.widget.Toast.LENGTH_LONG).show();
        }

        if (onConfirm != null) {
            new android.os.Handler().postDelayed(onConfirm::onClick, 1500);
        }
    }
    
    /**
     * Helper methods untuk kasus umum
     */
    public static void showLoginSuccess(Context context, String username, OnSweetClickListener onConfirm) {
        showSuccess(context, "Login Berhasil!",
                   "Selamat datang " + username + "!", onConfirm);
    }

    public static void showLoginError(Context context, String errorMessage) {
        showError(context, "Login Gagal", errorMessage);
    }

    public static void showValidationError(Context context, String message) {
        showWarning(context, "Periksa Input", message);
    }

    public static void showNetworkError(Context context) {
        showError(context, "Koneksi Bermasalah",
                 "Gagal terhubung ke server. Periksa koneksi internet Anda.");
    }

    public static void showLogoutConfirmation(Context context,
                                            OnSweetClickListener onConfirm) {
        showConfirmation(context, "Logout",
                        "Apakah Anda yakin ingin keluar?",
                        onConfirm, null);
    }
}
