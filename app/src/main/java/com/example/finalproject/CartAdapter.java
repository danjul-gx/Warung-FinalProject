package com.example.finalproject;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView; // Import ImageView
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Import Glide
import com.example.finalproject.models.CartItem;
import com.example.finalproject.models.Menu;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    ArrayList<CartItem> cartItems;
    CartManager cartManager;

    private OnCartUpdatedListener mListener;

    public interface OnCartUpdatedListener {
        void onCartUpdated();
    }

    public CartAdapter(Context context, ArrayList<CartItem> cartItems, OnCartUpdatedListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartManager = CartManager.getInstance();
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        Menu menu = item.getMenu();

        // 1. Set Teks
        holder.tvCartMenuName.setText(menu.getNamaMenu() + " x " + item.getQuantity());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedPrice = formatter.format(item.getItemTotalPrice());
        holder.tvCartItemTotalPrice.setText(formattedPrice);

        // 2. Set Gambar (Pakai Glide lagi biar konsisten)
        if (menu.getGambar() != null && !menu.getGambar().isEmpty()) {
            Glide.with(context)
                    .load(menu.getGambar())
                    .centerCrop()
                    .placeholder(R.drawable.ic_food_placeholder)
                    .into(holder.ivCartItemImage);
        } else {
            holder.ivCartItemImage.setImageResource(R.drawable.ic_food_placeholder);
        }

        // 3. Set Opsi & Catatan
        ArrayList<String> opsiList = new ArrayList<>();
        if (item.getOpsiPedas() != null && !item.getOpsiPedas().isEmpty()) opsiList.add(item.getOpsiPedas());
        if (item.getOpsiGula() != null && !item.getOpsiGula().isEmpty()) opsiList.add(item.getOpsiGula());

        if (opsiList.isEmpty()) {
            holder.tvCartOpsi.setVisibility(View.GONE);
        } else {
            holder.tvCartOpsi.setVisibility(View.VISIBLE);
            holder.tvCartOpsi.setText("Opsi: " + TextUtils.join(", ", opsiList));
        }

        if (item.getCatatanTambahan() == null || item.getCatatanTambahan().isEmpty()) {
            holder.tvCartCatatan.setVisibility(View.GONE);
        } else {
            holder.tvCartCatatan.setVisibility(View.VISIBLE);
            holder.tvCartCatatan.setText("Catatan: " + item.getCatatanTambahan());
        }

        // 4. Tombol Hapus
        holder.btnRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition == RecyclerView.NO_POSITION) return;

                cartManager.removeCartItem(cartItems.get(currentPosition));
                notifyDataSetChanged();
                mListener.onCartUpdated();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView tvCartMenuName, tvCartItemTotalPrice, tvCartOpsi, tvCartCatatan;
        ImageButton btnRemoveItem;
        ImageView ivCartItemImage; // <-- ImageView untuk Cart

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCartMenuName = itemView.findViewById(R.id.tvCartMenuName);
            tvCartItemTotalPrice = itemView.findViewById(R.id.tvCartItemTotalPrice);
            tvCartOpsi = itemView.findViewById(R.id.tvCartOpsi);
            tvCartCatatan = itemView.findViewById(R.id.tvCartCatatan);
            btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);
            ivCartItemImage = itemView.findViewById(R.id.ivCartItemImage); // <-- Hubungkan ID
        }
    }
}