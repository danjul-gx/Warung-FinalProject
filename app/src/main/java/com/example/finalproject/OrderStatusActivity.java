package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderStatusActivity extends AppCompatActivity {

    private TextView tvStatusLabel, tvStatusDesc, tvOrderTotal;
    private ImageView btnBack;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        tvStatusLabel = findViewById(R.id.tvStatusLabel);
        tvStatusDesc = findViewById(R.id.tvStatusDesc);
        tvOrderTotal = findViewById(R.id.tvOrderTotal);
        btnBack = findViewById(R.id.btnBack);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(v -> finish());

        loadLatestOrder();
    }

    private void loadLatestOrder() {
        if (mAuth.getCurrentUser() == null) return;

        db.collection("orders")
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(OrderStatusActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            DocumentSnapshot doc = value.getDocuments().get(0);
                            String status = doc.getString("status");
                            Long total = doc.getLong("totalPrice");

                            updateUI(status, total != null ? total : 0);
                        } else {
                            tvStatusLabel.setText("Belum Ada Pesanan");
                            tvStatusDesc.setText("Kamu belum memesan apapun.");
                        }
                    }
                });
    }

    private void updateUI(String status, long total) {
        // Format Rupiah
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        tvOrderTotal.setText(formatter.format(total));

        if (status == null) return;

        switch (status) {
            case "Menunggu Konfirmasi":
                tvStatusLabel.setText("Menunggu Konfirmasi");
                tvStatusDesc.setText("Pesananmu sudah masuk, mohon tunggu sebentar.");
                break;
            case "Sedang Dimasak":
                tvStatusLabel.setText("Sedang Dimasak");
                tvStatusDesc.setText("Chef sedang menyiapkan makananmu!");
                break;
            case "Sedang Diantar":
                tvStatusLabel.setText("Sedang Diantar");
                tvStatusDesc.setText("Kurir sedang menuju ke tempatmu.");
                break;
            case "Selesai":
                tvStatusLabel.setText("Pesanan Selesai");
                tvStatusDesc.setText("Terima kasih sudah memesan!");
                break;
            default:
                tvStatusLabel.setText(status);
                tvStatusDesc.setText("Status pesanan diperbarui.");
                break;
        }
    }
}