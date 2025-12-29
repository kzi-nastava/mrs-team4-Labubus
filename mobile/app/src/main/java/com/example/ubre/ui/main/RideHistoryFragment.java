package com.example.ubre.ui.main;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import com.example.ubre.R;
import com.example.ubre.ui.adapters.RideListAdapter;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.dtos.VehicleDto;
import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.enums.VehicleType;
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.dtos.UserDto;
import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;

public class RideHistoryFragment extends Fragment implements RideListAdapter.OnItemClickedListener {
    private  UserDto currentUser;
    private DatePickerDialog datePickerDialog;
    private Button dateFilterButton;
    private ListView sortList;
    private MaterialButton sortSpinner;
    private LocalDate filterDate;
    private boolean sortAscending = false;

    public RideHistoryFragment() {

    }

    public static RideHistoryFragment newInstance(UserDto user) {
        RideHistoryFragment rideHistory = new RideHistoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("USER", user);
        rideHistory.setArguments(args);
        return rideHistory;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            currentUser = (UserDto)getArguments().getSerializable("USER");

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener eventHandler = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                filterDate = LocalDate.of(year, month, dayOfMonth);
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

        View driverFilter = this.getView().findViewById(R.id.driver_filter);
        driverFilter.setVisibility(currentUser.getRole() == Role.ADMIN ? View.VISIBLE : View.GONE);

        sortSpinner = this.getView().findViewById(R.id.sort_field);
        sortList = this.getView().findViewById(R.id.sorting_options);
        sortList.setAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1,
                Arrays.stream(RideDto.class.getDeclaredFields()).map(Field::getName).toArray(String[]::new)));
        sortList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                rotate(v, sortAscending ? 180 : 360);
            }
        });

        this.getView().findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        com.example.ubre.ui.dtos.UserDto placeholder = new com.example.ubre.ui.dtos.UserDto(1L, com.example.ubre.ui.enums.Role.REGISTERED_USER, "", "mail@mail.com", "Pera", "Peric", "0124120412041", "Adresa 123" );
        RideDto[] rides = {
                new RideDto(1L, LocalDateTime.now(), LocalDateTime.now(), new WaypointDto[]{
                        new WaypointDto(1L, "Bulevar despota stefana", 46.17, 19.32),
                        new WaypointDto(2L, "Narodno pozoriste", 46.17, 19.32),
                        new WaypointDto(3L, "Bulevar oslobodjenja", 46.17, 19.32)
                }, placeholder, new com.example.ubre.ui.dtos.UserDto[]{placeholder, placeholder}, false, "mail@mail.com",
                        new VehicleDto(1L, "Ford F-150", VehicleType.LUXURY, "12312123132", 5, true, false),
                        19.3, 4.1),
                new RideDto(2L, LocalDateTime.now(), LocalDateTime.now(), new WaypointDto[]{
                        new WaypointDto(4L, "Bulevar cara Lazara", 46.17, 19.32),
                        new WaypointDto(5L, "Spens", 46.17, 19.32),
                        new WaypointDto(6L, "Trg maldenaca", 46.17, 19.32)
                }, placeholder, new com.example.ubre.ui.dtos.UserDto[]{placeholder, placeholder}, true, "",
                        new VehicleDto(1L, "Honda Civic", VehicleType.VAN, "12312123132", 5, true, false),
                        19.3, 12.1),
                new RideDto(3L, LocalDateTime.now(), LocalDateTime.now(), new WaypointDto[]{
                        new WaypointDto(4L, "Most slobode", 46.17, 19.32),
                        new WaypointDto(6L, "Jevrejska", 46.17, 19.32)
                }, placeholder, new com.example.ubre.ui.dtos.UserDto[]{placeholder, placeholder}, false, "mail@mail.com",
                        new VehicleDto(1L, "Toyota Carolla 2021", VehicleType.STANDARD, "12312123132", 5, true, false),
                        19.3, 3.1),
                new RideDto(4L, LocalDateTime.now(), LocalDateTime.now(), new WaypointDto[]{
                        new WaypointDto(4L, "Limanski park", 46.17, 19.32),
                        new WaypointDto(5L, "Kisacka", 46.17, 19.32),
                        new WaypointDto(5L, "Partizanska", 46.17, 19.32),
                        new WaypointDto(6L, "Temerinski put", 46.17, 19.32)
                }, placeholder, new com.example.ubre.ui.dtos.UserDto[]{placeholder, placeholder}, false, "dsadasdasda",
                        new VehicleDto(1L, "Toyota Carolla 2021", VehicleType.STANDARD, "12312123132", 5, true, false),
                        19.3, 27.4),
                new RideDto(5L, LocalDateTime.now(), LocalDateTime.now(), new WaypointDto[]{
                        new WaypointDto(1L, "Bulevar despota stefana", 46.17, 19.32),
                        new WaypointDto(2L, "Narodno pozoriste", 46.17, 19.32),
                        new WaypointDto(3L, "Bulevar oslobodjenja", 46.17, 19.32)
                }, placeholder, new com.example.ubre.ui.dtos.UserDto[]{placeholder, placeholder}, true, "",
                        new VehicleDto(1L, "Ford F-150", VehicleType.LUXURY, "12312123132", 5, true, false),
                        19.3, 3.5),
                new RideDto(6L, LocalDateTime.now(), LocalDateTime.now(), new WaypointDto[]{
                        new WaypointDto(4L, "Bulevar cara Lazara", 46.17, 19.32),
                        new WaypointDto(5L, "Spens", 46.17, 19.32),
                        new WaypointDto(6L, "Trg maldenaca", 46.17, 19.32)
                }, placeholder, new com.example.ubre.ui.dtos.UserDto[]{placeholder, placeholder}, false, "",
                        new VehicleDto(1L, "Honda Civic", VehicleType.VAN, "12312123132", 5, true, false),
                        19.3, 1.4),
                new RideDto(7L, LocalDateTime.now(), LocalDateTime.now(), new WaypointDto[]{
                        new WaypointDto(4L, "Most slobode", 46.17, 19.32),
                        new WaypointDto(6L, "Jevrejska", 46.17, 19.32)
                }, placeholder, new com.example.ubre.ui.dtos.UserDto[]{placeholder, placeholder}, false, "",
                        new VehicleDto(1L, "Toyota Carolla 2021", VehicleType.STANDARD, "12312123132", 5, true, false),
                        19.3, 4.3),
                new RideDto(8L, LocalDateTime.now(), LocalDateTime.now(), new WaypointDto[]{
                        new WaypointDto(4L, "Limanski park", 46.17, 19.32),
                        new WaypointDto(5L, "Kisacka", 46.17, 19.32),
                        new WaypointDto(5L, "Partizanska", 46.17, 19.32),
                        new WaypointDto(6L, "Temerinski put", 46.17, 19.32)
                }, placeholder, new com.example.ubre.ui.dtos.UserDto[]{placeholder, placeholder}, true, "dasdasdasd",
                        new VehicleDto(1L, "Ford F-150", VehicleType.LUXURY, "12312123132", 5, true, false),
                        19.3, 9.4),
        };
        RecyclerView cards = this.getView().findViewById(R.id.ride_list_cards);
        cards.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        cards.setAdapter(new RideListAdapter(rides, this));
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

    @Override
    public void onItemClicked(RideDto ride) {
        MainActivity activity = (MainActivity) this.getActivity();
        Fragment f = RideDetailsFragment.newInstance(ride, currentUser);
        activity.showFragment(f);
    }
}