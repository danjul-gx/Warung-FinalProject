package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvPaymentTotal, tvTimer;
    private Button btnConfirmPayment;

    private CartManager cartManager;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        cartManager = CartManager.getInstance();

        tvPaymentTotal = findViewById(R.id.tvPaymentTotal);
        tvTimer = findViewById(R.id.tvTimer);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        long totalPrice = cartManager.getTotalPrice();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        tvPaymentTotal.setText(formatter.format(totalPrice));

        startTimer(300000);

        btnConfirmPayment.setOnClickListener(v -> processOrder(totalPrice));
    }

    private void startTimer(long durationInMillis) {
        countDownTimer = new CountDownTimer(durationInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Format waktu MM:SS
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                Toast.makeText(PaymentActivity.this, "Waktu pembayaran habis!", Toast.LENGTH_SHORT).show();
                // Opsional: Bisa disable tombol bayar di sini
            }
        }.start();
    }

    private void processOrder(long totalPrice) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User tidak ditemukan!", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<CartItem> items = cartManager.getCartItems();
        if (items.isEmpty()) {
            Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirmPayment.setEnabled(false);
        btnConfirmPayment.setText("Memproses...");

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("userId", user.getUid());
        orderData.put("userEmail", user.getEmail());
        orderData.put("items", items);
        orderData.put("totalPrice", totalPrice);
        orderData.put("status", "Menunggu Konfirmasi"); // Status Awal
        orderData.put("timestamp", FieldValue.serverTimestamp());

        // Kirim ke Firestore
        db.collection("orders").add(orderData)
                .addOnSuccessListener(documentReference -> {
                    // Sukses
                    Toast.makeText(this, "Pesanan Berhasil Dibuat!", Toast.LENGTH_SHORT).show();

                    cartManager.clearCart();

                    Intent intent = new Intent(PaymentActivity.this, OrderStatusActivity.class);
                    intent.putExtra("ORDER_ID", documentReference.getId());
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Gagal
                    btnConfirmPayment.setEnabled(true);
                    btnConfirmPayment.setText("SAYA SUDAH BAYAR");
                    Toast.makeText(this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}