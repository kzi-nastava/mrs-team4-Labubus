package com.example.ubre.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.R;
import com.example.ubre.ui.adapters.BlockUsersAdapter;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.services.UserService;
import com.example.ubre.ui.storages.BlockUsersStorage;

public class BlockUsersFragment extends Fragment {

    public static BlockUsersFragment newInstance() {
        return new BlockUsersFragment();
    }

    public static final String TAG = "BlockUsersFragment";
    private BlockUsersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.block_users, container, false);

        v.findViewById(R.id.btn_back).setOnClickListener(x ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        RecyclerView rv = v.findViewById(R.id.rv_block_users);
        View loading = v.findViewById(R.id.block_users_loading);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new BlockUsersAdapter(new BlockUsersAdapter.Listener() {
            @Override
            public void onBlock(UserDto user, String note) {
                if (user == null || user.getId() == null) return;
                BlockUsersStorage.getInstance().updateUserBlocked(user.getId(), true);
                try {
                    UserService.getInstance(requireContext()).blockUser(user.getId(), note, new UserService.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(requireContext(), "Failed to block user.", Toast.LENGTH_SHORT).show();
                            BlockUsersStorage.getInstance().updateUserBlocked(user.getId(), false);
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Failed to block user.", Toast.LENGTH_SHORT).show();
                    BlockUsersStorage.getInstance().updateUserBlocked(user.getId(), false);
                }
            }

            @Override
            public void onUnblock(UserDto user) {
                if (user == null || user.getId() == null) return;
                BlockUsersStorage.getInstance().updateUserBlocked(user.getId(), false);
                try {
                    UserService.getInstance(requireContext()).unblockUser(user.getId(), new UserService.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(requireContext(), "Failed to unblock user.", Toast.LENGTH_SHORT).show();
                            BlockUsersStorage.getInstance().updateUserBlocked(user.getId(), true);
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Failed to unblock user.", Toast.LENGTH_SHORT).show();
                    BlockUsersStorage.getInstance().updateUserBlocked(user.getId(), true);
                }
            }
        });

        rv.setAdapter(adapter);

        BlockUsersStorage.getInstance()
                .getUsersReadOnly()
                .observe(getViewLifecycleOwner(), list -> adapter.setItems(list));

        BlockUsersStorage.getInstance()
                .getLoadingReadOnly()
                .observe(getViewLifecycleOwner(), isLoading -> {
                    boolean show = Boolean.TRUE.equals(isLoading);
                    loading.setVisibility(show ? View.VISIBLE : View.GONE);
                    rv.setVisibility(show ? View.GONE : View.VISIBLE);
                });

        try {
            UserService.getInstance(requireContext()).loadAllUsers();
        } catch (Exception e) {
            Log.e(TAG, "Error loading users", e);
            Toast.makeText(requireContext(), "Failed to load users.", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }

        return v;
    }
}
