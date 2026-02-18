package com.example.ubre.ui.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.enums.Role;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockUsersAdapter extends RecyclerView.Adapter<BlockUsersAdapter.VH> {

    public interface Listener {
        void onBlock(UserDto user, String note);
        void onUnblock(UserDto user);
    }

    private final Listener listener;
    private final List<UserDto> items = new ArrayList<>();
    private final Map<Long, Boolean> noteOpen = new HashMap<>();
    private final Map<Long, String> noteText = new HashMap<>();

    public BlockUsersAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<UserDto> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.block_user_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        UserDto user = items.get(position);
        boolean isBlocked = user != null && Boolean.TRUE.equals(user.getIsBlocked());
        Long userId = user != null ? user.getId() : null;
        boolean isNoteOpen = userId != null && Boolean.TRUE.equals(noteOpen.get(userId));

        String name = user == null ? "" : safe(user.getName()) + " " + safe(user.getSurname());
        String roleLabel = mapRoleLabel(user == null ? null : user.getRole());
        String displayName = name.trim();
        if (!displayName.isEmpty() && !roleLabel.isEmpty()) {
            String combined = displayName + " \u00b7 " + roleLabel;
            android.text.SpannableString spannable = new android.text.SpannableString(combined);
            int dotIndex = displayName.length() + 1;
            int roleStart = dotIndex + 2;
            int roleEnd = combined.length();
            int lighterColor = androidx.core.content.ContextCompat.getColor(
                    h.itemView.getContext(), R.color.text_secondary_light);
            spannable.setSpan(new android.text.style.ForegroundColorSpan(lighterColor),
                    roleStart, roleEnd, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new android.text.style.ForegroundColorSpan(lighterColor),
                    dotIndex, dotIndex + 1, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                    dotIndex, dotIndex + 1, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            h.fullName.setText(spannable);
        } else if (!roleLabel.isEmpty()) {
            int lighterColor = androidx.core.content.ContextCompat.getColor(
                    h.itemView.getContext(), R.color.text_secondary_light);
            h.fullName.setText(roleLabel);
            h.fullName.setTextColor(lighterColor);
        } else {
            h.fullName.setText(displayName);
        }
        h.email.setText(user == null ? "" : safe(user.getEmail()));

        h.root.setBackgroundResource(isBlocked ? R.drawable.bg_user_card_blocked : R.drawable.bg_user_card);

        if (isBlocked) {
            h.noteLayout.setVisibility(View.GONE);
            h.primaryButton.setText("Unblock");
            h.primaryButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(h.itemView.getContext(), R.color.success)));
            h.primaryButton.setStrokeColor(android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(h.itemView.getContext(), R.color.success_dark)));
            h.secondaryButton.setVisibility(View.GONE);
        } else {
            h.primaryButton.setText(isNoteOpen ? "Block user" : "Block");
            h.primaryButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(h.itemView.getContext(), R.color.error)));
            h.primaryButton.setStrokeColor(android.content.res.ColorStateList.valueOf(
                    androidx.core.content.ContextCompat.getColor(h.itemView.getContext(), R.color.error_dark)));
            h.noteLayout.setVisibility(isNoteOpen ? View.VISIBLE : View.GONE);
            h.secondaryButton.setVisibility(isNoteOpen ? View.VISIBLE : View.GONE);
            if (isNoteOpen) {
                String existing = userId == null ? "" : noteText.getOrDefault(userId, "");
                if (h.noteWatcher != null) {
                    h.noteInput.removeTextChangedListener(h.noteWatcher);
                }
                h.noteInput.setText(existing);
                h.noteInput.setSelection(h.noteInput.getText() == null ? 0 : h.noteInput.getText().length());
                h.noteWatcher = new SimpleTextWatcher(text -> {
                    if (userId != null) {
                        noteText.put(userId, text);
                    }
                });
                h.noteInput.addTextChangedListener(h.noteWatcher);
            } else {
                if (h.noteWatcher != null) {
                    h.noteInput.removeTextChangedListener(h.noteWatcher);
                    h.noteWatcher = null;
                }
            }
        }

        h.primaryButton.setOnClickListener(v -> {
            if (user == null || userId == null) return;
            if (isBlocked) {
                listener.onUnblock(user);
                return;
            }
            if (!isNoteOpen) {
                noteOpen.put(userId, true);
                notifyItemChanged(position);
                return;
            }
            String note = noteText.getOrDefault(userId, "");
            noteOpen.put(userId, false);
            noteText.remove(userId);
            notifyItemChanged(position);
            listener.onBlock(user, note);
        });

        h.secondaryButton.setOnClickListener(v -> {
            if (userId == null) return;
            noteOpen.put(userId, false);
            noteText.remove(userId);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String mapRoleLabel(Role role) {
        if (role == Role.ADMIN) return "Admin";
        if (role == Role.DRIVER) return "Driver";
        if (role == Role.REGISTERED_USER) return "Passenger";
        return "Guest";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    static class VH extends RecyclerView.ViewHolder {
        LinearLayout root;
        TextView fullName;
        TextView email;
        TextInputLayout noteLayout;
        TextInputEditText noteInput;
        MaterialButton primaryButton;
        MaterialButton secondaryButton;
        TextWatcher noteWatcher;

        VH(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.block_user_card_root);
            fullName = itemView.findViewById(R.id.block_user_name);
            email = itemView.findViewById(R.id.block_user_email);
            noteLayout = itemView.findViewById(R.id.block_user_note_layout);
            noteInput = itemView.findViewById(R.id.block_user_note_input);
            primaryButton = itemView.findViewById(R.id.block_user_inline_action);
            secondaryButton = itemView.findViewById(R.id.block_user_secondary);
        }
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final OnTextChanged onTextChanged;

        SimpleTextWatcher(OnTextChanged onTextChanged) {
            this.onTextChanged = onTextChanged;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (onTextChanged != null) {
                onTextChanged.onChanged(s == null ? "" : s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private interface OnTextChanged {
        void onChanged(String text);
    }
}
