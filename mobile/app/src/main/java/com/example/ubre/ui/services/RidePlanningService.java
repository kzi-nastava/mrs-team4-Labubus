package com.example.ubre.ui.services;

import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.storages.RidePlanningStorage;

import java.util.List;

public class RidePlanningService {

    private static RidePlanningService instance;

    public static synchronized RidePlanningService getInstance() {
        if (instance == null) {
            instance = new RidePlanningService();
        }
        return instance;
    }

    public void addWaypoint(WaypointDto waypoint) {
        List<WaypointDto> current = RidePlanningStorage.getInstance().getWaypointsSnapshot();
        current.add(waypoint);
        RidePlanningStorage.getInstance().setWaypoints(current);
    }

    public void removeWaypointAt(int index) {
        List<WaypointDto> current = RidePlanningStorage.getInstance().getWaypointsSnapshot();
        if (index < 0 || index >= current.size()) {
            return;
        }
        current.remove(index);
        RidePlanningStorage.getInstance().setWaypoints(current);
    }

    public void updateWaypointAt(int index, WaypointDto waypoint) {
        List<WaypointDto> current = RidePlanningStorage.getInstance().getWaypointsSnapshot();
        if (index < 0 || index >= current.size()) {
            return;
        }
        current.set(index, waypoint);
        RidePlanningStorage.getInstance().setWaypoints(current);
    }

    public void updateWaypointLabelAt(int index, String label) {
        List<WaypointDto> current = RidePlanningStorage.getInstance().getWaypointsSnapshot();
        if (index < 0 || index >= current.size()) {
            return;
        }
        WaypointDto wp = current.get(index);
        WaypointDto updated = new WaypointDto(wp.getId(), label, wp.getLatitude(), wp.getLongitude());
        current.set(index, updated);
        RidePlanningStorage.getInstance().setWaypoints(current);
    }

    public void clear() {
        RidePlanningStorage.getInstance().clear();
    }
}
