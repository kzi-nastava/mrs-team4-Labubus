package com.example.ubre.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ubre.R;
import com.example.ubre.ui.dtos.ProfileChangeDto;

import java.util.ArrayList;
import java.util.List;

public class ProfileChangesAdapter extends RecyclerView.Adapter<ProfileChangesAdapter.VH> {

    public interface Listener {
        void onAccept(ProfileChangeDto item);
        void onReject(ProfileChangeDto item);
    }

    private final List<ProfileChangeDto> items = new ArrayList<>();
    private final Listener listener;

    public ProfileChangesAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<ProfileChangeDto> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    public void removeById(Long requestId) {
//        if (requestId == null) return;
//        for (int i = 0; i < items.size(); i++) {
//            ProfileChangeDto it = items.get(i);
//            if (requestId.equals(it.requestId)) {
//                items.remove(i);
//                notifyItemRemoved(i);
//                return;
//            }
//        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_change, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ProfileChangeDto it = items.get(pos);

        h.txtUserId.setText("User ID: " + it.userId);

        String oldInfo = "Old: " + safe(it.oldName) + " " + safe(it.oldSurname)
                + ", " + safe(it.oldAddress) + ", " + safe(it.oldPhone);

        String newInfo = "New: " + safe(it.newName) + " " + safe(it.newSurname)
                + ", " + safe(it.newAddress) + ", " + safe(it.newPhone);

        h.txtOld.setText(oldInfo);
        h.txtNew.setText(newInfo);

        bindAvatar(h.imgOldAvatar, it.oldAvatarUrl);
        bindAvatar(h.imgNewAvatar, it.newAvatarUrl);

        h.btnAccept.setOnClickListener(v -> listener.onAccept(it));
        h.btnReject.setOnClickListener(v -> listener.onReject(it));
    }

    private void bindAvatar(ImageView img, String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(img).load(url).circleCrop().into(img);
        } else {
            Glide.with(img).load(R.drawable.img_default_avatar).circleCrop().into(img);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtUserId, txtOld, txtNew;
        ImageView imgOldAvatar, imgNewAvatar;
        Button btnAccept, btnReject;

        VH(@NonNull View v) {
            super(v);
            txtUserId = v.findViewById(R.id.txt_user_id);
            txtOld = v.findViewById(R.id.txt_old);
            txtNew = v.findViewById(R.id.txt_new);
            imgOldAvatar = v.findViewById(R.id.img_old_avatar);
            imgNewAvatar = v.findViewById(R.id.img_new_avatar);
            btnAccept = v.findViewById(R.id.btn_accept);
            btnReject = v.findViewById(R.id.btn_reject);
        }
    }
}
