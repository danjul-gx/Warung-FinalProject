package com.example.finalproject;

import com.example.finalproject.models.CartItem; // <-- BARU: Import CartItem
import com.example.finalproject.models.Menu;
import java.util.ArrayList;

public class CartManager {

    private static CartManager instance;
    private ArrayList<CartItem> cartItems; // <-- BERUBAH: Sekarang menyimpan CartItem

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // BARU: Fungsi untuk menambah CartItem ke keranjang
    public void addCartItem(CartItem newItem) {
        // Cek apakah item dengan menu dan opsi yang SAMA sudah ada di keranjang
        // Jika ada, tambahkan quantity-nya saja
        for (CartItem existingItem : cartItems) {
            boolean sameMenu = existingItem.getMenu().getNamaMenu().equals(newItem.getMenu().getNamaMenu());
            boolean samePedas = existingItem.getOpsiPedas().equals(newItem.getOpsiPedas());
            boolean sameGula = existingItem.getOpsiGula().equals(newItem.getOpsiGula());
            boolean sameCatatan = existingItem.getCatatanTambahan().equals(newItem.getCatatanTambahan());

            if (sameMenu && samePedas && sameGula && sameCatatan) {
                existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                return; // Item sudah di-update, keluar dari fungsi
            }
        }
        // Jika belum ada, tambahkan sebagai item baru
        cartItems.add(newItem);
    }

    // BARU: Fungsi untuk menghapus CartItem (berdasarkan objeknya)
    public void removeCartItem(CartItem itemToRemove) {
        cartItems.remove(itemToRemove);
    }

    // Fungsi untuk mendapatkan semua CartItem
    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }

    // Fungsi untuk menghitung total harga SEMUA item
    public long getTotalPrice() {
        long total = 0;
        for (CartItem item : cartItems) {
            total += item.getItemTotalPrice(); // Menggunakan getItemTotalPrice() dari CartItem
        }
        return total;
    }

    // Fungsi untuk mendapatkan jumlah total item (kuantitas) di keranjang
    public int getTotalItemQuantity() {
        int totalQuantity = 0;
        for (CartItem item : cartItems) {
            totalQuantity += item.getQuantity();
        }
        return totalQuantity;
    }

    // Fungsi untuk mengosongkan keranjang
    public void clearCart() {
        cartItems.clear();
    }
}