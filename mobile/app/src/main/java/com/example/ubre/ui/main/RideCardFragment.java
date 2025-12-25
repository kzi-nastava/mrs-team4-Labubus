package com.example.ubre.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ubre.R;
import com.example.ubre.ui.model.RideDto;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_card, container, false);
    }
}