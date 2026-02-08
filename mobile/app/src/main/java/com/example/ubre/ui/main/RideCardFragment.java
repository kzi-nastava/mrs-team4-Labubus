package com.example.ubre.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.RideDto;

import java.time.format.DateTimeFormatter;

public class RideCardFragment extends Fragment {

    public RideCardFragment() {

    }

    public static RideCardFragment newInstance(RideDto ride) {
        RideCardFragment fragment = new RideCardFragment();
        Bundle args = new Bundle();
        args.putSerializable("RIDE", ride);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RideDto ride = (RideDto)savedInstanceState.getSerializable("RIDE");
        if (ride == null)
            return;

        TextView time = this.getView().findViewById(R.id.ride_card_start);
        time.setText(ride.getStartTime().format(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm")));

        TextView start = this.getView().findViewById(R.id.ride_card_waypoint1);
        String firstStop = ride.getWaypoints().getFirst().getLabel();
        start.setText(firstStop.length() > 30 ? firstStop.substring(0, 27) + "..." : firstStop);

        TextView end = this.getView().findViewById(R.id.ride_card_waypoint1);
        String lastStop = ride.getWaypoints().getLast().getLabel();
        end.setText(lastStop.length() > 30 ? lastStop.substring(0, 27) + "..." : lastStop);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_card, container, false);
    }
}