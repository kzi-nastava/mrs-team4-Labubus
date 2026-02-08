package com.example.ubre.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ubre.R;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.services.UserService;
import com.example.ubre.ui.storages.ProfileCardStorage;
import com.example.ubre.ui.storages.ReviewStorage;
import com.example.ubre.ui.storages.RideDetailsStorage;
import com.example.ubre.ui.storages.UserStorage;

import java.time.LocalDateTime;

public class ProfileCardFragment extends Fragment {

    private ProfileCardStorage storage;

    public ProfileCardFragment() {
        storage = new ProfileCardStorage();
    }

    public static ProfileCardFragment newInstance(Long userId, String name, String subtext, Integer icon) {
        ProfileCardFragment fragment = new ProfileCardFragment();
        Bundle args = new Bundle();
        args.putLong("USERID", userId);
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

            Long userId = args.getLong("USERID");
            storage.getAvatar().observe(this, (avatar) -> {
                if (avatar == null || avatar.length < 1)
                    Glide.with(this).load(R.drawable.img_default_avatar).circleCrop().into(avatarView);
                else
                    Glide.with(this).asBitmap().load(avatar).circleCrop().into(avatarView);
            });
            try {
                if (userId < 0)
                    Glide.with(this).load(R.drawable.img_default_avatar).circleCrop().into(avatarView);
                else
                    UserService.getInstance(getContext()).loadProfileCardAvatar(userId, storage);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AVATAR FETCH", e.getMessage());
            }

            int icon = args.getInt("ICON");
            if (icon == -1)
                iconView.setVisibility(View.GONE);
            else {
                Glide.with(this).load(icon).circleCrop().into(iconView);
                if (icon == R.drawable.ic_review) {
                    iconView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserDto user = UserStorage.getInstance().getCurrentUser().getValue();
                            RideDto ride = RideDetailsStorage.getInstance().getSelectedRideReadOnly().getValue();

                            if (user == null || ride == null || !ride.getCreatedBy().equals(user.getId()) || LocalDateTime.now().isAfter(ride.getStartTime().plusDays(3))) { //
                                Toast.makeText(getContext(), "Unable to review ride", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            ReviewStorage.getInstance().setRideId(RideDetailsStorage.getInstance().getSelectedRideReadOnly().getValue().getId());
                        }
                    });
                }
            }


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