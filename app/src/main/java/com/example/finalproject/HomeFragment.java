package com.example.finalproject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar; // Import baru
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalproject.models.Menu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment implements MenuAdapter.OnItemAddedListener {

    private RecyclerView rvMakanan, rvMinuman;
    private EditText etSearch;
    private ViewPager2 vpBanner;

    private ProgressBar progressBar;
    private View mainContent;

    private FirebaseFirestore db;

    private ArrayList<Menu> displayMakananList;
    private ArrayList<Menu> displayMinumanList;
    private ArrayList<Menu> masterMakananList;
    private ArrayList<Menu> masterMinumanList;

    private MenuAdapter makananAdapter;
    private MenuAdapter minumanAdapter;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private OnCartUpdatedListener cartListener;

    public interface OnCartUpdatedListener {
        void onCartUpdated();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();

        rvMakanan = view.findViewById(R.id.rvMakanan);
        rvMinuman = view.findViewById(R.id.rvMinuman);
        etSearch = view.findViewById(R.id.etSearch);
        vpBanner = view.findViewById(R.id.vpBanner);

        progressBar = view.findViewById(R.id.progressBar);
        mainContent = view.findViewById(R.id.mainContent);

        List<Integer> bannerImages = Arrays.asList(
                R.drawable.banner_1, R.drawable.banner_2, R.drawable.banner_1
        );
        BannerAdapter bannerAdapter = new BannerAdapter(bannerImages);
        vpBanner.setAdapter(bannerAdapter);

        vpBanner.setOffscreenPageLimit(3);
        vpBanner.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(30));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        vpBanner.setPageTransformer(compositePageTransformer);
        vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });

        // --- MENU SETUP ---
        rvMakanan.setHasFixedSize(true);
        rvMakanan.setLayoutManager(new LinearLayoutManager(getContext()));

        rvMinuman.setHasFixedSize(true);
        rvMinuman.setLayoutManager(new LinearLayoutManager(getContext()));

        displayMakananList = new ArrayList<>();
        displayMinumanList = new ArrayList<>();
        masterMakananList = new ArrayList<>();
        masterMinumanList = new ArrayList<>();

        makananAdapter = new MenuAdapter(getContext(), displayMakananList, this);
        rvMakanan.setAdapter(makananAdapter);

        minumanAdapter = new MenuAdapter(getContext(), displayMinumanList, this);
        rvMinuman.setAdapter(minumanAdapter);

        fetchMenuData();
        setupSearch();

        return view;
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (vpBanner != null) {
                int currentItem = vpBanner.getCurrentItem();
                int totalItem = vpBanner.getAdapter().getItemCount();
                if (currentItem < totalItem - 1) {
                    vpBanner.setCurrentItem(currentItem + 1);
                } else {
                    vpBanner.setCurrentItem(0);
                }
            }
        }
    };

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMenu(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterMenu(String text) {
        ArrayList<Menu> filteredMakanan = new ArrayList<>();
        ArrayList<Menu> filteredMinuman = new ArrayList<>();
        for (Menu item : masterMakananList) {
            if (item.getNamaMenu().toLowerCase().contains(text.toLowerCase())) filteredMakanan.add(item);
        }
        for (Menu item : masterMinumanList) {
            if (item.getNamaMenu().toLowerCase().contains(text.toLowerCase())) filteredMinuman.add(item);
        }
        displayMakananList.clear();
        displayMakananList.addAll(filteredMakanan);
        makananAdapter.notifyDataSetChanged();
        displayMinumanList.clear();
        displayMinumanList.addAll(filteredMinuman);
        minumanAdapter.notifyDataSetChanged();
    }

    private void fetchMenuData() {
        // Mulai Loading
        progressBar.setVisibility(View.VISIBLE);
        mainContent.setVisibility(View.GONE);

        db.collection("menu").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                // Selesai Loading (Sukses atau Gagal tetap stop)
                progressBar.setVisibility(View.GONE);
                mainContent.setVisibility(View.VISIBLE);

                if (task.isSuccessful()) {
                    displayMakananList.clear();
                    displayMinumanList.clear();
                    masterMakananList.clear();
                    masterMinumanList.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Menu menu = document.toObject(Menu.class);
                        if (menu.getKategori() != null && menu.getKategori().equalsIgnoreCase("Makanan")) {
                            displayMakananList.add(menu);
                            masterMakananList.add(menu);
                        } else if (menu.getKategori() != null && menu.getKategori().equalsIgnoreCase("Minuman")) {
                            displayMinumanList.add(menu);
                            masterMinumanList.add(menu);
                        }
                    }
                    makananAdapter.notifyDataSetChanged();
                    minumanAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Gagal memuat menu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemAdded() {
        if (cartListener != null) cartListener.onCartUpdated();
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
        if (cartListener != null) cartListener.onCartUpdated();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCartUpdatedListener) {
            cartListener = (OnCartUpdatedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " harus implement OnCartUpdatedListener");
        }
    }
}