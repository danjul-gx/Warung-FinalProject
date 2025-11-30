package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Import Glide untuk gambar URL
import com.example.finalproject.models.CartItem;
import com.example.finalproject.models.Menu;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    Context context;
    ArrayList<Menu> menuList;
    CartManager cartManager;
    private OnItemAddedListener mListener;

    public interface OnItemAddedListener {
        void onItemAdded();
    }

    public MenuAdapter(Context context, ArrayList<Menu> menuList, OnItemAddedListener listener) {
        this.context = context;
        this.menuList = menuList;
        this.cartManager = CartManager.getInstance();
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan layout item_menu.xml
        View v = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Menu menu = menuList.get(position);

        // 1. Set Data Teks
        holder.tvMenuName.setText(menu.getNamaMenu());
        holder.tvMenuDescription.setText(menu.getDeskripsi());

        // Format Harga ke Rupiah
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedPrice = formatter.format(menu.getHarga());
        holder.tvMenuPrice.setText(formattedPrice);

        // 2. Set Gambar (Menggunakan Glide - URL dari Firestore)
        if (menu.getGambar() != null && !menu.getGambar().isEmpty()) {
            Glide.with(context)
                    .load(menu.getGambar())
                    .centerCrop()
                    .placeholder(R.drawable.ic_food_placeholder)
                    .error(R.drawable.ic_food_placeholder)
                    .into(holder.ivMenuImage);
        } else {
            // ini kalo URL kosong, pake placeholder default
            holder.ivMenuImage.setImageResource(R.drawable.ic_food_placeholder);
        }

        holder.btnAddToCart.setOnClickListener(v -> showBottomSheet(menu));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("nama", menu.getNamaMenu());
            intent.putExtra("harga", menu.getHarga());
            intent.putExtra("deskripsi", menu.getDeskripsi());
            intent.putExtra("gambar", menu.getGambar()); // Kirim URL gambar
            intent.putExtra("kategori", menu.getKategori());
            context.startActivity(intent);
        });
    }

    // --- BOTTOM SHEET DIALOG (Opsi Pesanan) ---
    private void showBottomSheet(Menu menu) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_menu, null);
        bottomSheetDialog.setContentView(view);

        // Hubungkan View di Bottom Sheet
        ImageView ivSheetImage = view.findViewById(R.id.ivSheetImage);
        TextView tvSheetMenuName = view.findViewById(R.id.tvSheetMenuName);
        TextView tvSheetPrice = view.findViewById(R.id.tvSheetPrice);
        TextView tvSheetDesc = view.findViewById(R.id.tvSheetDesc);
        ImageButton btnCloseSheet = view.findViewById(R.id.btnCloseSheet);

        LinearLayout layoutSheetOpsiPedas = view.findViewById(R.id.layoutSheetOpsiPedas);
        RadioGroup rgSheetOpsiPedas = view.findViewById(R.id.rgSheetOpsiPedas);
        LinearLayout layoutSheetOpsiGula = view.findViewById(R.id.layoutSheetOpsiGula);
        RadioGroup rgSheetOpsiGula = view.findViewById(R.id.rgSheetOpsiGula);

        EditText etSheetCatatan = view.findViewById(R.id.etSheetCatatan);
        ImageButton btnSheetKurang = view.findViewById(R.id.btnSheetKurang);
        ImageButton btnSheetTambah = view.findViewById(R.id.btnSheetTambah);
        TextView tvSheetQuantity = view.findViewById(R.id.tvSheetQuantity);
        View btnSheetAddToCart = view.findViewById(R.id.btnSheetAddToCart);

        // Set Data ke Sheet
        tvSheetMenuName.setText(menu.getNamaMenu());
        tvSheetDesc.setText(menu.getDeskripsi());
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        tvSheetPrice.setText(formatter.format(menu.getHarga()));

        // Gambar di Sheet (Glide)
        if (menu.getGambar() != null && !menu.getGambar().isEmpty()) {
            Glide.with(context).load(menu.getGambar()).centerCrop().into(ivSheetImage);
        } else {
            ivSheetImage.setImageResource(R.drawable.ic_food_placeholder);
        }

        // Logic Tampilkan Opsi Sesuai Kategori
        if (menu.getKategori() != null) {
            if (menu.getKategori().equalsIgnoreCase("Makanan")) {
                layoutSheetOpsiPedas.setVisibility(View.VISIBLE);
                layoutSheetOpsiGula.setVisibility(View.GONE);
            } else if (menu.getKategori() != null && menu.getKategori().equalsIgnoreCase("Minuman")) {
                layoutSheetOpsiGula.setVisibility(View.VISIBLE);
                layoutSheetOpsiPedas.setVisibility(View.GONE);
            }
        }

        final int[] quantity = {1};

        // Logic Tombol Jumlah
        btnSheetKurang.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                tvSheetQuantity.setText(String.valueOf(quantity[0]));
            }
        });

        btnSheetTambah.setOnClickListener(v -> {
            quantity[0]++;
            tvSheetQuantity.setText(String.valueOf(quantity[0]));
        });

        btnCloseSheet.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // Logic Tambah ke Keranjang
        btnSheetAddToCart.setOnClickListener(v -> {
            String opsiPedas = "";
            if (layoutSheetOpsiPedas.getVisibility() == View.VISIBLE) {
                int selectedId = rgSheetOpsiPedas.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton rb = view.findViewById(selectedId);
                    opsiPedas = rb.getText().toString();
                }
            }

            String opsiGula = "";
            if (layoutSheetOpsiGula.getVisibility() == View.VISIBLE) {
                int selectedId = rgSheetOpsiGula.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton rb = view.findViewById(selectedId);
                    opsiGula = rb.getText().toString();
                }
            }

            String catatan = etSheetCatatan.getText().toString().trim();
            int finalQuantity = quantity[0];

            // Buat Item Keranjang & Simpan
            CartItem cartItem = new CartItem(menu, finalQuantity, opsiPedas, opsiGula, catatan);
            cartManager.addCartItem(cartItem);

            // Snackbar (Notifikasi)
            Snackbar snackbar = Snackbar.make(view, "Berhasil masuk keranjang!", Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.brand_brown));
            snackbar.setTextColor(context.getResources().getColor(R.color.white));
            snackbar.show();

            if (mListener != null) {
                mListener.onItemAdded();
            }

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    // Fungsi Filter untuk Search Bar
    public void setFilteredList(ArrayList<Menu> filteredList) {
        this.menuList = filteredList;
        notifyDataSetChanged();
    }

    // ViewHolder
    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvMenuName, tvMenuDescription, tvMenuPrice;
        ImageButton btnAddToCart;
        ImageView ivMenuImage;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMenuName = itemView.findViewById(R.id.tvMenuName);
            tvMenuDescription = itemView.findViewById(R.id.tvMenuDescription);
            tvMenuPrice = itemView.findViewById(R.id.tvMenuPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            ivMenuImage = itemView.findViewById(R.id.ivMenuImage);
        }
    }
}