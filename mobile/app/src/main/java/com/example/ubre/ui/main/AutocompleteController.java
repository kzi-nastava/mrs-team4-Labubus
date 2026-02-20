package com.example.ubre.ui.main;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.ui.adapters.AutocompleteAdapter;
import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.services.GeocodingService;
import com.example.ubre.ui.services.RidePlanningService;
import com.example.ubre.ui.storages.RidePlanningStorage;
import com.example.ubre.ui.utils.TextNormalizer;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteController {
    public interface LabelFormatter {
        String format(String displayName);
    }

    public interface LoadingCallbacks {
        void onStart();
        void onEnd();
    }

    private final Activity activity;
    private final GeocodingService geocodingService;
    private final TextInputEditText fromInput;
    private final TextInputEditText toInput;
    private final RecyclerView fromView;
    private final RecyclerView toView;
    private final LabelFormatter labelFormatter;
    private final LoadingCallbacks loadingCallbacks;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable fromRunnable;
    private Runnable toRunnable;
    private boolean suppressAutocomplete = false;

    private List<com.example.ubre.ui.dtos.GeocodingResult> fromSuggestions = new ArrayList<>();
    private List<com.example.ubre.ui.dtos.GeocodingResult> toSuggestions = new ArrayList<>();

    private final AutocompleteAdapter fromAdapter;
    private final AutocompleteAdapter toAdapter;

    public AutocompleteController(Activity activity,
                                  GeocodingService geocodingService,
                                  TextInputEditText fromInput,
                                  TextInputEditText toInput,
                                  RecyclerView fromView,
                                  RecyclerView toView,
                                  LabelFormatter labelFormatter,
                                  LoadingCallbacks loadingCallbacks) {
        this.activity = activity;
        this.geocodingService = geocodingService;
        this.fromInput = fromInput;
        this.toInput = toInput;
        this.fromView = fromView;
        this.toView = toView;
        this.labelFormatter = labelFormatter;
        this.loadingCallbacks = loadingCallbacks;

        fromAdapter = new AutocompleteAdapter(position -> {
            if (position >= 0 && position < fromSuggestions.size()) {
                applySuggestion(fromSuggestions.get(position), true);
                hideFromSuggestions();
            }
        });
        toAdapter = new AutocompleteAdapter(position -> {
            if (position >= 0 && position < toSuggestions.size()) {
                applySuggestion(toSuggestions.get(position), false);
                hideToSuggestions();
            }
        });
    }

    public AutocompleteAdapter getFromAdapter() {
        return fromAdapter;
    }

    public AutocompleteAdapter getToAdapter() {
        return toAdapter;
    }

    public void bind() {
        fromView.setLayoutManager(new LinearLayoutManager(fromView.getContext()));
        fromView.setAdapter(fromAdapter);
        toView.setLayoutManager(new LinearLayoutManager(toView.getContext()));
        toView.setAdapter(toAdapter);

        fromInput.addTextChangedListener(new SimpleWatcher(true));
        toInput.addTextChangedListener(new SimpleWatcher(false));

        fromInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideFromSuggestions();
            }
        });
        toInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideToSuggestions();
            }
        });
    }

    public void hideFromSuggestions() {
        fromView.setVisibility(View.GONE);
    }

    public void hideToSuggestions() {
        toView.setVisibility(View.GONE);
    }

    public void geocodeFromInput(TextInputEditText input, boolean isFrom) {
        if (geocodingService == null || input.getText() == null) {
            return;
        }
        String query = TextNormalizer.toLatin(input.getText().toString().trim());
        if (query.isEmpty()) {
            return;
        }

        loadingCallbacks.onStart();
        geocodingService.geocode(query, new GeocodingService.GeocodingCallback() {
            @Override
            public void onResult(com.example.ubre.ui.dtos.GeocodingResult result) {
                loadingCallbacks.onEnd();
                if (result == null || result.lat == null || result.lon == null) {
                    activity.runOnUiThread(() -> android.widget.Toast.makeText(activity, "No results found.", android.widget.Toast.LENGTH_SHORT).show());
                    return;
                }
                double lat = Double.parseDouble(result.lat);
                double lon = Double.parseDouble(result.lon);
                String label = result.displayName == null ? query : labelFormatter.format(result.displayName);
                WaypointDto waypoint = new WaypointDto(null, label, lat, lon);

                activity.runOnUiThread(() -> {
                    List<WaypointDto> current = RidePlanningStorage.getInstance().getWaypointsSnapshot();
                    if (isFrom) {
                        if (current.isEmpty()) {
                            RidePlanningService.getInstance().addWaypoint(waypoint);
                        } else {
                            RidePlanningService.getInstance().updateWaypointAt(0, waypoint);
                        }
                    } else {
                        if (current.size() < 2) {
                            RidePlanningService.getInstance().addWaypoint(waypoint);
                        } else {
                            RidePlanningService.getInstance().updateWaypointAt(1, waypoint);
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                loadingCallbacks.onEnd();
                activity.runOnUiThread(() -> android.widget.Toast.makeText(activity, "Geocoding failed.", android.widget.Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void requestAutocomplete(String rawQuery, boolean isFrom) {
        if (geocodingService == null) {
            return;
        }
        if (isFrom && !fromInput.hasFocus()) {
            hideFromSuggestions();
            return;
        }
        if (!isFrom && !toInput.hasFocus()) {
            hideToSuggestions();
            return;
        }
        String query = TextNormalizer.toLatin(rawQuery.trim());
        if (query.length() < 3) {
            if (isFrom) {
                fromSuggestions = new ArrayList<>();
                fromAdapter.setItems(new ArrayList<>());
                hideFromSuggestions();
            } else {
                toSuggestions = new ArrayList<>();
                toAdapter.setItems(new ArrayList<>());
                hideToSuggestions();
            }
            return;
        }

        geocodingService.search(query, 6, new GeocodingService.SearchCallback() {
            @Override
            public void onResult(List<com.example.ubre.ui.dtos.GeocodingResult> results) {
                activity.runOnUiThread(() -> {
                    List<com.example.ubre.ui.dtos.GeocodingResult> safe = results == null ? new ArrayList<>() : results;
                    ArrayList<String> labels = new ArrayList<>();
                    for (com.example.ubre.ui.dtos.GeocodingResult r : safe) {
                        if (r != null && r.displayName != null) {
                            labels.add(TextNormalizer.toLatin(r.displayName));
                        }
                    }
                    if (isFrom) {
                        fromSuggestions = safe;
                        fromAdapter.setQuery(query);
                        fromAdapter.setItems(labels);
                        fromView.setVisibility(labels.isEmpty() ? View.GONE : View.VISIBLE);
                    } else {
                        toSuggestions = safe;
                        toAdapter.setQuery(query);
                        toAdapter.setItems(labels);
                        toView.setVisibility(labels.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
            }
        });
    }

    private void applySuggestion(com.example.ubre.ui.dtos.GeocodingResult result, boolean isFrom) {
        if (result == null || result.lat == null || result.lon == null) {
            return;
        }
        double lat = Double.parseDouble(result.lat);
        double lon = Double.parseDouble(result.lon);
        String label = result.displayName == null ? "" : labelFormatter.format(result.displayName);
        WaypointDto waypoint = new WaypointDto(null, label, lat, lon);
        suppressAutocomplete = true;
        List<WaypointDto> current = RidePlanningStorage.getInstance().getWaypointsSnapshot();
        if (isFrom) {
            if (current.isEmpty()) {
                RidePlanningService.getInstance().addWaypoint(waypoint);
            } else {
                RidePlanningService.getInstance().updateWaypointAt(0, waypoint);
            }
            fromInput.setText(label);
            fromInput.clearFocus();
        } else {
            if (current.size() < 2) {
                RidePlanningService.getInstance().addWaypoint(waypoint);
            } else {
                RidePlanningService.getInstance().updateWaypointAt(1, waypoint);
            }
            toInput.setText(label);
            toInput.clearFocus();
        }
        suppressAutocomplete = false;
    }

    private class SimpleWatcher implements android.text.TextWatcher {
        private final boolean isFrom;

        SimpleWatcher(boolean isFrom) {
            this.isFrom = isFrom;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (suppressAutocomplete) {
                return;
            }
            if (isFrom) {
                if (fromRunnable != null) {
                    handler.removeCallbacks(fromRunnable);
                }
                fromRunnable = () -> requestAutocomplete(s.toString(), true);
                handler.postDelayed(fromRunnable, 350);
            } else {
                if (toRunnable != null) {
                    handler.removeCallbacks(toRunnable);
                }
                toRunnable = () -> requestAutocomplete(s.toString(), false);
                handler.postDelayed(toRunnable, 350);
            }
        }

        @Override
        public void afterTextChanged(android.text.Editable s) {
        }
    }
}
