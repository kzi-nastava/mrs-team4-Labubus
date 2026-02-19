package com.example.ubre.ui.main;
import android.Manifest;
import android.app.AutomaticZenRule;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.enums.RideStatus;
import com.example.ubre.ui.services.RideService;
import com.example.ubre.ui.storages.ComplaintStorage;
import com.example.ubre.ui.storages.CurrentRideStorage;
import com.example.ubre.ui.storages.ProfileChangeStorage;
import com.example.ubre.ui.storages.ReviewStorage;
import com.example.ubre.ui.storages.UserStorage;
import com.example.ubre.ui.services.WsConnectionOwner;
import com.example.ubre.ui.utils.NotificationHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;

import com.example.ubre.ui.storages.RidePlanningStorage;
import com.example.ubre.ui.services.GeocodingService;
import com.example.ubre.ui.utils.TextNormalizer;
import com.example.ubre.ui.dtos.WaypointDto;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST = 2001;
    private MapView mapView;
    private MapUiController mapUiController;
    private View btnMenu;
    private View btnMapSearch;
    private View btnChat;
    private View btnComplaint;
    private View btnStartRide;
    private View blockNotice;
    private android.widget.TextView blockNoticeTitle;
    private android.widget.TextView blockNoticeMessage;
    private String lastBlockNoticeMessage;
    private boolean isBlockedUser = false;
    private Role currentRole = Role.GUEST;
    private boolean hasCurrentRide = false;
    private DrawerLayout drawer;
    private MainDrawerController drawerController;
    private MapLayerController mapLayerController;
    private RideOrderCoordinator rideOrderCoordinator;
    private MainObserversBinder observersBinder;
    private AuthLoader authLoader;
    private RideOrderSheetController rideOrderSheetController;
    private RideOrderUiController rideOrderUiController;
    private TextInputEditText rideOrderFromInput;
    private TextInputEditText rideOrderToInput;
    private View rideOrderUseMyLocation;
    private LinearLayout rideOrderStopsContainer;
    private View rideOptionStandard;
    private View rideOptionLuxury;
    private View rideOptionVan;
    private View rideOptionBabyFriendly;
    private View rideOptionPetFriendly;
    private FrameLayout rideOrderPriceContainer;
    private View rideOrderConfirmButton;
    private RideOrderLogicController rideOrderLogicController;
    private RideOrderWaypointsController rideOrderWaypointsController;
    private RideOrderAddWaypointController rideOrderAddWaypointController;
    private com.google.android.material.checkbox.MaterialCheckBox rideOrderScheduleCheck;
    private com.google.android.material.textfield.TextInputEditText rideOrderHourInput;
    private com.google.android.material.textfield.TextInputEditText rideOrderMinuteInput;
    private LinearLayout rideOrderInviteContainer;
    private RecyclerView rideOrderFromSuggestionsView;
    private RecyclerView rideOrderToSuggestionsView;
    private boolean isPickOnMapActive = false;
    private GeocodingService geocodingService;
    private ProgressBar routeLoadingSpinner;
    private LoadingIndicatorController loadingIndicatorController;
    private AutocompleteController autocompleteController;
    private View btnPanic;
    private View btnCancelDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationHelper.createChannels(this);
        requestNotificationPermission();

        // getWindow().setDecorFitsSystemWindows(false);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_light));

        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false); // bele ikonice

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            boolean hasFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;

            if (hasFragments) {
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                if (mapView != null) mapView.setVisibility(View.INVISIBLE);
                if (btnMenu != null) btnMenu.setVisibility(View.GONE);
                updateMapSearchVisibility();
                updateBlockNoticeVisibility();
                updateStartRideState();
                if (btnChat != null) btnChat.setVisibility(View.GONE);
                if (btnComplaint != null) btnComplaint.setVisibility(View.GONE);
                if (btnPanic != null) btnPanic.setVisibility(View.GONE);
            } else {
                findViewById(R.id.fragment_container).setVisibility(View.GONE);
                if (mapView != null) mapView.setVisibility(View.VISIBLE);
                if (btnMenu != null) btnMenu.setVisibility(View.VISIBLE);
                updateMapSearchVisibility();
                updateBlockNoticeVisibility();
                updateStartRideState();
                if (btnChat != null) btnChat.setVisibility(View.VISIBLE);
                updateComplaintButtonVisibility();
                if (btnPanic != null && canActivatePanic()) btnPanic.setVisibility(View.VISIBLE);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(statusBars.left, statusBars.top, statusBars.right, 0);
            return insets;
        });

        initCoreUi();
        initMapLayer();
        initControllers();
        bindRideOrderUi();
        bindObservers();
        loadUserIfAuthenticated();
        WsConnectionOwner.getInstance(getApplicationContext()).connectPublic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapUiController != null) mapUiController.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapUiController != null) mapUiController.onPause();
    }

    public void showFragment(Fragment f) {
        hideRideOrderSheet();
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        if (mapView != null) {
            mapView.setVisibility(View.INVISIBLE);
        }
        if (btnMenu != null) btnMenu.setVisibility(View.GONE);
        if (btnMapSearch != null) btnMapSearch.setVisibility(View.GONE);
        if (btnChat != null) btnChat.setVisibility(View.GONE);
        if (btnComplaint != null) btnComplaint.setVisibility(View.GONE);
        updateBlockNoticeVisibility();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, f)
                .addToBackStack(null)
                .commit();
    }

    public void showModal(Fragment f) {
        findViewById(R.id.modal_container).setVisibility(View.VISIBLE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.modal_container, f)
                .commit();
    }

    void updateMapSearchVisibility() {
        if (btnMapSearch == null) {
            return;
        }
        boolean hasFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        boolean allowedRole = currentRole == Role.GUEST || currentRole == Role.REGISTERED_USER;
        boolean visible = !hasFragments && allowedRole;
        btnMapSearch.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (visible) {
            boolean enabled = !hasCurrentRide && !isBlockedUser;
            btnMapSearch.setEnabled(enabled);
            btnMapSearch.setAlpha(enabled ? 1.0f : 0.5f);
        }
    }

    void updateBlockNotice(String note, boolean isBlocked) {
        isBlockedUser = isBlocked;
        lastBlockNoticeMessage = note == null ? "" : note.trim();
        if (lastBlockNoticeMessage.isEmpty()) {
            lastBlockNoticeMessage = "There is no reason provided";
        }
        if (blockNoticeTitle != null) {
            blockNoticeTitle.setText("You have been blocked");
        }
        if (blockNoticeMessage != null) {
            blockNoticeMessage.setText(lastBlockNoticeMessage);
        }
        if (mapView != null) {
            mapView.setEnabled(!isBlockedUser);
            mapView.setClickable(!isBlockedUser);
        }
        updateMapSearchVisibility();
        updateBlockNoticeVisibility();
    }

    void updateBlockNoticeVisibility() {
        if (blockNotice == null) {
            return;
        }
        boolean hasFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        boolean visible = !hasFragments && isBlockedUser;
        blockNotice.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private boolean isRideOrderSheetHidden() {
        return rideOrderSheetController == null || rideOrderSheetController.isHidden();
    }

    private void hideRideOrderSheet() {
        if (rideOrderSheetController != null) {
            rideOrderSheetController.hide();
        }
        isPickOnMapActive = false;
    }

    private void collapseRideOrderSheet() {
        if (rideOrderSheetController != null) {
            rideOrderSheetController.collapse();
        }
    }

    void updateGuestRideOrderState() {
        if (rideOrderSheetController != null) {
            rideOrderSheetController.updateGuestState(currentRole == Role.GUEST || hasCurrentRide);
        }
        rideOrderUiController.updateGuestState(currentRole == Role.GUEST || hasCurrentRide);
    }

    private void updateStartRideState() {
        if (btnStartRide == null) {
            return;
        }
        boolean isDriver = currentRole == Role.DRIVER;
        boolean hasFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        btnStartRide.setVisibility(isDriver && !hasFragments ? View.VISIBLE : View.GONE);
        if (isDriver && hasCurrentRide) {
            RideDto ride = CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue();
            if (ride == null) {
                btnStartRide.setEnabled(false);
                btnStartRide.setAlpha(0.5f);
            } else if (ride.getStatus() == RideStatus.IN_PROGRESS) {
                // Stop ride mode
                ((com.google.android.material.button.MaterialButton) btnStartRide).setText("Stop ride");
                btnStartRide.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_stop_ride));
                btnStartRide.setEnabled(true);
                btnStartRide.setAlpha(1.0f);

                btnCancelDriver.setEnabled(false);
                btnCancelDriver.setAlpha(0.5f);
            } else {
                // Start ride mode
                ((com.google.android.material.button.MaterialButton) btnStartRide).setText("Start this ride");
                btnStartRide.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_start_ride));
                btnStartRide.setEnabled(true);
                btnStartRide.setAlpha(1.0f);

                btnCancelDriver.setEnabled(true);
                btnCancelDriver.setAlpha(1.0f);
            }
        } else if (isDriver) {
            ((com.google.android.material.button.MaterialButton) btnStartRide).setText("Start this ride");
            btnStartRide.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_start_ride));
            btnStartRide.setEnabled(false);
            btnStartRide.setAlpha(0.5f);

            btnCancelDriver.setEnabled(false);
            btnCancelDriver.setAlpha(0.5f);
        }

        // btnCancelDriver.setVisibility(isDriver && !hasFragments ? View.VISIBLE : View.GONE);
        // if (isDriver) {
        //     boolean canStart = hasCurrentRide;
        //     if (canStart) {
        //         if (CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue() == null) {
        //             canStart = false;
        //         } else if (CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue().getStatus() == RideStatus.IN_PROGRESS) {
        //             canStart = false;
        //         }
        //     }
        //     btnStartRide.setEnabled(canStart);
        //     btnStartRide.setAlpha(canStart ? 1.0f : 0.5f);

        //     btnCancelDriver.setEnabled(canStart);
        //     btnCancelDriver.setAlpha(canStart ? 1.0f : 0.5f);
        // }
    }

    private boolean canGuestAddWaypoint() {
        if (currentRole != Role.GUEST) {
            return true;
        }
        return RidePlanningStorage.getInstance().getWaypointsSnapshot().size() < 2;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private String formatShortLabel(String displayName) {
        return TextNormalizer.toLatin(displayName).trim();
    }

    private void initMapLayer() {
        mapLayerController = new MapLayerController(
                this,
                () -> isPickOnMapActive,
                value -> isPickOnMapActive = value,
                this::isRideOrderSheetHidden,
                this::collapseRideOrderSheet,
                () -> rideOrderAddWaypointController,
                LOCATION_PERMISSION_REQUEST
        );
        mapLayerController.init();
        mapView = mapLayerController.getMapView();
        mapUiController = mapLayerController.getMapUiController();
        geocodingService = mapLayerController.getGeocodingService();
    }

    private void initCoreUi() {
        drawer = findViewById(R.id.main);
        findViewById(R.id.btn_menu).setOnClickListener(v ->
                drawer.openDrawer(GravityCompat.START)
        );

        NavigationView navigationView = findViewById(R.id.nav_view);
        drawerController = new MainDrawerController(this, drawer, navigationView);
        drawerController.init();

        btnMenu = findViewById(R.id.btn_menu);
        btnMapSearch = findViewById(R.id.btn_map_search);
        btnChat = findViewById(R.id.btn_chat);
        btnComplaint = findViewById(R.id.btn_complaint);
        btnStartRide = findViewById(R.id.btn_start_ride);
        blockNotice = findViewById(R.id.block_notice);
        blockNoticeTitle = findViewById(R.id.block_notice_title);
        blockNoticeMessage = findViewById(R.id.block_notice_message);
        btnPanic = findViewById(R.id.btn_panic);
        btnCancelDriver = findViewById(R.id.btn_cancel_driver);

        if (btnStartRide != null) {
            btnStartRide.setOnClickListener(v -> {
                if (currentRole != Role.DRIVER || !hasCurrentRide) {
                    return;
                }
                RideDto ride = CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue();
                if (ride == null) {
                    return;
                }
                Long rideId = ride.getId();
                if (rideId == null || rideId == 0L) {
                    return;
                }
                if (ride.getStatus() == RideStatus.IN_PROGRESS) {
                    // Stop ride — get GPS location and call stopRide
                    onStopRideClick(rideId);
                } else {
                    try {
                        RideService.getInstance().startRide(this, rideId);
                    } catch (Exception ignored) {
                    }
                }
            });
        }
        if (btnComplaint != null) {
            btnComplaint.setOnClickListener(v -> {
                RideDto ride = CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue();
                if (ride != null && ride.getId() != null) {
                    ComplaintStorage.getInstance().setRideId(ride.getId());
                }
            });
        }

        if (btnPanic != null) {
            btnPanic.setOnClickListener(v -> {
                if (!canActivatePanic()) return;
                Long rideId = CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue().getId();
                try {
                    RideService.getInstance().activatePanic(this, rideId);
                } catch (Exception ignored) {}
            });
        }

        updateMapSearchVisibility();
        updateBlockNoticeVisibility();
        updateStartRideState();
        updateComplaintButtonVisibility();
        updatePanicBtnVisibility(hasCurrentRide);
        routeLoadingSpinner = findViewById(R.id.route_loading_spinner);
        loadingIndicatorController = new LoadingIndicatorController(routeLoadingSpinner);
    }

    private void initControllers() {
        rideOrderUiController = new RideOrderUiController(this);
        rideOrderFromInput = rideOrderUiController.fromInput;
        rideOrderToInput = rideOrderUiController.toInput;
        rideOrderUseMyLocation = rideOrderUiController.useMyLocation;
        rideOrderStopsContainer = (LinearLayout) rideOrderUiController.stopsContainer;
        rideOptionStandard = rideOrderUiController.optionStandard;
        rideOptionLuxury = rideOrderUiController.optionLuxury;
        rideOptionVan = rideOrderUiController.optionVan;
        rideOptionBabyFriendly = rideOrderUiController.optionBaby;
        rideOptionPetFriendly = rideOrderUiController.optionPet;
        rideOrderPriceContainer = (FrameLayout) rideOrderUiController.priceContainer;
        rideOrderScheduleCheck = rideOrderUiController.scheduleCheck;
        rideOrderHourInput = rideOrderUiController.hourInput;
        rideOrderMinuteInput = rideOrderUiController.minuteInput;
        rideOrderInviteContainer = (LinearLayout) rideOrderUiController.inviteContainer;
        rideOrderConfirmButton = rideOrderUiController.confirmButton;
        rideOrderFromSuggestionsView = rideOrderUiController.fromSuggestionsView;
        rideOrderToSuggestionsView = rideOrderUiController.toSuggestionsView;

        rideOrderSheetController = new RideOrderSheetController(
                rideOrderUiController.rideOrderSheet,
                rideOrderConfirmButton,
                dpToPx(96)
        );
        updateGuestRideOrderState();

        rideOrderLogicController = new RideOrderLogicController(
                this,
                rideOrderPriceContainer,
                rideOrderScheduleCheck,
                rideOrderHourInput,
                rideOrderMinuteInput,
                rideOrderInviteContainer,
                mapView,
                mapUiController
        );
        rideOrderWaypointsController = new RideOrderWaypointsController(
                this,
                rideOrderStopsContainer,
                rideOrderFromInput,
                rideOrderToInput,
                rideOrderUseMyLocation
        );
        rideOrderAddWaypointController = new RideOrderAddWaypointController(
                this,
                mapUiController,
                geocodingService,
                this::canGuestAddWaypoint,
                this::formatShortLabel,
                new RideOrderAddWaypointController.GeocodeCallbacks() {
                    @Override
                    public void onStart() {
                        if (loadingIndicatorController != null) {
                            loadingIndicatorController.onGeocodeStart();
                        }
                    }

                    @Override
                    public void onEnd() {
                        if (loadingIndicatorController != null) {
                            loadingIndicatorController.onGeocodeEnd();
                        }
                    }
                }
        );

        autocompleteController = new AutocompleteController(
                this,
                geocodingService,
                rideOrderFromInput,
                rideOrderToInput,
                rideOrderFromSuggestionsView,
                rideOrderToSuggestionsView,
                this::formatShortLabel,
                new AutocompleteController.LoadingCallbacks() {
                    @Override
                    public void onStart() {
                        if (loadingIndicatorController != null) {
                            loadingIndicatorController.onGeocodeStart();
                        }
                    }

                    @Override
                    public void onEnd() {
                        if (loadingIndicatorController != null) {
                            loadingIndicatorController.onGeocodeEnd();
                        }
                    }
                }
        );
        rideOrderUiController.bindAdapters(
                autocompleteController.getFromAdapter(),
                autocompleteController.getToAdapter()
        );
        autocompleteController.bind();

        rideOrderCoordinator = new RideOrderCoordinator(
                this,
                rideOrderUiController,
                rideOrderSheetController,
                rideOrderLogicController,
                rideOrderAddWaypointController,
                autocompleteController,
                rideOrderFromInput,
                rideOrderToInput,
                rideOptionStandard,
                rideOptionLuxury,
                rideOptionVan,
                rideOptionBabyFriendly,
                rideOptionPetFriendly,
                btnMapSearch,
                drawer,
                this::dpToPx,
                () -> currentRole,
                value -> isPickOnMapActive = value,
                this::collapseRideOrderSheet,
                this::hideRideOrderSheet
        );
    }

    private void bindRideOrderUi() {
        if (rideOrderCoordinator != null) {
            rideOrderCoordinator.bind();
        }
    }

    private void bindObservers() {
        if (observersBinder == null) {
            observersBinder = new MainObserversBinder(
                    this,
                    rideOrderWaypointsController,
                    rideOrderLogicController,
                    mapUiController,
                    mapView,
                    loadingIndicatorController,
                    rideOrderCoordinator,
                    drawerController
            );
            observersBinder.bind();
        }
    }

    private void loadUserIfAuthenticated() {
        if (authLoader == null) {
            authLoader = new AuthLoader(getApplicationContext(), drawerController);
        }
        authLoader.loadUserIfAuthenticated();
    }

    void setCurrentRole(Role role) {
        currentRole = role == null ? Role.GUEST : role;
        updateStartRideState();
        updateComplaintButtonVisibility();
    }

    void setCurrentRideActive(boolean active) {
        hasCurrentRide = active;
        if (hasCurrentRide) {
            hideRideOrderSheet();
        }
        updateMapSearchVisibility();
        updateGuestRideOrderState();
        updateStartRideState();
        updateComplaintButtonVisibility();
        updatePanicBtnVisibility(hasCurrentRide);
    }

    void updateComplaintButtonVisibility() {
        if (btnComplaint == null) {
            return;
        }
        boolean hasFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if (hasFragments) {
            btnComplaint.setVisibility(View.GONE);
            return;
        }
        boolean isUser = currentRole == Role.REGISTERED_USER;
        boolean rideInProgress = false;
        if (hasCurrentRide) {
            RideDto ride = CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue();
            rideInProgress = ride != null && ride.getStatus() == RideStatus.IN_PROGRESS;
        }
        btnComplaint.setVisibility(isUser && rideInProgress ? View.VISIBLE : View.GONE);
    }


    public void openRideOrderWithWaypoints(List<WaypointDto> waypoints) {
        if (waypoints == null) {
            RidePlanningStorage.getInstance().clear();
        } else {
            RidePlanningStorage.getInstance().setWaypoints(new java.util.ArrayList<>(waypoints));
        }

        getSupportFragmentManager().popBackStack(null, getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
        if (rideOrderSheetController != null) {
            rideOrderSheetController.showExpanded();
        }
        updateMapSearchVisibility();
    }

    private void onStopRideClick(Long rideId) {
        if (geocodingService == null || mapUiController == null) {
            return;
        }
        // Try to get GPS location from the map overlay
        org.osmdroid.util.GeoPoint myLoc = mapUiController.getMyLocation();
        if (myLoc != null) {
            geocodingService.reverse(myLoc.getLatitude(), myLoc.getLongitude(), new GeocodingService.GeocodingCallback() {
                @Override
                public void onResult(com.example.ubre.ui.dtos.GeocodingResult result) {
                    String label = result != null && result.displayName != null
                            ? formatShortLabel(result.displayName) : "Stop location";
                    WaypointDto stopWp = new WaypointDto(0L, label, myLoc.getLatitude(), myLoc.getLongitude());
                    try {
                        RideService.getInstance().stopRide(MainActivity.this, rideId, stopWp);
                    } catch (Exception ignored) {
                    }
                }

                @Override
                public void onError(Throwable t) {
                    // Fallback: use coordinates without label
                    WaypointDto stopWp = new WaypointDto(0L, "Stop location", myLoc.getLatitude(), myLoc.getLongitude());
                    try {
                        RideService.getInstance().stopRide(MainActivity.this, rideId, stopWp);
                    } catch (Exception ignored) {
                    }
                }
            });
        } else {
            // No GPS available — use a default location
            WaypointDto stopWp = new WaypointDto(0L, "Stop location", 45.2671, 19.8335);
            try {
                RideService.getInstance().stopRide(this, rideId, stopWp);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mapLayerController != null) {
            mapLayerController.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void updatePanicBtnVisibility(boolean hasCurrentRide) {
        SharedPreferences prefs = this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String role = prefs.getString("role", "");

        boolean isAllowedRole = role.equals("DRIVER") || role.equals("REGISTERED_USER");
        if (!isAllowedRole) {
            btnPanic.setVisibility(View.GONE);
            return;
        }

        btnPanic.setVisibility(View.VISIBLE);
        btnPanic.setEnabled(hasCurrentRide);
        btnPanic.setAlpha(hasCurrentRide ? 1.0f : 0.5f);
    }

    private boolean canActivatePanic() {
        if (currentRole != Role.DRIVER && currentRole != Role.REGISTERED_USER) return false;
        if (!hasCurrentRide) return false;

        Long rideId = CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue() != null
                ? CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue().getId()
                : null;
        return rideId != null && rideId != 0L;
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

}
