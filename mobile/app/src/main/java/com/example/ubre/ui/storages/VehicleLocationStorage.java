package com.example.ubre.ui.storages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.VehicleIndicatorDto;

import java.util.ArrayList;
import java.util.List;

public class VehicleLocationStorage {

    private static VehicleLocationStorage instance;

    private final MutableLiveData<List<VehicleIndicatorDto>> locations = new MutableLiveData<>(new ArrayList<>());
    private Long followDriverId = null;

    public static VehicleLocationStorage getInstance() {
        if (instance == null) {
            instance = new VehicleLocationStorage();
        }
        return instance;
    }

    public LiveData<List<VehicleIndicatorDto>> getLocationsReadOnly() {
        return locations;
    }

    public void setLocations(List<VehicleIndicatorDto> indicators) {
        locations.setValue(indicators);
    }

    public Long getFollowDriverId() {
        return followDriverId;
    }

    public void setFollowDriverId(Long driverId) {
        this.followDriverId = driverId;
    }
}
