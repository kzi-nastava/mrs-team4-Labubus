package com.example.ubre.ui.storages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.RideCardDto;
import com.example.ubre.ui.dtos.UserDto;

import java.util.ArrayList;
import java.util.List;

public class ActiveRidesStorage {
    private static ActiveRidesStorage instance;

    private MutableLiveData<List<RideCardDto>> activeRides = new MutableLiveData<>(List.of());
    private MutableLiveData<List<UserDto>> filterUsers = new MutableLiveData<>(List.of());

    public static ActiveRidesStorage getInstance() {
        if (instance == null)
            instance = new ActiveRidesStorage();
        return instance;
    }

    public LiveData<List<RideCardDto>> getActiveRidesReadOnly() { return activeRides; }

    public void extendActiveRides(List<RideCardDto> rides) {
        List<RideCardDto> temp = activeRides.getValue();
        List<RideCardDto> updated = temp == null ? new ArrayList<>() : new ArrayList<>(temp);
        if (rides != null) {
            updated.addAll(rides);
        }
        activeRides.setValue(updated);
    }

    public void clearActiveRides() {
        activeRides.setValue(new ArrayList<>());
    }

    public LiveData<List<UserDto>> getFilterUsersReadOnly() { return filterUsers; }

    public void setFilterUsers(List<UserDto> users) {
        filterUsers.setValue(users);
    }
}
