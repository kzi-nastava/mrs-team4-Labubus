package com.example.ubre.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.R;
import com.example.ubre.ui.adapters.ProfileChangesAdapter;
import com.example.ubre.ui.dtos.ProfileChangeDto;
import com.example.ubre.ui.mockdata.MockData;
import com.example.ubre.ui.services.ProfileChangeService;
import com.example.ubre.ui.storages.ProfileChangeStorage;


public class ProfileChangesFragment extends Fragment {

    public static ProfileChangesFragment newInstance() {
        return new ProfileChangesFragment();
    }
    public static final String TAG = "ProfileChangesFragment";
    private ProfileChangesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile_changes, container, false);

        v.findViewById(R.id.btn_back).setOnClickListener(x ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        RecyclerView rv = v.findViewById(R.id.rv_requests);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ProfileChangesAdapter(new ProfileChangesAdapter.Listener() {
            @Override public void onAccept(ProfileChangeDto item) {
                try {
                    ProfileChangeService.getInstance(requireContext()).approve(item); // this one runs post remove in background
                } catch (Exception e) {
                    Log.e(TAG, "Error approving profile change", e);
                }
            }

            @Override public void onReject(ProfileChangeDto item) {
                try {
                    ProfileChangeService.getInstance(requireContext()).reject(item); // this one runs post remove in background
                } catch (Exception e) {
                    Log.e(TAG, "Error rejecting profile change", e);
                }
            }
        });

        rv.setAdapter(adapter);

        ProfileChangeStorage.getInstance()
                .getProfileChanges()
                .observe(getViewLifecycleOwner(), list -> {
                    adapter.setItems(list);
                });

        ProfileChangeStorage.getInstance()
                .getAvatars()
                .observe(getViewLifecycleOwner(), map -> {
                    adapter.notifyDataSetChanged();
                });

        try {
            ProfileChangeService.getInstance(requireContext()).loadPendingProfileChanges();
        } catch (Exception e) {
            Log.e(TAG, "Error loading pending profile changes", e);
        }

        return v;
    }
}

