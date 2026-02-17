package com.example.ubre.ui.storages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.RideDto;

public class CurrentRideStorage {

    private static CurrentRideStorage instance;

    private final MutableLiveData<RideDto> currentRide = new MutableLiveData<>(null);

    public static CurrentRideStorage getInstance() {
        if (instance == null) {
            instance = new CurrentRideStorage();
        }
        return instance;
    }

    public LiveData<RideDto> getCurrentRideReadOnly() {
        return currentRide;
    }

    public void setCurrentRide(RideDto ride) {
        currentRide.setValue(ride);
    }

    public void clear() {
        currentRide.setValue(null);
    }
}
