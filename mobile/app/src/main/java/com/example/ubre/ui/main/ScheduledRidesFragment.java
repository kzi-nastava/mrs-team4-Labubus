package com.example.ubre.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubre.R;
import com.example.ubre.ui.adapters.RideListAdapter;
import com.example.ubre.ui.dtos.RideCardDto;
import com.example.ubre.ui.services.RideService;

import java.util.List;

public class ScheduledRidesFragment extends Fragment implements RideListAdapter.OnItemClickedListener {

    private Integer page;
    private Integer count;
    private Long userId;

    private boolean isLoading = false;
    private boolean hasMoreData = true;

    public ScheduledRidesFragment() {}

    public static ScheduledRidesFragment newInstance() {
        ScheduledRidesFragment fragment = new ScheduledRidesFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        count = (int) Math.ceil(displayMetrics.heightPixels / 200.0 * 0.7);

        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String idStr = prefs.getString("id", null);
        if (idStr != null) {
            userId = Long.parseLong(idStr);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ride_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView title = getView().findViewById(R.id.ride_list_title);
        title.setText("Scheduled Rides");
        getView().findViewById(R.id.user_filter).setVisibility(View.GONE);


        getView().findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        RecyclerView cards = getView().findViewById(R.id.ride_list_cards);
        cards.setAdapter(new RideListAdapter(List.of(), this));
        cards.setLayoutManager(new LinearLayoutManager(getActivity()));

        cards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!cards.canScrollVertically(1) && !isLoading && hasMoreData) {
                    isLoading = true;
                    try {
                        RideService.getInstance().getScheduledRides(requireContext(), userId, ++page, count, rides -> {
                            isLoading = false;
                            if (rides.isEmpty()) {
                                hasMoreData = false;
                                return;
                            }
                            ((RideListAdapter) cards.getAdapter()).updateItems(rides);
                        });
                    } catch (Exception e) {
                        isLoading = false;
                        Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        resetCards();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void resetCards() {
        page = 0;
        isLoading = false;
        hasMoreData = true;
        try {
            RideService.getInstance().getScheduledRides(getContext(), userId, page, count, rides -> {
                if (getView() == null) return;
                RecyclerView cards = getView().findViewById(R.id.ride_list_cards);
                ((RideListAdapter) cards.getAdapter()).updateItems(rides);
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClicked(RideCardDto ride) {
        MainActivity activity = (MainActivity) getActivity();
        Fragment f = RideDetailsFragment.newInstance(ride.getId(), false, true);
        activity.showFragment(f);
    }
}