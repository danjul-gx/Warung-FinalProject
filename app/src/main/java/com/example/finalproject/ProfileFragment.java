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
    private Button btnLogout, btnEditProfile, btnOrderStatus;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnOrderStatus = view.findViewById(R.id.btnOrderStatus);

        // Set Data User
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail());
            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                tvName.setText(user.getDisplayName());
            } else {
                String email = user.getEmail();
                if (email != null && email.contains("@")) {
                    tvName.setText(email.substring(0, email.indexOf("@")));
                } else {
                    tvName.setText("Pengguna");
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

        // Tombol Lacak Pesanan
        if (btnOrderStatus != null) {
            btnOrderStatus.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), OrderStatusActivity.class);
                startActivity(intent);
            });
        }

        // Tombol Edit (Placeholder)
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                // Tambahkan intent ke EditProfileActivity jika sudah ada
            });
        }

        return view;
    }
}