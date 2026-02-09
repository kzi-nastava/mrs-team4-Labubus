package com.example.ubre.ui.main;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.util.DisplayMetrics;

import com.example.ubre.R;
import com.example.ubre.ui.adapters.RideListAdapter;
import com.example.ubre.ui.dtos.RideCardDto;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.dtos.VehicleDto;
import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.enums.RideStatus;
import com.example.ubre.ui.enums.UserStatus;
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.services.RideService;
import com.example.ubre.ui.services.UserService;
import com.example.ubre.ui.storages.RideHistoryStorage;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class RideHistoryFragment extends Fragment implements RideListAdapter.OnItemClickedListener {
    private DatePickerDialog datePickerDialog;
    private Button dateFilterButton;
    private ListView sortList;
    private MaterialButton sortSpinner;
    private LocalDate filterDate;
    private boolean sortAscending = false;
    private String sortBy;
    private Long userId;
    private Integer page;
    private Integer count;

    public RideHistoryFragment() {

    }

    public static RideHistoryFragment newInstance() {
        RideHistoryFragment rideHistory = new RideHistoryFragment();
        Bundle args = new Bundle();
        rideHistory.setArguments(args);
        return rideHistory;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        count = (int) Math.ceil(displayMetrics.heightPixels / 200.0 * 0.7);

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener eventHandler = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                filterDate = LocalDate.of(year, month+1, dayOfMonth);
                resetCards();
                dateFilterButton.setText(filterDate.format(DateTimeFormatter.ofPattern("d MMM yyyy")));
            }
        };

        datePickerDialog = new DatePickerDialog(getActivity(), eventHandler, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onStart() {
        super.onStart();

        dateFilterButton = this.getView().findViewById(R.id.date_filter_button);
        dateFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        EditText userFilter = this.getView().findViewById(R.id.user_filter);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");
        if (role.equals("ADMIN")) {
            userFilter.setVisibility(View.VISIBLE);
            userFilter.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String searchName = s.toString();
                    if (searchName.isEmpty()) {
                        RideHistoryStorage.getInstance().setFilterUsers(List.of());
                        userId = null;
                        resetCards();
                    }
                    else {
                        try {
                            UserService.getInstance(getContext()).searchFilterUsers(searchName);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("USER FETCH", e.getMessage());
                        }
                    }
                }
            });
        }
        else
            userFilter.setVisibility(View.GONE);

        ListView userOptions = this.getView().findViewById(R.id.filtering_options);
        RideHistoryStorage.getInstance().getFilterUsersReadOnly().observe(this, (users) -> {
            if (users.isEmpty() || userId != null)
                userOptions.setVisibility(View.GONE);
            else
                userOptions.setVisibility(View.VISIBLE);

            userOptions.setAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1,
                    users.stream().map((user) -> user.getName() + " " + user.getSurname()).toArray(String[]::new)));
            userOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    userOptions.setVisibility(View.GONE);
                    userId = users.get(position).getId();
                    resetCards();
                    userFilter.setText((String)parent.getItemAtPosition(position));
                    RideHistoryStorage.getInstance().setFilterUsers(List.of());
                }
            });
        });

        sortSpinner = this.getView().findViewById(R.id.sort_field);
        sortList = this.getView().findViewById(R.id.sorting_options);
        sortList.setAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1,
                new String[]{"Start Time", "End Time", "Price", "Distance", "Driver"}));
        sortList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fieldName = ((String)parent.getItemAtPosition(position)).replace(" ", "");
                sortBy = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                resetCards();
                sortSpinner.setText((String)parent.getItemAtPosition(position));
                sortSpinner.callOnClick();
            }
        });

        sortSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (View.VISIBLE == sortList.getVisibility()) {
                    sortList.setVisibility(View.GONE);
                    sortSpinner.setIconResource(R.drawable.ic_arrow_down);
                }
                else {
                    sortList.setVisibility(View.VISIBLE);
                    sortSpinner.setIconResource(R.drawable.ic_arrow_up);
                }
            }
        });

        this.getView().findViewById(R.id.sort_direction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortAscending = !sortAscending;
                resetCards();
                rotate(v, sortAscending ? 180 : 360);
            }
        });

        this.getView().findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        RecyclerView cards = this.getView().findViewById(R.id.ride_list_cards);
        cards.setAdapter(new RideListAdapter(List.of(), this));
        cards.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        RideHistoryStorage.getInstance().getHistoryReadOnly().observe(this, rides -> {
            ((RideListAdapter) cards.getAdapter()).updateItems(rides);
        });

        cards.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!cards.canScrollVertically(1)) {
                    try {
                        RideService.getInstance().getHistoryPage(getContext(), userId, ++page, count, sortBy, sortAscending, filterDate);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("HISTORY FETCH", e.getMessage());
                    }
                }
            }
        });

        resetCards();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RideHistoryStorage.getInstance().setFilterUsers(List.of());
        RideHistoryStorage.getInstance().clearHistory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ride_list, container, false);
    }

    public void rotate(View view, float amount) {
        view.animate().rotation(amount)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    private  void resetCards() {
        page = 0;
        RideHistoryStorage.getInstance().clearHistory();
        try {
            RideService.getInstance().getHistoryPage(getContext(), userId, page, count, sortBy, sortAscending, filterDate);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("HISTORY FETCH", e.getMessage());
        }

    }

    @Override
    public void onItemClicked(RideCardDto ride) {
        MainActivity activity = (MainActivity) this.getActivity();
        Fragment f = RideDetailsFragment.newInstance(ride.getId());
        activity.showFragment(f);
    }
}