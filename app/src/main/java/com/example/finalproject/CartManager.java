package com.example.finalproject;

import com.example.finalproject.models.CartItem; // <-- BARU: Import CartItem
import com.example.finalproject.models.Menu;
import java.util.ArrayList;

public class CartManager {

    private static CartManager instance;
    private ArrayList<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addCartItem(CartItem newItem) {
        for (CartItem existingItem : cartItems) {
            boolean sameMenu = existingItem.getMenu().getNamaMenu().equals(newItem.getMenu().getNamaMenu());
            boolean samePedas = existingItem.getOpsiPedas().equals(newItem.getOpsiPedas());
            boolean sameGula = existingItem.getOpsiGula().equals(newItem.getOpsiGula());
            boolean sameCatatan = existingItem.getCatatanTambahan().equals(newItem.getCatatanTambahan());

            if (sameMenu && samePedas && sameGula && sameCatatan) {
                existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        cartItems.add(newItem);
    }

    public void removeCartItem(CartItem itemToRemove) {
        cartItems.remove(itemToRemove);
    }

    // fungsi buat dapet semua CartItem
    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }

    // fungsi untuk ngitung total harga SEMUA item
    public long getTotalPrice() {
        long total = 0;
        for (CartItem item : cartItems) {
            total += item.getItemTotalPrice();
        }
        return total;
    }

    // fungsi buat dapetin jumlah total item (kuantitas) di keranjang
    public int getTotalItemQuantity() {
        int totalQuantity = 0;
        for (CartItem item : cartItems) {
            totalQuantity += item.getQuantity();
        }
        return totalQuantity;
    }

    // fungsi buat mengosongkan keranjang
    public void clearCart() {
        cartItems.clear();
    }
}