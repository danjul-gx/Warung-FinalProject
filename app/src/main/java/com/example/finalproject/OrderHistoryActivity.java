package com.example.finalproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrderHistory;
    private OrderHistoryAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ImageView btnBackHistory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        btnBackHistory = findViewById(R.id.btnBackHistory);

        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnBackHistory.setOnClickListener(v -> finish());

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        if (mAuth.getCurrentUser() == null) return;

        db.collection("orders")
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        adapter = new OrderHistoryAdapter(queryDocumentSnapshots.getDocuments());
                        rvOrderHistory.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Belum ada riwayat pesanan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}