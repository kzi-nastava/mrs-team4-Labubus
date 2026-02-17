package com.example.ubre.ui.main;

import android.view.View;
import android.widget.ProgressBar;

public class LoadingIndicatorController {
    private final ProgressBar spinner;
    private int geocodeInFlight = 0;
    private boolean routeLoading = false;

    public LoadingIndicatorController(ProgressBar spinner) {
        this.spinner = spinner;
        updateVisibility();
    }

    public void onGeocodeStart() {
        geocodeInFlight++;
        updateVisibility();
    }

    public void onGeocodeEnd() {
        geocodeInFlight = Math.max(0, geocodeInFlight - 1);
        updateVisibility();
    }

    public void onRouteLoadingChanged(boolean isLoading) {
        routeLoading = isLoading;
        updateVisibility();
    }

    public void reset() {
        geocodeInFlight = 0;
        routeLoading = false;
        updateVisibility();
    }

    private void updateVisibility() {
        if (spinner == null) {
            return;
        }
        boolean show = routeLoading || geocodeInFlight > 0;
        spinner.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
