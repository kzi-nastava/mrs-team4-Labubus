package com.example.ubre.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.R;
import com.example.ubre.ui.adapters.PanicListAdapter;
import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.LoginApi;
import com.example.ubre.ui.apis.RideApi;
import com.example.ubre.ui.dtos.PanicNotificationDto;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanicNotificationsFragment extends Fragment {

    private RecyclerView rvPanics;
    private TextView tvEmpty;

    public static PanicNotificationsFragment newInstance() {
        return new PanicNotificationsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_panic_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPanics = view.findViewById(R.id.rv_panics);
        tvEmpty = view.findViewById(R.id.tv_empty);

        rvPanics.setLayoutManager(new LinearLayoutManager(requireContext()));

        view.findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().onBackPressed()
        );

        loadPanics();
    }

    private void loadPanics() {
        rvPanics.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = requireContext().getApplicationContext()
                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);
        RideApi rideApi = ApiClient.getClient().create(RideApi.class);
        rideApi.getPanics("Bearer " + token).enqueue(new Callback<List<PanicNotificationDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<PanicNotificationDto>> call,
                                   @NonNull Response<List<PanicNotificationDto>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    List<PanicNotificationDto> panics = response.body();

                    if (panics.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvPanics.setVisibility(View.VISIBLE);
                        rvPanics.setAdapter(new PanicListAdapter(panics));
                    }
                } else {
                    Toast.makeText(requireContext(),
                            "Failed to load panic notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PanicNotificationDto>> call,
                                  @NonNull Throwable t) {
                Toast.makeText(requireContext(),
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}