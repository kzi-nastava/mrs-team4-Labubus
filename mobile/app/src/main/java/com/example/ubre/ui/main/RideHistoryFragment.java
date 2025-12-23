package com.example.ubre.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ubre.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RideHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RideHistoryFragment extends Fragment {

    public RideHistoryFragment() {
        // Required empty public constructor
    }

    public static RideHistoryFragment newInstance() {
        return new RideHistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ride_list, container, false);
    }
}