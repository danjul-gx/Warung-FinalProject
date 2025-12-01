package com.example.finalproject;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private List<DocumentSnapshot> orderList;

    public OrderHistoryAdapter(List<DocumentSnapshot> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot doc = orderList.get(position);

        String status = doc.getString("status");
        Long total = doc.getLong("totalPrice");
        Timestamp timestamp = doc.getTimestamp("timestamp");

        NumberFormat rupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        holder.tvTotalPrice.setText("Total: " + rupiah.format(total != null ? total : 0));

        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
            holder.tvDate.setText(sdf.format(timestamp.toDate()));
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");
        StringBuilder summary = new StringBuilder();

        if (items != null) {
            for (Map<String, Object> item : items) {
                Map<String, Object> menuData = (Map<String, Object>) item.get("menu");
                String namaMenu = "Menu";
                if (menuData != null) {
                    namaMenu = (String) menuData.get("namaMenu");
                }
                Long qty = (Long) item.get("quantity");

                if (summary.length() > 0) summary.append(", ");
                summary.append(namaMenu).append(" (").append(qty).append(")");
            }
        }
        holder.tvOrderSummary.setText(summary.toString());

        if (status == null) status = "Memproses";
        holder.tvStatus.setText(status);

        int bgColor;
        int textColor;
        int iconTint;

        if (status.equalsIgnoreCase("Sedang Diantar")) {
            bgColor = Color.parseColor("#E3F2FD"); // Biru Muda
            textColor = Color.parseColor("#1565C0"); // Biru Tua
            iconTint = textColor;
        } else if (status.equalsIgnoreCase("Sedang Dimasak")) {
            bgColor = Color.parseColor("#FFF3E0"); // Oranye Muda
            textColor = Color.parseColor("#EF6C00"); // Oranye Tua
            iconTint = textColor;
        } else if (status.equalsIgnoreCase("Selesai") || status.equalsIgnoreCase("Pesanan Selesai")) {
            bgColor = Color.parseColor("#E8F5E9"); // Hijau Muda
            textColor = Color.parseColor("#2E7D32"); // Hijau Tua
            iconTint = textColor;
        } else {
            bgColor = Color.parseColor("#F5F5F5"); // Abu Muda
            textColor = Color.parseColor("#616161"); // Abu Tua
            iconTint = textColor;
        }

        holder.cvStatusBadge.setCardBackgroundColor(bgColor);
        holder.tvStatus.setTextColor(textColor);
        holder.ivStatusIconSmall.setColorFilter(iconTint);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvStatus, tvOrderSummary, tvTotalPrice;
        CardView cvStatusBadge;
        ImageView ivStatusIconSmall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvOrderSummary = itemView.findViewById(R.id.tvOrderSummary);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            cvStatusBadge = itemView.findViewById(R.id.cvStatusBadge);
            ivStatusIconSmall = itemView.findViewById(R.id.ivStatusIconSmall);
        }
    }
}