package com.example.ubre.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.R;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteAdapter extends RecyclerView.Adapter<AutocompleteAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final List<String> items = new ArrayList<>();
    private String query = "";
    private final OnItemClickListener listener;

    public AutocompleteAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<String> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void setQuery(String query) {
        this.query = query == null ? "" : query.trim().toLowerCase();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.autocomplete_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = items.get(position);
        holder.text.setText(highlight(text, query));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.autocomplete_item_text);
        }
    }

    private CharSequence highlight(String text, String query) {
        if (text == null || query == null || query.isEmpty()) {
            return text;
        }
        String lower = text.toLowerCase();
        int start = lower.indexOf(query);
        if (start < 0) {
            return text;
        }
        int end = start + query.length();
        SpannableString span = new SpannableString(text);
        span.setSpan(new ForegroundColorSpan(0xFF0A2A66), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }
}
