package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // Deklarasi View
    EditText etEmail, etPassword, etNama; // <-- BARU: etNama
    RadioGroup radioGroupGender;
    CheckBox cbTerms;
    Button btnRegister;
    TextView tvLoginLink;

    // Deklarasi Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etNama = findViewById(R.id.etNamaRegister); // <-- BARU: Hubungkan etNama
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerUser() {
        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(nama)) {
            etNama.setError("Nama tidak boleh kosong");
            etNama.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }
        // ... (validasi sisa)
        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Pilih jenis kelamin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Anda harus menyetujui Syarat & Ketentuan", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Memproses pendaftaran...", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registrasi Auth Berhasil", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            RadioButton selectedRadioButton = findViewById(selectedGenderId);
                            String gender = selectedRadioButton.getText().toString();

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("nama", nama); // <-- INI DIA
                            userData.put("email", email);
                            userData.put("gender", gender);
                            userData.put("role", "pelanggan");

                            db.collection("users").document(userId)
                                    .set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Firestore", "Data user berhasil disimpan!");
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error saving data", e);
                                            Toast.makeText(RegisterActivity.this, "Gagal simpan data user.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(RegisterActivity.this, "Registrasi Gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}