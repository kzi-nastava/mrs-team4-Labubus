package com.example.ubre.ui.main;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
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
import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.dtos.VehicleDto;
import com.example.ubre.ui.apis.LoginApi;
import com.example.ubre.ui.services.WsConnectionOwner;
import com.example.ubre.ui.services.UserService;
import com.example.ubre.ui.storages.ReviewStorage;
import com.example.ubre.ui.storages.ProfileChangeStorage;
import com.example.ubre.ui.storages.UserStorage;
import com.google.android.material.navigation.NavigationView;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.services.RouteService;
import com.example.ubre.ui.storages.RidePlanningStorage;
import com.example.ubre.ui.services.GeocodingService;
import com.example.ubre.ui.utils.TextNormalizer;
import com.example.ubre.ui.enums.VehicleType;

import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST = 2001;
    private MapView mapView;
    private MapUiController mapUiController;
    private View btnMenu;
    private View btnMapSearch;
    private View btnChat;
    private Role currentRole = Role.GUEST;
    private DrawerLayout drawer;
    private RideOrderSheetController rideOrderSheetController;
    private RideOrderUiController rideOrderUiController;
    private TextInputEditText rideOrderFromInput;
    private TextInputEditText rideOrderToInput;
    private View rideOrderUseMyLocation;
    private View rideOrderPickOnMap;
    private LinearLayout rideOrderStopsContainer;
    private View rideOptionStandard;
    private View rideOptionLuxury;
    private View rideOptionVan;
    private View rideOptionBabyFriendly;
    private View rideOptionPetFriendly;
    private FrameLayout rideOrderPriceContainer;
    private VehicleType selectedVehicleType = VehicleType.STANDARD;
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
    LoginApi loginApi = ApiClient.getClient().create(LoginApi.class);

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
                if (btnChat != null) btnChat.setVisibility(View.GONE);
            } else {
                findViewById(R.id.fragment_container).setVisibility(View.GONE);
                if (mapView != null) mapView.setVisibility(View.VISIBLE);
                if (btnMenu != null) btnMenu.setVisibility(View.VISIBLE);
                updateMapSearchVisibility();
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

    private void setMenuOptions(Role role) {
        currentRole = role == null ? Role.GUEST : role;
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().clear();

        int menuRes; // Variable to hold the menu resource ID
        switch (role) {
            case ADMIN: menuRes = R.menu.drawer_admin; break;
            case DRIVER: menuRes = R.menu.drawer_driver; break;
            case REGISTERED_USER: menuRes = R.menu.drawer_registered_user; break;
            default: menuRes = R.menu.drawer_guest; break;
        }

        navigationView.inflateMenu(menuRes);

        MenuItem logout = navigationView.getMenu().findItem(R.id.nav_log_out);
        if (logout != null) {
            SpannableString logoutText = new SpannableString(logout.getTitle());
            logoutText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.error)), 0, logoutText.length(), 0);
            logout.setTitle(logoutText);
        }

        View header = navigationView.getHeaderView(0);
        ImageView backIcon = header.findViewById(R.id.nav_back);

        backIcon.setOnClickListener(v ->
                drawer.closeDrawer(GravityCompat.START)
        );

        updateMapSearchVisibility();
        updateGuestRideOrderState();
    }

    @SuppressLint("SetTextI18n")
    private void fillDrawerHeader() {
        UserDto user = UserStorage.getInstance().getCurrentUser().getValue();

        NavigationView nav = findViewById(R.id.nav_view);

        ImageView avatar = nav.getHeaderView(0).findViewById(R.id.img_avatar);
        TextView name = nav.getHeaderView(0).findViewById(R.id.txt_name);
        TextView phone = nav.getHeaderView(0).findViewById(R.id.txt_phone);

        if (user == null) {
            name.setText("John Doe");
            phone.setText("+381 XX XXX XXXX");
            Glide.with(this).load(R.drawable.img_default_avatar).circleCrop().into(avatar);
            return;
        }

        name.setText(user.getName() + " " + user.getSurname());
        phone.setText(user.getPhone());

        byte[] avatarBytes = UserStorage.getInstance().getCurrentUserAvatar().getValue();

        if (avatarBytes != null) {
            Glide.with(this).asBitmap().load(avatarBytes).circleCrop().into(avatar);
        } else {
            Glide.with(this).load(R.drawable.img_default_avatar).circleCrop().into(avatar);
        }
    }

    public void showFragment(Fragment f) {
        hideRideOrderSheet();
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        mapView.setVisibility(View.INVISIBLE);
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

    // WS connect/stop is owned by WsConnectionOwner (single owner).

    private void logout() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            WsConnectionOwner.getInstance(getApplicationContext())
                    .stop("Logout.noToken");
            Intent intent = new Intent(MainActivity.this, LoginSignupActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        loginApi.logout("Bearer " + token).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Logout", "Logout");

                if (response.isSuccessful()) {
                    WsConnectionOwner.getInstance(getApplicationContext())
                            .stop("Logout.success");
                    sharedPreferences.edit().clear().apply();
                    UserStorage.getInstance().clearUserStorage();
                    ProfileChangeStorage.getInstance().clearProfileChangeStorage();
                    RidePlanningStorage.getInstance().clear();
                    Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, LoginSignupActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Logout failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // 3) (optional) Notify backend about logout - this is not strictly necessary if using stateless JWTs
        // It is necessary because the driver can't logout in certain situations.
        // Thanks for deleting half of my code without checking with me first.

    }

    private void updateMapSearchVisibility() {
        if (btnMapSearch == null) {
            return;
        }
        boolean hasFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        boolean allowedRole = currentRole == Role.GUEST || currentRole == Role.REGISTERED_USER;
        btnMapSearch.setVisibility(!hasFragments && allowedRole ? View.VISIBLE : View.GONE);
    }

    private boolean isRideOrderSheetHidden() {
        return rideOrderSheetController == null || rideOrderSheetController.isHidden();
    }

    private void hideRideOrderSheet() {
        if (rideOrderSheetController != null) {
            rideOrderSheetController.hide();
        }
    }

    private void collapseRideOrderSheet() {
        if (rideOrderSheetController != null) {
            rideOrderSheetController.collapse();
        }
    }

    private void updateGuestRideOrderState() {
        if (rideOrderSheetController != null) {
            rideOrderSheetController.updateGuestState(currentRole == Role.GUEST);
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

    private void selectRideOption(View selected) {
        rideOptionStandard.setSelected(selected == rideOptionStandard);
        rideOptionLuxury.setSelected(selected == rideOptionLuxury);
        rideOptionVan.setSelected(selected == rideOptionVan);
        applyRideOptionScale(rideOptionStandard, selected == rideOptionStandard);
        applyRideOptionScale(rideOptionLuxury, selected == rideOptionLuxury);
        applyRideOptionScale(rideOptionVan, selected == rideOptionVan);
        if (selected == rideOptionStandard) {
            selectedVehicleType = VehicleType.STANDARD;
        } else if (selected == rideOptionVan) {
            selectedVehicleType = VehicleType.VAN;
        } else if (selected == rideOptionLuxury) {
            selectedVehicleType = VehicleType.LUXURY;
        }
        rideOrderLogicController.requestPriceEstimate(selectedVehicleType);
    }

    private void toggleExtraOption(View option) {
        option.setSelected(!option.isSelected());
        applyRideOptionScale(option, option.isSelected());
    }

    private void applyRideOptionScale(View option, boolean selected) {
        option.animate()
                .scaleX(selected ? 1.01f : 1.0f)
                .scaleY(selected ? 1.01f : 1.0f)
                .setDuration(120)
                .start();
        option.setElevation(dpToPx(3));
    }

    private class TimeFieldWatcher implements TextWatcher {
        private final int min;
        private final int max;
        private final View nextFocus;

        TimeFieldWatcher(int min, int max, View nextFocus) {
            this.min = min;
            this.max = max;
            this.nextFocus = nextFocus;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s == null) {
                return;
            }
            String text = s.toString();
            if (text.length() == 2 && nextFocus != null) {
                nextFocus.requestFocus();
            }
            if (text.isEmpty()) {
                return;
            }
            try {
                int value = Integer.parseInt(text);
                if (value < min || value > max) {
                    s.replace(0, s.length(), String.format("%02d", min));
                }
            } catch (NumberFormatException e) {
                s.clear();
            }
        }
    }

    private String formatShortLabel(String displayName) {
        return TextNormalizer.toLatin(displayName).trim();
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void initMapLayer() {
        mapView = findViewById(R.id.map);
        mapUiController = new MapUiController(this, mapView);
        mapUiController.init(new MapUiController.OnMapTapListener() {
            @Override
            public void onSingleTap(GeoPoint point) {
                if (!isRideOrderSheetHidden()) {
                    if (rideOrderAddWaypointController != null) {
                        rideOrderAddWaypointController.addFromMap(point);
                    }
                    collapseRideOrderSheet();
                    isPickOnMapActive = false;
                }
            }

            @Override
            public void onLongPress(GeoPoint point) {
            }
        });
        mapUiController.setupLocationOverlay(hasLocationPermission(), () -> ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST
        ));
        geocodingService = GeocodingService.getInstance(this);
    }

    private void initCoreUi() {
        drawer = findViewById(R.id.main);
        findViewById(R.id.btn_menu).setOnClickListener(v ->
                drawer.openDrawer(GravityCompat.START)
        );

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(item -> {
            drawer.closeDrawer(GravityCompat.START);

            int itemId = item.getItemId();

            if (itemId == R.id.nav_account_settings) {
                if (UserStorage.getInstance().getCurrentUser().getValue() != null && UserStorage.getInstance().getCurrentUser().getValue().getRole() == Role.DRIVER) {
                    showFragment(AccountSettingsFragment.newInstance());
                    return true;
                } else {
                    showFragment(AccountSettingsFragment.newInstance());
                    return true;
                }
            }
            else if (itemId == R.id.nav_ride_history) { showFragment(RideHistoryFragment.newInstance()); return true; }
            else if (itemId == R.id.nav_profile_changes) {
                showFragment(ProfileChangesFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_register_driver) {
                showFragment(RegisterDriverFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_log_out) {
                logout();
                return true;
            } else if (itemId == R.id.nav_log_in) {
                Intent intent = new Intent(MainActivity.this, LoginSignupActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_register) {
                Intent intent = new Intent(MainActivity.this, LoginSignupActivity.class);
                startActivity(intent);
                finish();
                return true;
            }

            return true;
        });

        btnMenu = findViewById(R.id.btn_menu);
        btnMapSearch = findViewById(R.id.btn_map_search);
        btnChat = findViewById(R.id.btn_chat);
        updateMapSearchVisibility();
        routeLoadingSpinner = findViewById(R.id.route_loading_spinner);
        loadingIndicatorController = new LoadingIndicatorController(routeLoadingSpinner);
    }

    private void initControllers() {
        rideOrderUiController = new RideOrderUiController(this);
        rideOrderFromInput = rideOrderUiController.fromInput;
        rideOrderToInput = rideOrderUiController.toInput;
        rideOrderUseMyLocation = rideOrderUiController.useMyLocation;
        rideOrderPickOnMap = rideOrderUiController.pickOnMap;
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
    }

    private void bindRideOrderUi() {
        rideOrderUiController.bindCallbacks(new RideOrderUiController.Callbacks() {
            @Override
            public void onUseMyLocation() {
                if (rideOrderAddWaypointController != null) {
                    rideOrderAddWaypointController.addFromMyLocation();
                }
            }

            @Override
            public void onPickOnMap() {
                isPickOnMapActive = true;
                collapseRideOrderSheet();
                Toast.makeText(MainActivity.this, "Tap on the map to add a waypoint.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onToggleCollapsed() {
                rideOrderSheetController.toggleCollapsed();
            }

            @Override
            public void onSelectOption(View option) {
                selectRideOption(option);
            }

            @Override
            public void onToggleExtra(View option) {
                toggleExtraOption(option);
            }

            @Override
            public void onConfirm() {
                rideOrderLogicController.submitRideOrder(
                        currentRole,
                        selectedVehicleType,
                        rideOptionBabyFriendly.isSelected(),
                        rideOptionPetFriendly.isSelected()
                );
            }

            @Override
            public boolean onFromEditorAction(int actionId) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (autocompleteController != null) {
                        autocompleteController.geocodeFromInput(rideOrderFromInput, true);
                        autocompleteController.hideFromSuggestions();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onToEditorAction(int actionId) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (autocompleteController != null) {
                        autocompleteController.geocodeFromInput(rideOrderToInput, false);
                        autocompleteController.hideToSuggestions();
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onFromFocusLost() {
                if (autocompleteController != null) {
                    autocompleteController.hideFromSuggestions();
                }
            }

            @Override
            public void onToFocusLost() {
                if (autocompleteController != null) {
                    autocompleteController.hideToSuggestions();
                }
            }
        });

        selectRideOption(rideOptionStandard);
        rideOrderLogicController.initRidePriceCard();
        rideOrderLogicController.initScheduleTimeInputs((min, max, next) -> new TimeFieldWatcher(min, max, next));
        rideOrderLogicController.addInviteEmailRow();

        btnMapSearch.setOnClickListener(v -> rideOrderSheetController.toggle());
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                hideRideOrderSheet();
            }
        });
    }

    private void bindObservers() {
        RidePlanningStorage.getInstance().getWaypointsReadOnly().observe(this, waypoints -> {
            List<WaypointDto> safeWaypoints = waypoints == null ? new ArrayList<>() : waypoints;
            rideOrderWaypointsController.renderWaypoints(safeWaypoints);
            if (mapUiController != null) {
                mapUiController.syncRideOrderMarkers(safeWaypoints);
            }
            rideOrderLogicController.resetRouteAndPriceOnWaypointsChanged();
            if (safeWaypoints.size() >= 2) {
                RouteService.getInstance().drawRoute(mapView, safeWaypoints);
            } else {
                RouteService.getInstance().clearRoute(mapView);
            }
        });

        RouteService.getInstance().setRouteLoadingListener(isLoading -> {
            if (loadingIndicatorController != null) {
                loadingIndicatorController.onRouteLoadingChanged(isLoading);
            }
        });
        RouteService.getInstance().setRouteInfoListener(new RouteService.RouteInfoListener() {
            @Override
            public void onRouteInfo(double meters, double durationSeconds) {
                rideOrderLogicController.onRouteInfo(meters, durationSeconds, selectedVehicleType);
            }

            @Override
            public void onRouteCleared() {
                rideOrderLogicController.onRouteCleared();
            }
        });

        ReviewStorage.getInstance().getRideId().observe(this, (rideId) -> {
            if (rideId != null)
                showModal(new ReviewModalFragment());
            else {
                FrameLayout modalContainer = findViewById(R.id.modal_container);
                modalContainer.setVisibility(View.GONE);
                modalContainer.removeAllViews();
            }
        });

        UserStorage.getInstance().getCurrentUser().observe(this, currentUser -> {
            SharedPreferences sp2 = getSharedPreferences("app_prefs", MODE_PRIVATE);
            String token2 = sp2.getString("jwt", null);
            if (currentUser == null) {
                if (token2 != null && !token2.isEmpty()) {
                    return;
                }
                setMenuOptions(Role.GUEST);
                WsConnectionOwner.getInstance(getApplicationContext())
                        .stop("MainActivity.currentUser=null");
                return;
            }
            setMenuOptions(currentUser.getRole());
            fillDrawerHeader();
            Long userId = currentUser.getId();
            if (userId == null || userId == 0L) {
                return;
            }
            WsConnectionOwner.getInstance(getApplicationContext())
                    .requestConnectForUser(userId, "MainActivity.currentUser");
        });

        UserStorage.getInstance().getCurrentUserAvatar().observe(this, avatar -> {
            fillDrawerHeader();
        });
    }

    private void loadUserIfAuthenticated() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        setMenuOptions(Role.GUEST);

        if (token != null && !token.isEmpty()) {
            try { UserService.getInstance(getApplicationContext()).loadCurrentUser(); }
            catch (Exception e) { Log.e(TAG, "Failed to load current user", e); }
            try { UserService.getInstance(getApplicationContext()).loadCurrentUserAvatar(); }
            catch (Exception e) { Log.e(TAG, "Failed to load current user avatar", e); }
            String roleString = sharedPreferences.getString("role", "GUEST");
            Role role = Role.valueOf(roleString);
            if (role == Role.DRIVER) {
                try { UserService.getInstance(getApplicationContext()).loadCurrentUserVehicle(); }
                catch (Exception e) { Log.e(TAG, "Failed to load current user vehicle", e); }
                try { UserService.getInstance(getApplicationContext()).loadCurrentUserStats(); }
                catch (Exception e) { Log.e(TAG, "Failed to load current user stats", e); }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            boolean granted = false;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }
            if (granted) {
                if (mapUiController != null) {
                    mapUiController.setupLocationOverlay(true, null);
                }
            } else {
                Toast.makeText(this, "Location permission is required to show your position.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
