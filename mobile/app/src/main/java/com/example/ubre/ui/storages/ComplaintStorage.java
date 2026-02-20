package com.example.ubre.ui.storages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ComplaintStorage {
    private static ComplaintStorage instance;

    private final MutableLiveData<Long> rideId = new MutableLiveData<>(null);

    private ComplaintStorage() {}

    public static ComplaintStorage getInstance() {
        if (instance == null)
            instance = new ComplaintStorage();
        return instance;
    }

    public LiveData<Long> getRideId() { return rideId; }

    public void setRideId(Long rideId) {
        this.rideId.setValue(rideId);
    }
}
