package com.example.ubre.ui.storages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.dtos.VehicleDto;

public class RideDetailsStorage {

    private static RideDetailsStorage instance;

    private MutableLiveData<RideDto> selectedRide = new MutableLiveData<>(null);

    private MutableLiveData<VehicleDto> selectedRideVehicle = new MutableLiveData<>(null);

    public static RideDetailsStorage getInstance() {
        if (instance == null)
            instance = new RideDetailsStorage();
        return instance;
    }

    public LiveData<RideDto> getSelectedRideReadOnly() {return selectedRide;}


    public void setSelectedRide(RideDto ride) {
        selectedRide.setValue(ride);
    }

    public LiveData<VehicleDto> getSelectedRideVehicleReadOnly() {return selectedRideVehicle;}

    public void setSelectedRideVehicle(VehicleDto vehicle) {
        selectedRideVehicle.setValue(vehicle);
    }
}
