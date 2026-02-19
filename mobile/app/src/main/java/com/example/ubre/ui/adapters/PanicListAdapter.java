package com.example.ubre.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.PanicNotificationDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PanicListAdapter extends RecyclerView.Adapter<PanicListAdapter.ViewHolder> {

    private final List<PanicNotificationDto> items;

    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]");

    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.ENGLISH);

    public PanicListAdapter(List<PanicNotificationDto> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_panic_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PanicNotificationDto item = items.get(position);

        holder.tvTimestamp.setText(formatTimestamp(item.getTimestamp()));
        holder.tvRideId.setText("Ride #" + item.getRideId());
        holder.tvTriggeredBy.setText(formatTriggeredBy(item.getTriggeredBy()));
    }

    private String formatTimestamp(String raw) {
        if (raw == null) return "";
        try {
            LocalDateTime dt = LocalDateTime.parse(raw, INPUT_FORMAT);
            return dt.format(OUTPUT_FORMAT);
        } catch (Exception e) {
            return raw;
        }
    }

    private String formatTriggeredBy(String raw) {
        if (raw == null) return "";
        switch (raw) {
            case "DRIVER": return "Triggered by: Driver";
            case "REGISTERED_USER": return "Triggered by: Passenger";
            default: return "Triggered by: " + raw;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimestamp, tvRideId, tvTriggeredBy;

        ViewHolder(View itemView) {
            super(itemView);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvRideId = itemView.findViewById(R.id.tv_ride_id);
            tvTriggeredBy = itemView.findViewById(R.id.tv_triggered_by);
        }
    }
}