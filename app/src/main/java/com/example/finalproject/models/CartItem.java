package com.example.finalproject.models;

public class CartItem {
    private Menu menu;
    private int quantity;
    private String opsiPedas;
    private String opsiGula;
    private String catatanTambahan;

    public CartItem(Menu menu, int quantity, String opsiPedas, String opsiGula, String catatanTambahan) {
        this.menu = menu;
        this.quantity = quantity;
        this.opsiPedas = opsiPedas;
        this.opsiGula = opsiGula;
        this.catatanTambahan = catatanTambahan;
    }

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

    // --- Setters  ---
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