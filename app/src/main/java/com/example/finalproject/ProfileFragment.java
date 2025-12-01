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
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvMemberStatus;
    private Button btnLogout, btnEditProfile, btnOrderStatus;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvMemberStatus = view.findViewById(R.id.tvMemberStatus);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnOrderStatus = view.findViewById(R.id.btnOrderStatus); // Tombol Lacak Pesanan

        loadUserData();

        // --- SETUP BUTTON LISTENERS ---

        // A. Tombol Logout
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        // B. Tombol Lacak Pesanan -> Ke Halaman Riwayat (History)
        if (btnOrderStatus != null) {
            btnOrderStatus.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), OrderHistoryActivity.class);
                startActivity(intent);
            });
        }

        // C. Tombol Edit Profil -> Buka Dialog
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

            // Set Nama
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



            if (tvMemberStatus != null) {
                db.collection("users").document(user.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists() && documentSnapshot.contains("totalSpending")) {
                                long totalSpent = documentSnapshot.getLong("totalSpending");
                                updateMemberStatus(totalSpent);
                            } else {
                                updateMemberStatus(0); // Belum pernah belanja
                            }
                        });
            }
        }
    }

    private void updateMemberStatus(long totalSpent) {
        String status;
        int colorCode;

        if (totalSpent >= 1000000) {
            status = "PLATINUM MEMBER";
            colorCode = Color.parseColor("#E5E4E2");
        } else if (totalSpent >= 500000) {
            status = "GOLD MEMBER";
            colorCode = Color.parseColor("#FFD700");
        } else if (totalSpent >= 200000) {
            status = "SILVER MEMBER";
            colorCode = Color.parseColor("#C0C0C0");
        } else if (totalSpent >= 50000) {
            status = "BRONZE MEMBER";
            colorCode = Color.parseColor("#CD7F32");
        } else {
            status = "NEW MEMBER";
            colorCode = Color.parseColor("#8D6E63");
        }

        tvMemberStatus.setText(status);
        tvMemberStatus.setTextColor(colorCode);
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextInputEditText etEditName = dialogView.findViewById(R.id.etEditName);
        Button btnSave = dialogView.findViewById(R.id.btnSaveEdit);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelEdit);

        if (tvName.getText() != null) {
            etEditName.setText(tvName.getText().toString());
        }

        btnSave.setOnClickListener(v -> {
            String newName = etEditName.getText().toString().trim();
            if (!newName.isEmpty()) {
                updateFirebaseProfile(newName);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateFirebaseProfile(String newName) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Profil diperbarui!", Toast.LENGTH_SHORT).show();
                                tvName.setText(newName);
                            } else {
                                Toast.makeText(getContext(), "Gagal update profil", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}