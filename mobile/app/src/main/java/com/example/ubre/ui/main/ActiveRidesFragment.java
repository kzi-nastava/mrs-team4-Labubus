package com.example.ubre.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubre.R;
import com.example.ubre.ui.adapters.RideListAdapter;
import com.example.ubre.ui.dtos.RideCardDto;
import com.example.ubre.ui.services.RideService;
import com.example.ubre.ui.services.UserService;
import com.example.ubre.ui.storages.ActiveRidesStorage;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ActiveRidesFragment extends Fragment implements RideListAdapter.OnItemClickedListener {
    private ListView sortList;
    private MaterialButton sortSpinner;
    private boolean sortAscending = false;
    private String sortBy;
    private Long userId;
    private Integer page;
    private Integer count;

    public ActiveRidesFragment() {}

    public static ActiveRidesFragment newInstance() {
        ActiveRidesFragment fragment = new ActiveRidesFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        count = (int) Math.ceil(displayMetrics.heightPixels / 200.0 * 0.7);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ride_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        TextView title = getView().findViewById(R.id.ride_list_title);
        title.setText(R.string.active_rides);

        // Hide date filter — active rides don't need it (matching frontend)
        getView().findViewById(R.id.date_filter_button).setVisibility(View.GONE);

        // User filter
        EditText userFilter = getView().findViewById(R.id.user_filter);
        userFilter.setVisibility(View.VISIBLE);
        userFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchName = s.toString();
                if (searchName.isEmpty()) {
                    ActiveRidesStorage.getInstance().setFilterUsers(List.of());
                    userId = null;
                    resetCards();
                } else {
                    try {
                        UserService.getInstance(getContext()).searchActiveRideFilterUsers(searchName);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("USER FETCH", e.getMessage());
                    }
                }
            }
        });

        ListView userOptions = getView().findViewById(R.id.filtering_options);
        ActiveRidesStorage.getInstance().getFilterUsersReadOnly().observe(this, (users) -> {
            if (users.isEmpty() || userId != null)
                userOptions.setVisibility(View.GONE);
            else
                userOptions.setVisibility(View.VISIBLE);

            userOptions.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                    users.stream().map((user) -> user.getName() + " " + user.getSurname()).toArray(String[]::new)));
            userOptions.setOnItemClickListener((parent, view, position, id) -> {
                userOptions.setVisibility(View.GONE);
                userId = users.get(position).getId();
                resetCards();
                userFilter.setText((String) parent.getItemAtPosition(position));
                ActiveRidesStorage.getInstance().setFilterUsers(List.of());
            });
        });

        // Sort
        sortSpinner = getView().findViewById(R.id.sort_field);
        sortList = getView().findViewById(R.id.sorting_options);
        sortList.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                new String[]{"Start Time", "Price", "Distance", "Driver"}));
        sortList.setOnItemClickListener((parent, view, position, id) -> {
            String fieldName = ((String) parent.getItemAtPosition(position)).replace(" ", "");
            sortBy = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
            resetCards();
            sortSpinner.setText((String) parent.getItemAtPosition(position));
            sortSpinner.callOnClick();
        });

        sortSpinner.setOnClickListener(v -> {
            if (View.VISIBLE == sortList.getVisibility()) {
                sortList.setVisibility(View.GONE);
                sortSpinner.setIconResource(R.drawable.ic_arrow_down);
            } else {
                sortList.setVisibility(View.VISIBLE);
                sortSpinner.setIconResource(R.drawable.ic_arrow_up);
            }
        });

        getView().findViewById(R.id.sort_direction).setOnClickListener(v -> {
            sortAscending = !sortAscending;
            resetCards();
            v.animate().rotation(sortAscending ? 180 : 360)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(200).start();
        });

        getView().findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        RecyclerView cards = getView().findViewById(R.id.ride_list_cards);
        cards.setAdapter(new RideListAdapter(List.of(), this));
        cards.setLayoutManager(new LinearLayoutManager(getActivity()));
        ActiveRidesStorage.getInstance().getActiveRidesReadOnly().observe(this, rides -> {
            ((RideListAdapter) cards.getAdapter()).updateItems(rides);
        });

        cards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!cards.canScrollVertically(1)) {
                    try {
                        RideService.getInstance().getActiveRidesPage(getContext(), userId, ++page, count, sortBy, sortAscending);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ACTIVE RIDES FETCH", e.getMessage());
                    }
                }
            }
        });

        resetCards();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ActiveRidesStorage.getInstance().setFilterUsers(List.of());
        ActiveRidesStorage.getInstance().clearActiveRides();
    }

    private void resetCards() {
        page = 0;
        ActiveRidesStorage.getInstance().clearActiveRides();
        try {
            RideService.getInstance().getActiveRidesPage(getContext(), userId, page, count, sortBy, sortAscending);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ACTIVE RIDES FETCH", e.getMessage());
        }
    }

    @Override
    public void onItemClicked(RideCardDto ride) {
        MainActivity activity = (MainActivity) getActivity();
        Fragment f = RideDetailsFragment.newInstance(ride.getId());
        activity.showFragment(f);
    }
}
