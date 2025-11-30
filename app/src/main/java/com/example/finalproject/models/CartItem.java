package com.example.finalproject.models;

// Class ini merepresentasikan satu item di keranjang, termasuk opsi tambahan
public class CartItem {
    private Menu menu; // Objek Menu yang dipesan
    private int quantity; // Jumlah item yang dipesan
    private String opsiPedas; // Contoh: "Tidak Pedas", "Sedang", "Pedas"
    private String opsiGula;  // Contoh: "Normal", "Low Sugar", "Tidak Manis"
    private String catatanTambahan; // Untuk request lain-lain

    public CartItem(Menu menu, int quantity, String opsiPedas, String opsiGula, String catatanTambahan) {
        this.menu = menu;
        this.quantity = quantity;
        this.opsiPedas = opsiPedas;
        this.opsiGula = opsiGula;
        this.catatanTambahan = catatanTambahan;
    }

    // Constructor default (wajib jika nanti mau simpan ke Firestore/Gson)
    public CartItem() {
        // Default values
        this.quantity = 1;
        this.opsiPedas = "";
        this.opsiGula = "";
        this.catatanTambahan = "";
    }

    // --- Getters ---
    public Menu getMenu() {
        return menu;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getOpsiPedas() {
        return opsiPedas;
    }

    public String getOpsiGula() {
        return opsiGula;
    }

    public String getCatatanTambahan() {
        return catatanTambahan;
    }

    // --- Setters (Jika nanti perlu mengubah kuantitas atau opsi di keranjang) ---
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOpsiPedas(String opsiPedas) {
        this.opsiPedas = opsiPedas;
    }

    public void setOpsiGula(String opsiGula) {
        this.opsiGula = opsiGula;
    }

    public void setCatatanTambahan(String catatanTambahan) {
        this.catatanTambahan = catatanTambahan;
    }

    // --- Metode Bantuan ---
    public long getItemTotalPrice() {
        return menu.getHarga() * quantity;
    }
}