package com.example.ubre.ui.main;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;

import androidx.core.content.ContextCompat;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.VehicleIndicatorDto;
import com.example.ubre.ui.dtos.WaypointDto;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapUiController {
    public interface OnMapTapListener {
        void onSingleTap(GeoPoint point);
        void onLongPress(GeoPoint point);
    }

    private static final long ANIMATION_DURATION_MS = 1000;

    private final Activity activity;
    private final MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private final List<Marker> rideOrderMarkers = new ArrayList<>();

    private final Map<Long, Marker> vehicleMarkers = new HashMap<>();
    private final Map<Long, ValueAnimator> vehicleAnimators = new HashMap<>();
    private Bitmap carBitmap;
    private Bitmap carPanicBitmap;

    private Long followDriverId = null;

    private final List<Marker> trackingWaypointMarkers = new ArrayList<>();

    public MapUiController(Activity activity, MapView map) {
        this.activity = activity;
        this.map = map;
    }

    public void init(OnMapTapListener listener) {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (listener != null) {
                    listener.onSingleTap(p);
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                if (listener != null) {
                    listener.onLongPress(p);
                }
                return false;
            }
        }));

        MapController controller = (MapController) map.getController();
        controller.setZoom(14.0);
        controller.setCenter(new GeoPoint(45.2671, 19.8335));

        // Pre-create car bitmaps
        int carW = dpToPx(24);
        int carH = dpToPx(42);
        carBitmap = drawableToBitmap(ContextCompat.getDrawable(activity, R.drawable.ic_car), carW, carH);

        int panicW = dpToPx(32);
        int panicH = dpToPx(56);
        carPanicBitmap = drawableToBitmap(ContextCompat.getDrawable(activity, R.drawable.ic_car_panic), panicW, panicH);
    }

    public void syncVehicleMarkers(List<VehicleIndicatorDto> indicators) {
        if (indicators == null) {
            indicators = new ArrayList<>();
        }

        Set<Long> currentDriverIds = new HashSet<>();

        for (VehicleIndicatorDto v : indicators) {
            if (v.getDriverId() == null || v.getLocation() == null
                    || v.getLocation().getLatitude() == null || v.getLocation().getLongitude() == null) continue;
            currentDriverIds.add(v.getDriverId());

            GeoPoint newPos = new GeoPoint(v.getLocation().getLatitude(), v.getLocation().getLongitude());
            Marker existing = vehicleMarkers.get(v.getDriverId());

            if (existing != null) {
                // Update existing marker — animate to new position
                GeoPoint oldPos = existing.getPosition();
                if (oldPos.getLatitude() != newPos.getLatitude() || oldPos.getLongitude() != newPos.getLongitude()) {
                    float bearing = calculateBearing(
                            oldPos.getLatitude(), oldPos.getLongitude(),
                            newPos.getLatitude(), newPos.getLongitude()
                    );
                    existing.setRotation(-bearing); // OSMDroid uses counter-clockwise
                    animateMarker(v.getDriverId(), existing, oldPos, newPos);
                }
                // Update icon based on panic
                existing.setIcon(markerIcon(v.isPanic()));
                String label = v.getLocation().getLabel() != null ? v.getLocation().getLabel() : "";
                existing.setTitle(label + " (" + v.getStatus() + ")");
            } else {
                // Create new marker
                Marker marker = new Marker(map);
                marker.setPosition(newPos);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                marker.setIcon(markerIcon(v.isPanic()));
                marker.setFlat(true);
                String label = v.getLocation().getLabel() != null ? v.getLocation().getLabel() : "";
                marker.setTitle(label + " (" + v.getStatus() + ")");
                map.getOverlays().add(marker);
                vehicleMarkers.put(v.getDriverId(), marker);
            }

            // Camera follow
            if (v.getDriverId().equals(followDriverId)) {
                map.post(() -> map.getController().animateTo(newPos));
            }
        }

        // Remove stale markers
        List<Long> stale = new ArrayList<>();
        for (Long driverId : vehicleMarkers.keySet()) {
            if (!currentDriverIds.contains(driverId)) {
                stale.add(driverId);
            }
        }
        for (Long driverId : stale) {
            Marker m = vehicleMarkers.remove(driverId);
            if (m != null) {
                map.getOverlays().remove(m);
            }
            ValueAnimator anim = vehicleAnimators.remove(driverId);
            if (anim != null) {
                anim.cancel();
            }
        }

        map.invalidate();
    }

    private Drawable markerIcon(Boolean panic) {
        if (Boolean.TRUE.equals(panic)) {
            return bitmapToDrawable(carPanicBitmap);
        }
        return bitmapToDrawable(carBitmap);
    }

    private Drawable bitmapToDrawable(Bitmap bitmap) {
        if (bitmap == null) return null;
        android.graphics.drawable.BitmapDrawable d = new android.graphics.drawable.BitmapDrawable(activity.getResources(), bitmap);
        d.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        return d;
    }

    private void animateMarker(Long driverId, Marker marker, GeoPoint from, GeoPoint to) {
        ValueAnimator prev = vehicleAnimators.get(driverId);
        if (prev != null) {
            prev.cancel();
        }

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(ANIMATION_DURATION_MS);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            float t = (float) animation.getAnimatedValue();
            double lat = from.getLatitude() + (to.getLatitude() - from.getLatitude()) * t;
            double lon = from.getLongitude() + (to.getLongitude() - from.getLongitude()) * t;
            marker.setPosition(new GeoPoint(lat, lon));
            map.invalidate();
        });
        animator.start();
        vehicleAnimators.put(driverId, animator);
    }

    private float calculateBearing(double lat1, double lon1, double lat2, double lon2) {
        double dLon = Math.toRadians(lon2 - lon1);
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double y = Math.sin(dLon) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(dLon);
        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (float) ((bearing + 360) % 360);
    }

    public void setFollowDriverId(Long driverId) {
        this.followDriverId = driverId;
        if (driverId != null) {
            map.getController().setZoom(17.0);
        }
    }

    public void syncTrackingWaypoints(List<WaypointDto> waypoints) {
        clearTrackingWaypoints();
        if (waypoints == null) return;

        for (WaypointDto wp : waypoints) {
            if (wp.getVisited() != null && wp.getVisited()) continue;
            if (wp.getLatitude() == null || wp.getLongitude() == null) continue;
            GeoPoint point = new GeoPoint(wp.getLatitude(), wp.getLongitude());
            Marker marker = new Marker(map);
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(ContextCompat.getDrawable(activity, R.drawable.ic_waypoint_red));
            marker.setTitle(wp.getLabel());
            map.getOverlays().add(marker);
            trackingWaypointMarkers.add(marker);
        }
        map.invalidate();
    }

    private void clearTrackingWaypoints() {
        for (Marker m : trackingWaypointMarkers) {
            map.getOverlays().remove(m);
        }
        trackingWaypointMarkers.clear();
    }

    public void clearTrackingOverlays() {
        clearTrackingWaypoints();
        followDriverId = null;
        map.invalidate();
    }

    public void setupLocationOverlay(boolean hasLocationPermission, Runnable requestPermission) {
        if (!hasLocationPermission) {
            if (requestPermission != null) {
                requestPermission.run();
            }
            return;
        }

        if (myLocationOverlay == null) {
            myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(activity), map);
            myLocationOverlay.setDrawAccuracyEnabled(true);
            Drawable pin = ContextCompat.getDrawable(activity, R.drawable.ic_my_location_blue);
            myLocationOverlay.setPersonIcon(drawableToBitmap(pin, dpToPx(56), dpToPx(56)));
            myLocationOverlay.setPersonAnchor(0.5f, 1.0f);
            map.getOverlays().add(myLocationOverlay);

            myLocationOverlay.runOnFirstFix(() -> map.post(() -> {
                if (myLocationOverlay.getMyLocation() != null) {
                    map.getController().animateTo(myLocationOverlay.getMyLocation());
                }
            }));
        }
        myLocationOverlay.enableMyLocation();
    }

    public void onResume() {
        map.onResume();
        if (myLocationOverlay != null) {
            myLocationOverlay.enableMyLocation();
        }
    }

    public void onPause() {
        map.onPause();
        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
        }
        for (ValueAnimator anim : vehicleAnimators.values()) {
            anim.cancel();
        }
    }

    public GeoPoint getMyLocation() {
        if (myLocationOverlay == null) {
            return null;
        }
        return myLocationOverlay.getMyLocation();
    }

    public MapView getMapView() {
        return map;
    }

    public void syncRideOrderMarkers(List<WaypointDto> waypoints) {
        for (Marker marker : rideOrderMarkers) {
            map.getOverlays().remove(marker);
        }
        rideOrderMarkers.clear();

        for (WaypointDto waypoint : waypoints) {
            if (waypoint.getLatitude() == null || waypoint.getLongitude() == null) continue;
            GeoPoint point = new GeoPoint(waypoint.getLatitude(), waypoint.getLongitude());
            Marker marker = new Marker(map);
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(ContextCompat.getDrawable(activity, R.drawable.ic_waypoint_red));
            marker.setTitle(waypoint.getLabel());
            map.getOverlays().add(marker);
            rideOrderMarkers.add(marker);
        }
        map.invalidate();
    }

    public void clearRideOrderMarkers() {
        for (Marker marker : rideOrderMarkers) {
            map.getOverlays().remove(marker);
        }
        rideOrderMarkers.clear();
        map.invalidate();
    }

    private int dpToPx(int dp) {
        float density = activity.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private Bitmap drawableToBitmap(Drawable drawable, int widthPx, int heightPx) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, widthPx, heightPx);
        drawable.draw(canvas);
        return bitmap;
    }
}
