package com.example.ubre.ui.main;
import android.graphics.Color;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.enums.RideStatus;
import com.example.ubre.ui.services.RideService;
import com.example.ubre.ui.storages.CurrentRideStorage;
import com.example.ubre.ui.storages.ProfileChangeStorage;
import com.example.ubre.ui.storages.UserStorage;
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
    private View btnStartRide;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                updateStartRideState();
                if (btnChat != null) btnChat.setVisibility(View.GONE);
            } else {
                findViewById(R.id.fragment_container).setVisibility(View.GONE);
                if (mapView != null) mapView.setVisibility(View.VISIBLE);
                if (btnMenu != null) btnMenu.setVisibility(View.VISIBLE);
                updateMapSearchVisibility();
                updateStartRideState();
                if (btnChat != null) btnChat.setVisibility(View.VISIBLE);
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

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, f)
                .addToBackStack(null)
                .commit();
    }

    public void showModal(Fragment f) {
        hideRideOrderSheet();
        findViewById(R.id.modal_container).setVisibility(View.VISIBLE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.modal_container, f)
                .addToBackStack(null)
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
            boolean enabled = !hasCurrentRide;
            btnMapSearch.setEnabled(enabled);
            btnMapSearch.setAlpha(enabled ? 1.0f : 0.5f);
        }
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
        if (isDriver) {
            boolean canStart = hasCurrentRide;
            if (canStart) {
                if (CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue() == null) {
                    canStart = false;
                } else if (CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue().getStatus() == RideStatus.IN_PROGRESS) {
                    canStart = false;
                }
            }
            btnStartRide.setEnabled(canStart);
            btnStartRide.setAlpha(canStart ? 1.0f : 0.5f);
        }
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
        btnStartRide = findViewById(R.id.btn_start_ride);
        if (btnStartRide != null) {
            btnStartRide.setOnClickListener(v -> {
                if (currentRole != Role.DRIVER) {
                    return;
                }
                if (!hasCurrentRide) {
                    return;
                }
                if (CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue() == null) {
                    return;
                }
                Long rideId = CurrentRideStorage.getInstance().getCurrentRideReadOnly().getValue().getId();
                if (rideId == null || rideId == 0L) {
                    return;
                }
                try {
                    RideService.getInstance().startRide(this, rideId);
                } catch (Exception ignored) {
                }
            });
        }
        updateMapSearchVisibility();
        updateStartRideState();
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
    }

    void setCurrentRideActive(boolean active) {
        hasCurrentRide = active;
        if (hasCurrentRide) {
            hideRideOrderSheet();
        }
        updateMapSearchVisibility();
        updateGuestRideOrderState();
        updateStartRideState();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mapLayerController != null) {
            mapLayerController.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
