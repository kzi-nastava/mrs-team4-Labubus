package com.example.ubre.ui.storages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.RideCardDto;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.dtos.UserDto;

import java.util.ArrayList;
import java.util.List;

public class RideHistoryStorage {
    private static RideHistoryStorage instance;

    private MutableLiveData<List<RideCardDto>> history = new MutableLiveData<>(List.of());


    private MutableLiveData<List<UserDto>> filterUsers = new MutableLiveData<>(List.of());

    public static RideHistoryStorage getInstance() {
        if (instance == null)
            instance = new RideHistoryStorage();
        return instance;
    }

    public LiveData<List<RideCardDto>> getHistoryReadOnly() {return history;}

    public void extendHistory(List<RideCardDto> rides) {
        List<RideCardDto> temp = history.getValue();
        temp.addAll(rides);
        history.setValue(temp);
    }

    public void clearHistory() {
        history.setValue(new ArrayList<>());
    }

    public LiveData<List<UserDto>> getFilterUsersReadOnly() {return filterUsers;}

    public void setFilterUsers(List<UserDto> users) {
        filterUsers.setValue(users);
    }
}
