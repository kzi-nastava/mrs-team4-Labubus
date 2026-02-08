package com.example.ubre.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ubre.R;
import com.example.ubre.ui.services.UserService;

public class ProfileCardFragment extends Fragment {

    public ProfileCardFragment() {
        // Required empty public constructor
    }

    public static ProfileCardFragment newInstance(String avatarUrl, String name, String subtext, Integer icon) {
        ProfileCardFragment fragment = new ProfileCardFragment();
        Bundle args = new Bundle();
        args.putString("AVATAR", avatarUrl);
        args.putString("NAME", name);
        args.putString("SUBTEXT", subtext);
        args.putInt("ICON", icon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_card, container, false);

        ImageView avatarView = root.findViewById(R.id.profile_card_avatar);
        ImageView iconView = root.findViewById(R.id.profile_card_icon);
        TextView nameView = root.findViewById(R.id.profile_card_name);
        TextView subtextView = root.findViewById(R.id.profile_card_subtext);

        if (getArguments() != null) {
            Bundle args = getArguments();

            String avatar = args.getString("AVATAR");
            if (avatar == null || avatar.isEmpty())
                Glide.with(this).load(R.drawable.img_default_avatar).circleCrop().into(avatarView);
            else
                Glide.with(this).load(avatar).circleCrop().into(avatarView);

            int icon = args.getInt("ICON");
            if (icon == -1)
                iconView.setVisibility(View.GONE);
            else
                Glide.with(this).load(icon).circleCrop().into(iconView);

            String name = args.getString("NAME");
            if (name != null)
                nameView.setText(name);

            String subtext = args.getString("SUBTEXT");
            if (subtext == null || subtext.isEmpty())
                subtextView.setVisibility(View.GONE);
            else
                subtextView.setText(subtext);
        }

        return root;
    }
}