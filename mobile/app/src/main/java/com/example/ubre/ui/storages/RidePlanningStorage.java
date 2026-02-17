package com.example.ubre.ui.storages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubre.ui.dtos.WaypointDto;

import java.util.ArrayList;
import java.util.List;

public class RidePlanningStorage {

    private static RidePlanningStorage instance;

    private final MutableLiveData<List<WaypointDto>> waypoints = new MutableLiveData<>(new ArrayList<>());

    public static synchronized RidePlanningStorage getInstance() {
        if (instance == null) {
            instance = new RidePlanningStorage();
        }
        return instance;
    }

    public LiveData<List<WaypointDto>> getWaypointsReadOnly() {
        return waypoints;
    }

    public List<WaypointDto> getWaypointsSnapshot() {
        List<WaypointDto> current = waypoints.getValue();
        return current == null ? new ArrayList<>() : new ArrayList<>(current);
    }

    public void setWaypoints(List<WaypointDto> updated) {
        waypoints.setValue(updated);
    }

    public void clear() {
        waypoints.setValue(new ArrayList<>());
    }
}
