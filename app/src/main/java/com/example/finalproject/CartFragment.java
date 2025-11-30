package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView; // PENTING: Import ini
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.models.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.OnCartUpdatedListener {

    private RecyclerView rvCart;
    private LinearLayout layoutEmptyState;
    private TextView tvTotalPrice;

    private CardView bottomLayout;
    // ------------------------------------

    private Button btnCheckout;

    private CartManager cartManager;
    private CartAdapter cartAdapter;
    private ArrayList<CartItem> cartItems;

    private HomeFragment.OnCartUpdatedListener mCartUpdateListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Ambil Data
        try {
            cartManager = CartManager.getInstance();
            cartItems = cartManager.getCartItems();
            if (cartItems == null) cartItems = new ArrayList<>();
        } catch (Exception e) {
            cartItems = new ArrayList<>();
        }

        // Hubungkan View
        rvCart = view.findViewById(R.id.rvCart);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);

        // ID bottomLayout di XML itu CardView, jadi variabelnya harus CardView
        bottomLayout = view.findViewById(R.id.bottomLayout);

        btnCheckout = view.findViewById(R.id.btnCheckout);

        //  Setup RecyclerView
        if (rvCart != null) {
            rvCart.setHasFixedSize(true);
            rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
            cartAdapter = new CartAdapter(getContext(), cartItems, this);
            rvCart.setAdapter(cartAdapter);
        }

        updateCartView();

        //  Tombol Checkout
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                if (cartItems.isEmpty()) {
                    Toast.makeText(getContext(), "Keranjang kosong!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Intent intent = new Intent(getActivity(), PaymentActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Gagal membuka pembayaran", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

    private void updateCartView() {
        if (getContext() == null || cartManager == null) return;

        try {
            long totalPrice = cartManager.getTotalPrice();
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            String formattedTotalPrice = formatter.format(totalPrice);

            if (tvTotalPrice != null) {
                tvTotalPrice.setText(formattedTotalPrice);
            }

            if (layoutEmptyState != null && rvCart != null && bottomLayout != null) {
                if (cartItems.isEmpty()) {
                    rvCart.setVisibility(View.GONE);
                    bottomLayout.setVisibility(View.GONE);
                    layoutEmptyState.setVisibility(View.VISIBLE);
                } else {
                    rvCart.setVisibility(View.VISIBLE);
                    bottomLayout.setVisibility(View.VISIBLE);
                    layoutEmptyState.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Log.e("CartFragment", "Error updating view: " + e.getMessage());
        }
    }

    @Override
    public void onCartUpdated() {
        updateCartView();
        if (mCartUpdateListener != null) {
            try {
                mCartUpdateListener.onCartUpdated();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }
        updateCartView();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeFragment.OnCartUpdatedListener) {
            mCartUpdateListener = (HomeFragment.OnCartUpdatedListener) context;
        } else {
            Log.w("CartFragment", "Activity tidak implement OnCartUpdatedListener.");
        }
    }
}