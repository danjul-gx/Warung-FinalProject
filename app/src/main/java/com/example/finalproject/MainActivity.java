package com.example.finalproject;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnCartUpdatedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load Fragment Home saat pertama kali
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Listener Navigasi Bawah
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

                    updateCartBadge();
                    return true;
                }
                return false;
            }
        });

        updateCartBadge();
    }

    @Override
    public void onCartUpdated() {
        updateCartBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }
    // -------------------------------------------------------------

    // Logika Badge Merah
    private void updateCartBadge() {
        // Ambil data real-time dari CartManager
        int totalItem = CartManager.getInstance().getCartItems().size();

        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);

        if (totalItem > 0) {
            badge.setVisible(true);
            badge.setNumber(totalItem);
            badge.setBackgroundColor(getResources().getColor(R.color.brand_dark_rust));
        } else {
            // Kalau 0, sembunyikan badge
            badge.setVisible(false);
            badge.clearNumber();
        }
    }
}