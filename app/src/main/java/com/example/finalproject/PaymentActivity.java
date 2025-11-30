package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer; // <-- BARU: Untuk Timer
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.models.CartItem; // <-- BARU
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    // Deklarasi View
    TextView tvPaymentTotal, tvTimer;
    Button btnConfirmPayment;

    // Deklarasi Firebase & Cart
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    CartManager cartManager;

    CountDownTimer countDownTimer;
    private long totalHarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Inisialisasi
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        cartManager = CartManager.getInstance();

        // Hubungkan View
        tvPaymentTotal = findViewById(R.id.tvPaymentTotal);
        tvTimer = findViewById(R.id.tvTimer);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        // 1. Ambil Total Harga dari CartManager
        totalHarga = cartManager.getTotalPrice();

        // 2. Format dan Tampilkan Total Harga
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        tvPaymentTotal.setText(formatter.format(totalHarga));

        // 3. Mulai Timer 5 Menit (300,000 milidetik)
        startTimer(300000);

        // 4. Set Listener Tombol Konfirmasi Bayar
        btnConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hentikan timer & simpan pesanan
                countDownTimer.cancel();
                saveOrderToFirestore();
            }
        });
    }

    // Fungsi untuk memulai timer
    private void startTimer(long duration) {
        countDownTimer = new CountDownTimer(duration, 1000) { // 1000ms = 1 detik tick
            @Override
            public void onTick(long millisUntilFinished) {
                // Konversi milidetik ke format menit:detik
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                tvTimer.setText(timeFormatted);
            }

            @Override
            public void onFinish() {
                // Saat timer habis
                tvTimer.setText("00:00 (Expired)");
                tvTimer.setTextColor(getResources().getColor(android.R.color.darker_gray));
                btnConfirmPayment.setEnabled(false); // Matikan tombol
                btnConfirmPayment.setText("Waktu Habis");
                Toast.makeText(PaymentActivity.this, "Waktu pembayaran habis", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    // Fungsi untuk menyimpan pesanan ke Firestore
    private void saveOrderToFirestore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Error: User tidak login", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String userEmail = user.getEmail();

        // Ambil item dari keranjang
        ArrayList<CartItem> cartItems = cartManager.getCartItems();

        // Ubah list CartItem menjadi format yang aman untuk Firestore (List of Maps)
        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (CartItem item : cartItems) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("namaMenu", item.getMenu().getNamaMenu());
            itemMap.put("harga", item.getMenu().getHarga());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("opsiPedas", item.getOpsiPedas());
            itemMap.put("opsiGula", item.getOpsiGula());
            itemMap.put("catatan", item.getCatatanTambahan());
            itemsList.add(itemMap);
        }

        // Buat "kotak" data pesanan (Dokumen)
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", userId);
        orderData.put("userEmail", userEmail);
        orderData.put("totalHarga", totalHarga);
        orderData.put("status", "Menunggu Konfirmasi Dapur"); // Status awal
        orderData.put("timestamp", FieldValue.serverTimestamp());
        orderData.put("items", itemsList); // Simpan list item

        // Simpan ke koleksi "orders"
        db.collection("orders")
                .add(orderData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(PaymentActivity.this, "Pesanan Berhasil Dibuat!", Toast.LENGTH_LONG).show();

                        // Kosongkan keranjang
                        cartManager.clearCart();

                        // Pindah kembali ke MainActivity (dan bersihkan tumpukan activity)
                        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); // Tutup PaymentActivity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error menambah pesanan", e);
                        Toast.makeText(PaymentActivity.this, "Gagal membuat pesanan.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Pastikan timer berhenti jika user menutup activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}