package com.example.finalproject;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // WAJIB: Import Glide
import com.example.finalproject.models.CartItem;
import com.example.finalproject.models.Menu;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    ImageView ivDetailImage;
    TextView tvDetailName, tvDetailPrice, tvDetailDesc;
    ImageButton btnBack;
    MaterialButton btnDetailAddToCart;

    Menu currentMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailDesc = findViewById(R.id.tvDetailDesc);
        btnBack = findViewById(R.id.btnBack);
        btnDetailAddToCart = findViewById(R.id.btnDetailAddToCart);

        if (getIntent().hasExtra("nama")) {
            String nama = getIntent().getStringExtra("nama");
            String deskripsi = getIntent().getStringExtra("deskripsi");
            long harga = getIntent().getLongExtra("harga", 0);
            String gambar = getIntent().getStringExtra("gambar"); // Ambil URL
            String kategori = getIntent().getStringExtra("kategori");

            currentMenu = new Menu(nama, harga, deskripsi, kategori, gambar);

            tvDetailName.setText(nama);
            tvDetailDesc.setText(deskripsi);

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            tvDetailPrice.setText(formatter.format(harga));

            if (gambar != null && !gambar.isEmpty()) {
                Glide.with(this)
                        .load(gambar)
                        .centerCrop()
                        .placeholder(R.drawable.ic_food_placeholder) // Loading
                        .error(R.drawable.ic_food_placeholder)       // Error
                        .into(ivDetailImage);
            } else {
                // ini kalo URL nya kosong, pakai placeholder default
                ivDetailImage.setImageResource(R.drawable.ic_food_placeholder);
            }
        }

        // Tombol Back
        btnBack.setOnClickListener(v -> finish());

        // Tombol Tambah ke Keranjang (Simple: Tambah 1 Pcs)
        btnDetailAddToCart.setOnClickListener(v -> {
            CartItem item = new CartItem(currentMenu, 1, "Normal", "Normal", "");
            CartManager.getInstance().addCartItem(item);

            Toast.makeText(this, "Berhasil ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show();
            finish(); // Kembali ke menu utama
        });
    }
}