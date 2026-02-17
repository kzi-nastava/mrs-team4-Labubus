package com.example.ubre.ui.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.example.ubre.R;
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
import java.util.List;

public class MapUiController {
    public interface OnMapTapListener {
        void onSingleTap(GeoPoint point);
        void onLongPress(GeoPoint point);
    }

    private final Activity activity;
    private final MapView map;
    private MyLocationNewOverlay myLocationOverlay;
    private final List<Marker> rideOrderMarkers = new ArrayList<>();

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
