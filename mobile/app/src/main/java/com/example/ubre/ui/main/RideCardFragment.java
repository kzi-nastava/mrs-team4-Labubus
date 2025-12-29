package com.example.ubre.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ubre.R;
import com.example.ubre.ui.adapters.RideListAdapter;
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
        time.setText(ride.getStart().format(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm")));

        TextView start = this.getView().findViewById(R.id.ride_card_waypoint1);
        start.setText(ride.getWaypoints()[0].getLabel());

        TextView end = this.getView().findViewById(R.id.ride_card_waypoint1);
        end.setText(ride.getWaypoints()[ride.getWaypoints().length - 1].getLabel());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_card, container, false);
    }
}