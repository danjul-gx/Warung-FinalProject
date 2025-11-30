package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail;
    private Button btnLogout, btnEditProfile; // Tambahan btnEditProfile
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        // --- PERBAIKAN ID DI SINI ---
        // ID ini harus SAMA PERSIS dengan yang ada di fragment_profile.xml (Versi Premium)
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // Ambil data user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Set Email
            if (tvEmail != null) {
                tvEmail.setText(user.getEmail());
            }

            // Set Nama
            if (tvName != null) {
                if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                    tvName.setText(user.getDisplayName());
                } else {
                    // Ambil nama dari email jika display name kosong
                    String email = user.getEmail();
                    if (email != null && email.contains("@")) {
                        tvName.setText(email.substring(0, email.indexOf("@")));
                    } else {
                        tvName.setText("Pengguna");
                    }
                }
            }
        }

        // Tombol Logout
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        // Tombol Edit (Opsional, bisa diisi intent ke halaman edit nanti)
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                // Tambahkan logika edit profil di sini jika sudah ada activity-nya
            });
        }

        return view;
    }
}