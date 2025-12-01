package com.example.finalproject.models;

import com.google.firebase.firestore.PropertyName; // 1. Wajib Import ini

public class Menu {
    private String namaMenu;
    private long harga;
    private String deskripsi;
    private String kategori;
    private String gambar;

    public Menu() {
    }

    public Menu(String namaMenu, long harga, String deskripsi, String kategori, String gambar) {
        this.namaMenu = namaMenu;
        this.harga = harga;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.gambar = gambar;
    }


    @PropertyName("namaMenu")
    public String getNamaMenu() { return namaMenu; }

    @PropertyName("namaMenu")
    public void setNamaMenu(String namaMenu) { this.namaMenu = namaMenu; }

    @PropertyName("harga")
    public long getHarga() { return harga; }
    @PropertyName("harga")
    public void setHarga(long harga) { this.harga = harga; }

    @PropertyName("deskripsi")
    public String getDeskripsi() { return deskripsi; }
    @PropertyName("deskripsi")
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    @PropertyName("kategori")
    public String getKategori() { return kategori; }
    @PropertyName("kategori")
    public void setKategori(String kategori) { this.kategori = kategori; }

    @PropertyName("gambar")
    public String getGambar() { return gambar; }
    @PropertyName("gambar")
    public void setGambar(String gambar) { this.gambar = gambar; }
}