package com.example.finalproject;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

// PENTING: Tambahkan 'implements HomeFragment.OnCartUpdatedListener'
// Ini yang mencegah Force Close saat buka Cart
public class MainActivity extends AppCompatActivity implements HomeFragment.OnCartUpdatedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load Fragment Home saat pertama kali dibuka
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Listener untuk klik menu bawah
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (id == R.id.nav_cart) {
                    selectedFragment = new CartFragment();
                } else if (id == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });

        // Update badge merah di keranjang saat aplikasi mulai
        updateCartBadge();
    }

    // Fungsi wajib dari Interface OnCartUpdatedListener
    @Override
    public void onCartUpdated() {
        updateCartBadge();
    }

    // Logika menampilkan angka merah (Badge) di ikon Cart
    private void updateCartBadge() {
        int totalItem = CartManager.getInstance().getCartItems().size();

        // Pastikan ID R.id.nav_cart sesuai dengan di bottom_nav_menu.xml
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);

        if (totalItem > 0) {
            badge.setVisible(true);
            badge.setNumber(totalItem);
            // Warna badge merah bata biar kelihatan
            badge.setBackgroundColor(getResources().getColor(R.color.brand_dark_rust));
        } else {
            badge.setVisible(false);
        }
    }
}