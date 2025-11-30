package com.example.finalproject.models;

public class Menu {
    private String namaMenu;
    private long harga;
    private String deskripsi;
    private String kategori;
    private String gambar;

    // --- [CRITICAL FIX] No-Argument Constructor (Required by Firebase) ---
    public Menu() {
        // Empty constructor needed for Firestore serialization
    }
    // ---------------------------------------------------------------------

    public Menu(String namaMenu, long harga, String deskripsi, String kategori, String gambar) {
        this.namaMenu = namaMenu;
        this.harga = harga;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.gambar = gambar;
    }

    // Getters
    public String getNamaMenu() { return namaMenu; }
    public long getHarga() { return harga; }
    public String getDeskripsi() { return deskripsi; }
    public String getKategori() { return kategori; }
    public String getGambar() { return gambar; }
}