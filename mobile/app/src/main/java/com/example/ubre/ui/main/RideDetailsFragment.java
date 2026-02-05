package com.example.ubre.ui.main;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.dtos.VehicleDto;
import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.enums.Role;

import java.time.format.DateTimeFormatter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RideDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RideDetailsFragment extends Fragment {


    public RideDetailsFragment() {

    }

    public static RideDetailsFragment newInstance(RideDto ride, UserDto currentUser) {
        RideDetailsFragment fragment = new RideDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("RIDE", ride);
        args.putSerializable("USER", currentUser);
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
            RideDto ride = (RideDto) getArguments().getSerializable("RIDE");
            UserDto user = (UserDto) getArguments().getSerializable("USER");
            Typeface font = ResourcesCompat.getFont(this.getActivity(), R.font.poppins_regular);

            TextView start = root.findViewById(R.id.ride_details_start);
            start.setText(ride.getStartTime().format(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm")));

            TextView end = root.findViewById(R.id.ride_details_end);
            end.setText(ride.getEndTime().format(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm")));

            LinearLayout waypoints = root.findViewById(R.id.ride_details_waypoints);
            LinearLayout firstRow = new LinearLayout(this.getActivity());
            firstRow.setGravity(Gravity.CENTER_VERTICAL);

            ImageView firstWaypointIcon = new ImageView(this.getActivity());
            firstWaypointIcon.setBackgroundResource(R.drawable.ic_circle_primary);
            firstWaypointIcon.setLayoutParams(new LinearLayout.LayoutParams(toDP(20), toDP(20)));
            firstRow.addView(firstWaypointIcon);

            TextView firstWaypointLabel = new TextView(this.getActivity());
//            firstWaypointLabel.setText(ride.getWaypoints().getFirst().getLabel());
            firstWaypointLabel.setTypeface(font);
            firstWaypointLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            firstWaypointLabel.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams firstParam = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            firstParam.setMargins(toDP(10), 0, 0, 0);
            firstWaypointLabel.setLayoutParams(firstParam);
            firstRow.addView(firstWaypointLabel);

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
                waypointLabel.setText(waypoint.getLabel());
                waypointLabel.setTypeface(font);
                waypointLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                LinearLayout.LayoutParams waypointParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                waypointParams.setMargins(toDP(10), 0, 0, 0);
                waypointLabel.setLayoutParams(waypointParams);
                row.addView(waypointLabel);

                waypoints.addView(separatorRow);
                waypoints.addView(row);
            }

            TextView distance = root.findViewById(R.id.ride_details_distance);
            distance.setText(String.format("%.1fkm", ride.getDistance()));

            LinearLayout markers = root.findViewById(R.id.ride_details_markers);
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
            if (!ride.getCanceledBy().equals(null)) {
                ImageView cancelIndicator = new ImageView(this.getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(toDP(30), toDP(30));
                params.setMargins(toDP(5), 0, 0, 0);
                cancelIndicator.setLayoutParams(params);
                cancelIndicator.setBackgroundResource(ride.getCanceledBy().equals(ride.getDriver().getEmail()) ? R.drawable.bg_icon_yellow : R.drawable.bg_icon_red);
                cancelIndicator.setImageResource(R.drawable.ic_cancel);
                cancelIndicator.setPadding(toDP(4), toDP(4), toDP(4), toDP(4));
                markers.addView(cancelIndicator);
            }

            if (user.getRole() == Role.ADMIN || user.getRole() == Role.DRIVER) {
                for (int i = 0; i < ride.getPassengers().size(); i++) {
                    UserDto passenger = ride.getPassengers().get(i);
                    ProfileCardFragment profileCard = ProfileCardFragment.newInstance(passenger.getAvatarUrl(), passenger.getName(), "", i == 0 ? R.drawable.ic_ordering_customer : -1);
                    this.getActivity().getSupportFragmentManager().beginTransaction().add(R.id.ride_details_passengers, profileCard).commit();
                }
            }
            else
                root.findViewById(R.id.ride_details_passengers).setVisibility(View.GONE);

            if (user.getRole() == Role.ADMIN || user.getRole() == Role.REGISTERED_USER) {
                UserDto driver = ride.getDriver();
                ProfileCardFragment profileCard = ProfileCardFragment.newInstance(driver.getAvatarUrl(), driver.getName(), "", R.drawable.ic_review);
                this.getActivity().getSupportFragmentManager().beginTransaction().add(R.id.ride_details_driver, profileCard).commit();

//                VehicleDto vehicle = ride.getVehicle();
//                profileCard = ProfileCardFragment.newInstance("", vehicle.getModel(), vehicle.getType().name(), -1);
//                this.getActivity().getSupportFragmentManager().beginTransaction().add(R.id.ride_details_vehicle, profileCard).commit();
            }
            else {
                root.findViewById(R.id.ride_details_vehicle).setVisibility(View.GONE);
                root.findViewById(R.id.ride_details_driver).setVisibility(View.GONE);
            }

            LinearLayout priceCard = root.findViewById(R.id.ride_details_price);
            View card = inflater.inflate(R.layout.stat_card, priceCard, false);
            TextView tvValue = card.findViewById(R.id.stat_value);
            TextView tvLabel = card.findViewById(R.id.stat_label);
            tvValue.setText(String.format("$%.2f", ride.getPrice()));
            tvLabel.setText("Final price");
            priceCard.addView(card);

//            if (!user.getEmail().equals(ride.getPassengers().getFirst().getEmail()))
//                root.findViewById(R.id.ride_details_reorder).setVisibility(View.GONE);
        }

        return root;
    }

    private int toDP(int value) {
        return  (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  value, this.getResources().getDisplayMetrics());
    }
}