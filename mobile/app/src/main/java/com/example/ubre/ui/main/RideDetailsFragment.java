package com.example.ubre.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.dtos.VehicleDto;
import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.services.RideService;
import com.example.ubre.ui.services.UserService;
import com.example.ubre.ui.services.VehicleService;
import com.example.ubre.ui.storages.ReviewStorage;
import com.example.ubre.ui.storages.RideDetailsStorage;
import com.example.ubre.ui.storages.RideHistoryStorage;
import com.example.ubre.ui.storages.UserStorage;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RideDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RideDetailsFragment extends Fragment {

    public RideDetailsFragment() {

    }

    public static RideDetailsFragment newInstance(Long rideId) {
        RideDetailsFragment fragment = new RideDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("RIDEID", rideId);
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
        View root = inflater.inflate(R.layout.ride_details, container, false);

        if (getArguments() != null) {
            Long rideId = (Long) getArguments().getSerializable("RIDEID");
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String role = sharedPreferences.getString("role", "");
            Typeface font = ResourcesCompat.getFont(this.getActivity(), R.font.poppins_regular);

            ScrollView bottomDrawer = root.findViewById(R.id.ride_details_bottom_drawer);
            bottomDrawer.setOnTouchListener(new View.OnTouchListener() {
                private int startHeight;
                private float lastTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (event.getY() > 60) {
                                startHeight = 0;
                                lastTouchY = 0;
                                return false;
                            }

                            startHeight = v.getHeight();
                            lastTouchY = event.getRawY();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (startHeight == 0 || lastTouchY == 0)
                                return false;

                            float dy = event.getRawY() - lastTouchY;
                            int newHeight = Math.max((int) (startHeight - dy), 140);

                            ConstraintSet constraints = new ConstraintSet();
                            constraints.clone((ConstraintLayout) root);
                            constraints.constrainPercentHeight(R.id.ride_details_bottom_drawer, Math.min((float) newHeight / displayMetrics.heightPixels, 0.7f));
                            constraints.applyTo((ConstraintLayout) root);

                            lastTouchY = event.getRawY();
                            startHeight = newHeight;
                            break;
                    }

                    return true;
                }
            });

            LinearLayout waypoints = root.findViewById(R.id.ride_details_waypoints);
            LinearLayout firstRow = new LinearLayout(this.getActivity());
            firstRow.setGravity(Gravity.CENTER_VERTICAL);

            ImageView firstWaypointIcon = new ImageView(this.getActivity());
            firstWaypointIcon.setBackgroundResource(R.drawable.ic_circle_primary);
            firstWaypointIcon.setLayoutParams(new LinearLayout.LayoutParams(toDP(20), toDP(20)));
            firstRow.addView(firstWaypointIcon);

            TextView firstWaypointLabel = new TextView(this.getActivity());
            firstWaypointLabel.setTypeface(font);
            firstWaypointLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            firstWaypointLabel.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams firstParam = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            firstParam.setMargins(toDP(10), 0, 0, 0);
            firstWaypointLabel.setLayoutParams(firstParam);
            firstRow.addView(firstWaypointLabel);

            LinearLayout priceCard = root.findViewById(R.id.ride_details_price);
            View card = inflater.inflate(R.layout.stat_card, priceCard, false);
            TextView tvLabel = card.findViewById(R.id.stat_label);
            tvLabel.setText("Final price");
            priceCard.addView(card);

            RideDetailsStorage.getInstance().getSelectedRideReadOnly().observe(this, (RideDto ride) -> {
                if (ride == null)
                    return;

                try {
                    VehicleService.getInstance().getSelectedRideVehicle(getContext());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                TextView start = root.findViewById(R.id.ride_details_start);
                start.setText(ride.getStartTime().format(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm")));

                TextView end = root.findViewById(R.id.ride_details_end);
                end.setText(ride.getEndTime().format(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm")));

                waypoints.removeAllViews();

                String firstWaypointLabelText = ride.getWaypoints().get(0).getLabel();
                firstWaypointLabel.setText(firstWaypointLabelText.length() > 30 ? firstWaypointLabelText.substring(0, 27) + "..." : firstWaypointLabelText);
                waypoints.addView(firstRow);

                for (int i = 1; i < ride.getWaypoints().size(); ++i) {
                    WaypointDto waypoint = ride.getWaypoints().get(i);
                    LinearLayout separatorRow = new LinearLayout(this.getActivity());
                    separatorRow.setGravity(Gravity.CENTER_VERTICAL);

                    ImageView dots = new ImageView(this.getActivity());
                    dots.setBackgroundResource(R.drawable.ic_dots);
                    dots.setLayoutParams(new LinearLayout.LayoutParams(toDP(20), toDP(20)));
                    separatorRow.addView(dots);

                    TextView separator = new TextView(this.getActivity());
                    separator.setBackgroundColor(Color.rgb(218, 218, 218));
                    LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toDP(1)));
                    separatorParams.setMargins(toDP(10), 0, toDP(30), 0);
                    separator.setLayoutParams(separatorParams);
                    separatorRow.addView(separator);

                    LinearLayout row = new LinearLayout(this.getActivity());
                    row.setGravity(Gravity.CENTER_VERTICAL);

                    ImageView waypointIcon = new ImageView(this.getActivity());
                    waypointIcon.setBackgroundResource(R.color.primary);
                    waypointIcon.setLayoutParams(new LinearLayout.LayoutParams(toDP(20), toDP(20)));
                    row.addView(waypointIcon);

                    TextView waypointLabel = new TextView(this.getActivity());
                    waypointLabel.setText(waypoint.getLabel().length() > 30 ? waypoint.getLabel().substring(0, 27) + "..." : waypoint.getLabel());
                    waypointLabel.setTypeface(font);
                    waypointLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    LinearLayout.LayoutParams waypointParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    waypointParams.setMargins(toDP(10), 0, 0, 0);
                    waypointLabel.setLayoutParams(waypointParams);
                    row.addView(waypointLabel);

                    waypoints.addView(separatorRow);
                    waypoints.addView(row);
                }

                LinearLayout markers = root.findViewById(R.id.ride_details_markers);
                markers.removeAllViews();
                if (ride.getPanic()) {
                    ImageView panicIndicator = new ImageView(this.getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(toDP(30), toDP(30));
                    params.setMargins(toDP(5), 0, 0, 0);
                    panicIndicator.setLayoutParams(params);
                    panicIndicator.setBackgroundResource(R.drawable.bg_icon_red);
                    panicIndicator.setImageResource(R.drawable.ic_warning_surface);
                    panicIndicator.setPadding(toDP(4), toDP(4), toDP(4), toDP(4));
                    markers.addView(panicIndicator);
                }
                if (ride.getCanceledBy() != null) {
                    ImageView cancelIndicator = new ImageView(this.getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(toDP(30), toDP(30));
                    params.setMargins(toDP(5), 0, 0, 0);
                    cancelIndicator.setLayoutParams(params);
                    cancelIndicator.setBackgroundResource(ride.getCanceledBy().equals(ride.getDriver().getEmail()) ? R.drawable.bg_icon_yellow : R.drawable.bg_icon_red);
                    cancelIndicator.setImageResource(R.drawable.ic_cancel);
                    cancelIndicator.setPadding(toDP(4), toDP(4), toDP(4), toDP(4));
                    markers.addView(cancelIndicator);
                }

                LinearLayout driverInfo = root.findViewById(R.id.ride_details_driver);
                LinearLayout vehicleInfo = root.findViewById(R.id.ride_details_vehicle);
                driverInfo.removeAllViews();
                vehicleInfo.removeAllViews();
                if (role.equals("ADMIN") || role.equals("REGISTERED_USER") && ride.getDriver() != null) {
                    UserDto driver = ride.getDriver();
                    ProfileCardFragment profileCard = ProfileCardFragment.newInstance(driver.getId(), driver.getName(), "", R.drawable.ic_review);
                    root.findViewById(R.id.ride_details_driver_section).setVisibility(View.VISIBLE);
                    this.getActivity().getSupportFragmentManager().beginTransaction().add(R.id.ride_details_driver, profileCard).commit();
                }
                else {
                    root.findViewById(R.id.ride_details_vehicle_section).setVisibility(View.GONE);
                    root.findViewById(R.id.ride_details_driver_section).setVisibility(View.GONE);
                }

                LinearLayout passengers = root.findViewById(R.id.ride_details_passengers);
                passengers.removeAllViews();
                if (role.equals("ADMIN") || role.equals("DRIVER") && !ride.getPassengers().isEmpty()) {
                    for (int i = 0; i < ride.getPassengers().size(); i++) {
                        UserDto passenger = ride.getPassengers().get(i);
                        ProfileCardFragment profileCard = ProfileCardFragment.newInstance(passenger.getId(), passenger.getName(), "", i == 0 ? R.drawable.ic_ordering_customer : -1);
                        root.findViewById(R.id.ride_details_passenger_section).setVisibility(View.VISIBLE);
                        this.getActivity().getSupportFragmentManager().beginTransaction().add(R.id.ride_details_passengers, profileCard).commit();
                    }
                }
                else
                    root.findViewById(R.id.ride_details_passenger_section).setVisibility(View.GONE);

                TextView distance = root.findViewById(R.id.ride_details_distance);
                distance.setText(String.format("%.1fm", ride.getDistance()));

                TextView tvValue = card.findViewById(R.id.stat_value);
                tvValue.setText(String.format("$%.2f", ride.getPrice()));

                if (UserStorage.getInstance().getCurrentUser().getValue().getId().equals(ride.getCreatedBy()));
                root.findViewById(R.id.ride_details_reorder).setVisibility(View.GONE);
            });

            if (role.equals("ADMIN") || role.equals("REGISTERED_USER")) {
                RideDetailsStorage.getInstance().getSelectedRideVehicleReadOnly().observe(this, (vehicle) -> {
                    if (vehicle == null)
                        return;

                    ProfileCardFragment profileCard = ProfileCardFragment.newInstance(-1L, vehicle.getModel(), vehicle.getType().name(), -1);
                    root.findViewById(R.id.ride_details_vehicle_section).setVisibility(View.VISIBLE);
                    this.getActivity().getSupportFragmentManager().beginTransaction().add(R.id.ride_details_vehicle, profileCard).commit();
                });
                try {
                    VehicleService.getInstance().getSelectedRideVehicle(getContext());
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("VEHICLE FETCH", e.getMessage());
                }
            }

            try {
                RideService.getInstance().getRideDetails(getContext(), rideId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RideDetailsStorage.getInstance().setSelectedRideVehicle(null);
        RideDetailsStorage.getInstance().setSelectedRide(null);
    }

    private int toDP(int value) {
        return  (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  value, this.getResources().getDisplayMetrics());
    }
}