package com.example.ubre.ui.main;

import android.os.Bundle;
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


public class ProfileChangesFragment extends Fragment {

    public static ProfileChangesFragment newInstance() {
        return new ProfileChangesFragment();
    }

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
                // TODO: pozovi approve endpoint
                adapter.removeById(item.requestId);
            }

            @Override public void onReject(ProfileChangeDto item) {
                // TODO: pozovi reject endpoint
                adapter.removeById(item.requestId);
            }
        });

        rv.setAdapter(adapter);

        loadPending(); // TODO: ucitaj sa backend-a
        return v;
    }

    private void loadPending() {
        // TODO: GET pending -> adapter.setItems(list)
        // Za test dok se ne povežemo na backend
        adapter.setItems(MockData.profileProfileChanges());
    }
}

