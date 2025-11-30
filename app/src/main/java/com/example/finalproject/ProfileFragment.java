package com.example.finalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail;
    private Button btnLogout, btnEditProfile, btnOrderStatus;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        // 1. Hubungkan View
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnOrderStatus = view.findViewById(R.id.btnOrderStatus);

        // 2. Load Data User saat ini
        loadUserData();

        // 3. Listener Tombol Logout
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        // 4. Listener Tombol Lacak Pesanan
        if (btnOrderStatus != null) {
            btnOrderStatus.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), OrderStatusActivity.class);
                startActivity(intent);
            });
        }

        // 5. Listener Tombol Edit Profil (Munculkan Dialog Custom)
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        }

        return view;
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Set Email
            if (tvEmail != null) tvEmail.setText(user.getEmail());

            // Set Nama (Prioritas: DisplayName -> Email Name -> Default)
            if (tvName != null) {
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
        }
    }

    private void showEditProfileDialog() {
        // A. Siapkan Dialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // B. Inflate Layout Custom (dialog_edit_profile.xml)
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // C. PENTING: Set Background Transparan agar sudut CardView terlihat melengkung
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // D. Hubungkan View di dalam Dialog
        TextInputEditText etEditName = dialogView.findViewById(R.id.etEditName);
        Button btnSave = dialogView.findViewById(R.id.btnSaveEdit);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelEdit);

        // E. Isi input dengan nama yang sekarang
        if (tvName.getText() != null) {
            etEditName.setText(tvName.getText().toString());
        }

        // F. Aksi Tombol Simpan
        btnSave.setOnClickListener(v -> {
            String newName = etEditName.getText().toString().trim();
            if (!newName.isEmpty()) {
                updateFirebaseProfile(newName);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }
        });

        // G. Aksi Tombol Batal
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateFirebaseProfile(String newName) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Request update profile ke Firebase
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Profil diperbarui!", Toast.LENGTH_SHORT).show();
                                tvName.setText(newName); // Update tampilan langsung
                            } else {
                                Toast.makeText(getContext(), "Gagal update profil", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}