package com.example.ubre.ui.main;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.Canvas;

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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Duration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
import com.example.ubre.ui.dtos.RideDto;
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.dtos.VehicleDto;
import com.example.ubre.ui.apis.LoginApi;
import com.example.ubre.ui.services.UserService;
import com.example.ubre.ui.storages.ReviewStorage;
import com.example.ubre.ui.storages.ProfileChangeStorage;
import com.example.ubre.ui.storages.UserStorage;
import com.google.android.material.navigation.NavigationView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.Marker;

import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.services.RouteService;
import com.example.ubre.ui.services.RidePlanningService;
import com.example.ubre.ui.storages.RidePlanningStorage;
import com.example.ubre.ui.services.GeocodingService;
import com.example.ubre.ui.utils.TextNormalizer;
import com.example.ubre.ui.adapters.AutocompleteAdapter;
import com.example.ubre.ui.services.PriceEstimateService;
import com.example.ubre.ui.enums.VehicleType;
import com.example.ubre.ui.dtos.RideOrderRequest;
import com.example.ubre.ui.dtos.RideOrderWaypoint;
import com.example.ubre.ui.services.RideService;
import com.example.ubre.ui.utils.TopToast;
import com.example.ubre.ui.dtos.RideOrderRequest;
import com.example.ubre.ui.dtos.RideOrderWaypoint;
import com.example.ubre.ui.services.RideService;
import com.example.ubre.ui.utils.TopToast;

import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST = 2001;
    private MapView map;
    private View btnMenu;
    private View btnMapSearch;
    private View btnChat;
    private Role currentRole = Role.GUEST;
    private DrawerLayout drawer;
    private BottomSheetBehavior<View> rideOrderSheetBehavior;
    private MyLocationNewOverlay myLocationOverlay;
    private TextInputEditText rideOrderFromInput;
    private TextInputEditText rideOrderToInput;
    private View rideOrderUseMyLocation;
    private View rideOrderPickOnMap;
    private LinearLayout rideOrderStopsContainer;
    private View rideOrderHandle;
    private View rideOptionStandard;
    private View rideOptionLuxury;
    private View rideOptionVan;
    private View rideOptionBabyFriendly;
    private View rideOptionPetFriendly;
    private FrameLayout rideOrderPriceContainer;
    private TextView rideOrderPriceValue;
    private Double lastRouteDistanceMeters;
    private Double lastRouteDurationSeconds;
    private Double lastPriceEstimate;
    private VehicleType selectedVehicleType = VehicleType.STANDARD;
    private View rideOrderConfirmButton;
    private int priceRequestSeq = 0;
    private Double lastPriceDistanceMeters;
    private Integer lastPriceVehicleType;
    private com.google.android.material.checkbox.MaterialCheckBox rideOrderScheduleCheck;
    private View rideOrderScheduleContainer;
    private com.google.android.material.textfield.TextInputEditText rideOrderHourInput;
    private com.google.android.material.textfield.TextInputEditText rideOrderMinuteInput;
    private LinearLayout rideOrderInviteContainer;
    private RecyclerView rideOrderFromSuggestionsView;
    private RecyclerView rideOrderToSuggestionsView;
    private AutocompleteAdapter fromSuggestionsAdapter;
    private AutocompleteAdapter toSuggestionsAdapter;
    private final List<Marker> rideOrderMarkers = new ArrayList<>();
    private boolean isPickOnMapActive = false;
    private GeocodingService geocodingService;
    private ProgressBar routeLoadingSpinner;
    private int geocodeInFlight = 0;
    private boolean routeLoading = false;
    private final Handler autocompleteHandler = new Handler(Looper.getMainLooper());
    private Runnable fromAutocompleteRunnable;
    private Runnable toAutocompleteRunnable;
    private java.util.List<com.example.ubre.ui.dtos.GeocodingResult> fromSuggestions = new ArrayList<>();
    private java.util.List<com.example.ubre.ui.dtos.GeocodingResult> toSuggestions = new ArrayList<>();
    private boolean suppressAutocomplete = false;
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
                if (map != null) map.setVisibility(View.INVISIBLE);
                if (btnMenu != null) btnMenu.setVisibility(View.GONE);
                updateMapSearchVisibility();
                if (btnChat != null) btnChat.setVisibility(View.GONE);
            } else {
                findViewById(R.id.fragment_container).setVisibility(View.GONE);
                if (map != null) map.setVisibility(View.VISIBLE);
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


        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(
                CustomZoomButtonsController.Visibility.NEVER
        );
        map.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (rideOrderSheetBehavior != null &&
                        rideOrderSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                    addWaypointFromMap(p);
                    collapseRideOrderSheet();
                    isPickOnMapActive = false;
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        }));


        MapController controller = (MapController) map.getController();
        controller.setZoom(14.0);
        controller.setCenter(new GeoPoint(45.2671, 19.8335));

        setupLocationOverlay();
        geocodingService = GeocodingService.getInstance(this);

        drawer = findViewById(R.id.main);

        findViewById(R.id.btn_menu).setOnClickListener(v ->
                drawer.openDrawer(GravityCompat.START)
        );

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        setMenuOptions(Role.GUEST);

        if (token != null && !token.isEmpty()) {
            try { UserService.getInstance(getApplicationContext()).loadCurrentUser(); }
            catch (Exception e) { Log.e(TAG, "Failed to load current user", e); }
            try { UserService.getInstance(getApplicationContext()).loadCurrentUserAvatar(); }
            catch (Exception e) { Log.e(TAG, "Failed to load current user avatar", e); }
            // driver only (extract role from shared pref
            String roleString = sharedPreferences.getString("role", "GUEST");
            Role role = Role.valueOf(roleString);
            if (role == Role.DRIVER) {
                try { UserService.getInstance(getApplicationContext()).loadCurrentUserVehicle(); }
                catch (Exception e) { Log.e(TAG, "Failed to load current user vehicle", e); }
                try { UserService.getInstance(getApplicationContext()).loadCurrentUserStats(); }
                catch (Exception e) { Log.e(TAG, "Failed to load current user stats", e); }
            }
        }










        // SEKCIJA ZA OSLUŠKIVANJE PROMENA KORISNIKA

        UserStorage.getInstance().getCurrentUser().observe(this, currentUser -> {
            SharedPreferences sp2 = getSharedPreferences("app_prefs", MODE_PRIVATE);
            String token2 = sp2.getString("jwt", null);
            if (currentUser == null) {
                if (token2 != null && !token2.isEmpty()) { // user se učitava, samo čekaj
                    return;
                }
                // ako nema ni korisnika a ni tokena, onda je gost
                setMenuOptions(Role.GUEST);
                return;
            }
            setMenuOptions(currentUser.getRole());
            fillDrawerHeader();
        });

        UserStorage.getInstance().getCurrentUserAvatar().observe(this, avatar -> {
            fillDrawerHeader();
        });














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
        updateGuestRideOrderState();
        routeLoadingSpinner = findViewById(R.id.route_loading_spinner);

        View rideOrderSheet = findViewById(R.id.ride_order_sheet);
        rideOrderSheetBehavior = BottomSheetBehavior.from(rideOrderSheet);
        rideOrderSheetBehavior.setHideable(true);
        rideOrderSheetBehavior.setPeekHeight(dpToPx(96), true);
        rideOrderSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        rideOrderSheetBehavior.setDraggable(true);

        rideOrderFromInput = findViewById(R.id.ride_order_from_input);
        rideOrderToInput = findViewById(R.id.ride_order_to_input);
        rideOrderUseMyLocation = findViewById(R.id.ride_order_use_my_location);
        rideOrderPickOnMap = findViewById(R.id.ride_order_pick_on_map);
        rideOrderStopsContainer = findViewById(R.id.ride_order_stops_container);
        rideOrderHandle = findViewById(R.id.ride_order_handle);
        rideOptionStandard = findViewById(R.id.ride_option_standard);
        rideOptionLuxury = findViewById(R.id.ride_option_luxury);
        rideOptionVan = findViewById(R.id.ride_option_van);
        rideOptionBabyFriendly = findViewById(R.id.ride_order_baby_friendly);
        rideOptionPetFriendly = findViewById(R.id.ride_order_pet_friendly);
        rideOrderPriceContainer = findViewById(R.id.ride_order_price_container);
        rideOrderScheduleCheck = findViewById(R.id.ride_order_schedule_check);
        rideOrderScheduleContainer = findViewById(R.id.ride_order_schedule_container);
        rideOrderHourInput = findViewById(R.id.ride_order_time_hour_input);
        rideOrderMinuteInput = findViewById(R.id.ride_order_time_minute_input);
        rideOrderInviteContainer = findViewById(R.id.ride_order_invite_container);
        rideOrderConfirmButton = findViewById(R.id.ride_order_confirm);
        rideOrderFromSuggestionsView = findViewById(R.id.ride_order_from_suggestions);
        rideOrderToSuggestionsView = findViewById(R.id.ride_order_to_suggestions);

        fromSuggestionsAdapter = new AutocompleteAdapter(position -> {
            if (position >= 0 && position < fromSuggestions.size()) {
                applySuggestion(fromSuggestions.get(position), true);
                hideFromSuggestions();
            }
        });
        toSuggestionsAdapter = new AutocompleteAdapter(position -> {
            if (position >= 0 && position < toSuggestions.size()) {
                applySuggestion(toSuggestions.get(position), false);
                hideToSuggestions();
            }
        });

        rideOrderFromSuggestionsView.setLayoutManager(new LinearLayoutManager(this));
        rideOrderFromSuggestionsView.setAdapter(fromSuggestionsAdapter);
        rideOrderToSuggestionsView.setLayoutManager(new LinearLayoutManager(this));
        rideOrderToSuggestionsView.setAdapter(toSuggestionsAdapter);

        rideOrderUseMyLocation.setOnClickListener(v -> addWaypointFromMyLocation());
        rideOrderPickOnMap.setOnClickListener(v -> {
            isPickOnMapActive = true;
            collapseRideOrderSheet();
            Toast.makeText(this, "Tap on the map to add a waypoint.", Toast.LENGTH_SHORT).show();
        });

        rideOrderHandle.setOnClickListener(v -> toggleRideOrderSheetCollapsed());

        rideOptionStandard.setOnClickListener(v -> selectRideOption(rideOptionStandard));
        rideOptionLuxury.setOnClickListener(v -> selectRideOption(rideOptionLuxury));
        rideOptionVan.setOnClickListener(v -> selectRideOption(rideOptionVan));
        selectRideOption(rideOptionStandard);

        initRidePriceCard();
        rideOptionBabyFriendly.setOnClickListener(v -> toggleExtraOption(rideOptionBabyFriendly));
        rideOptionPetFriendly.setOnClickListener(v -> toggleExtraOption(rideOptionPetFriendly));

        rideOrderScheduleCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            rideOrderScheduleContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        initScheduleTimeInputs();
        addInviteEmailRow();
        rideOrderConfirmButton.setOnClickListener(v -> submitRideOrder());

        rideOrderFromInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                geocodeFromInput(rideOrderFromInput, true);
                hideFromSuggestions();
                return true;
            }
            return false;
        });

        rideOrderToInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                geocodeFromInput(rideOrderToInput, false);
                hideToSuggestions();
                return true;
            }
            return false;
        });

        rideOrderFromInput.addTextChangedListener(new SimpleAutocompleteWatcher(true));
        rideOrderToInput.addTextChangedListener(new SimpleAutocompleteWatcher(false));

        rideOrderFromInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideFromSuggestions();
            }
        });
        rideOrderToInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideToSuggestions();
            }
        });


        btnMapSearch.setOnClickListener(v -> toggleRideOrderSheet());

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                hideRideOrderSheet();
            }
        });

        RidePlanningStorage.getInstance().getWaypointsReadOnly().observe(this, waypoints -> {
            List<WaypointDto> safeWaypoints = waypoints == null ? new ArrayList<>() : waypoints;
            renderRideOrderWaypoints(safeWaypoints);
            syncRideOrderMarkers(safeWaypoints);
            // Invalidate current price until we have fresh route info for this set of waypoints
            lastRouteDistanceMeters = null;
            lastRouteDurationSeconds = null;
            lastPriceEstimate = null;
            updatePriceEstimate(null);
            if (safeWaypoints.size() >= 2) {
                RouteService.getInstance().drawRoute(map, safeWaypoints);
            } else {
                RouteService.getInstance().clearRoute(map);
            }
        });

        RouteService.getInstance().setRouteLoadingListener(isLoading -> {
            routeLoading = isLoading;
            updateLoadingSpinner();
        });
        RouteService.getInstance().setRouteInfoListener(new RouteService.RouteInfoListener() {
            @Override
            public void onRouteInfo(double meters, double durationSeconds) {
                lastRouteDistanceMeters = meters;
                lastRouteDurationSeconds = durationSeconds;
                requestPriceEstimate();
            }

            @Override
            public void onRouteCleared() {
                lastRouteDistanceMeters = null;
                lastRouteDurationSeconds = null;
                updatePriceEstimate(null);
            }
        });



        // Adding an observer that opens review modal when it's state is set
        ReviewStorage.getInstance().getRideId().observe(this, (rideId) -> {
            if (rideId != null)
                showModal(new ReviewModalFragment());
            else {
                FrameLayout modalContainer = findViewById(R.id.modal_container);
                modalContainer.setVisibility(View.GONE);
                modalContainer.removeAllViews();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) map.onResume();
        if (myLocationOverlay != null) myLocationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (map != null) map.onPause();
        if (myLocationOverlay != null) myLocationOverlay.disableMyLocation();
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
        map.setVisibility(View.INVISIBLE);
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

    private void logout() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
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

    private void toggleRideOrderSheet() {
        int state = rideOrderSheetBehavior.getState();
        if (state == BottomSheetBehavior.STATE_HIDDEN) {
            rideOrderSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            rideOrderSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void toggleRideOrderSheetCollapsed() {
        int state = rideOrderSheetBehavior.getState();
        if (state == BottomSheetBehavior.STATE_EXPANDED) {
            rideOrderSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (state == BottomSheetBehavior.STATE_COLLAPSED) {
            rideOrderSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (state == BottomSheetBehavior.STATE_HIDDEN) {
            rideOrderSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void hideRideOrderSheet() {
        if (rideOrderSheetBehavior != null &&
                rideOrderSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            rideOrderSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void updateMapSearchVisibility() {
        if (btnMapSearch == null) {
            return;
        }
        boolean hasFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;
        boolean allowedRole = currentRole == Role.GUEST || currentRole == Role.REGISTERED_USER;
        btnMapSearch.setVisibility(!hasFragments && allowedRole ? View.VISIBLE : View.GONE);
    }

    private boolean canGuestAddWaypoint() {
        if (currentRole != Role.GUEST) {
            return true;
        }
        return RidePlanningStorage.getInstance().getWaypointsSnapshot().size() < 2;
    }

    private void updateGuestRideOrderState() {
        if (rideOrderConfirmButton == null) {
            return;
        }
        boolean isGuest = currentRole == Role.GUEST;
        rideOrderConfirmButton.setEnabled(!isGuest);
        rideOrderConfirmButton.setAlpha(isGuest ? 0.5f : 1.0f);
    }

    private void collapseRideOrderSheet() {
        if (rideOrderSheetBehavior != null &&
                rideOrderSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            rideOrderSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }


    private void addWaypointFromMyLocation() {
        if (!canGuestAddWaypoint()) {
            TopToast.show(this, "Guest limit", "Guests can add up to 2 waypoints.");
            return;
        }
        if (myLocationOverlay == null || myLocationOverlay.getMyLocation() == null) {
            Toast.makeText(this, "Current location not available yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        GeoPoint point = myLocationOverlay.getMyLocation();
        addWaypoint("My location", point);
        int index = RidePlanningStorage.getInstance().getWaypointsSnapshot().size() - 1;
        if (geocodingService != null) {
            geocodeInFlight++;
            updateLoadingSpinner();
            geocodingService.reverse(point.getLatitude(), point.getLongitude(), new GeocodingService.GeocodingCallback() {
                @Override
                public void onResult(com.example.ubre.ui.dtos.GeocodingResult result) {
                    geocodeInFlight = Math.max(0, geocodeInFlight - 1);
                    updateLoadingSpinner();
                    if (result == null || result.displayName == null) {
                        return;
                    }
                    String label = formatShortLabel(result.displayName);
                    runOnUiThread(() -> RidePlanningService.getInstance().updateWaypointLabelAt(index, label));
                }

                @Override
                public void onError(Throwable t) {
                    geocodeInFlight = Math.max(0, geocodeInFlight - 1);
                    updateLoadingSpinner();
                }
            });
        }
    }

    private void addWaypointFromMap(GeoPoint point) {
        if (!canGuestAddWaypoint()) {
            TopToast.show(this, "Guest limit", "Guests can add up to 2 waypoints.");
            return;
        }
        addWaypoint("Pinned location", point);
        int index = RidePlanningStorage.getInstance().getWaypointsSnapshot().size() - 1;
        if (geocodingService != null) {
            geocodeInFlight++;
            updateLoadingSpinner();
            geocodingService.reverse(point.getLatitude(), point.getLongitude(), new GeocodingService.GeocodingCallback() {
                @Override
                public void onResult(com.example.ubre.ui.dtos.GeocodingResult result) {
                    geocodeInFlight = Math.max(0, geocodeInFlight - 1);
                    updateLoadingSpinner();
                    if (result == null || result.displayName == null) {
                        return;
                    }
                    String label = formatShortLabel(result.displayName);
                    runOnUiThread(() -> RidePlanningService.getInstance().updateWaypointLabelAt(index, label));
                }

                @Override
                public void onError(Throwable t) {
                    geocodeInFlight = Math.max(0, geocodeInFlight - 1);
                    updateLoadingSpinner();
                }
            });
        }
    }

    private void addWaypoint(String baseLabel, GeoPoint point) {
        if (!canGuestAddWaypoint()) {
            TopToast.show(this, "Guest limit", "Guests can add up to 2 waypoints.");
            return;
        }
        int index = RidePlanningStorage.getInstance().getWaypointsSnapshot().size() + 1;
        String label = baseLabel + " " + index;
        WaypointDto waypoint = new WaypointDto(null, label, point.getLatitude(), point.getLongitude());
        RidePlanningService.getInstance().addWaypoint(waypoint);
    }

    private void geocodeFromInput(TextInputEditText input, boolean isFrom) {
        if (geocodingService == null || input.getText() == null) {
            return;
        }
        String query = TextNormalizer.toLatin(input.getText().toString().trim());
        if (query.isEmpty()) {
            return;
        }

        geocodingService.geocode(query, new GeocodingService.GeocodingCallback() {
            @Override
            public void onResult(com.example.ubre.ui.dtos.GeocodingResult result) {
                geocodeInFlight = Math.max(0, geocodeInFlight - 1);
                updateLoadingSpinner();
                if (result == null || result.lat == null || result.lon == null) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "No results found.", Toast.LENGTH_SHORT).show());
                    return;
                }
                double lat = Double.parseDouble(result.lat);
                double lon = Double.parseDouble(result.lon);
                String label = result.displayName == null ? query : formatShortLabel(result.displayName);
                WaypointDto waypoint = new WaypointDto(null, label, lat, lon);

                runOnUiThread(() -> {
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
                geocodeInFlight = Math.max(0, geocodeInFlight - 1);
                updateLoadingSpinner();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Geocoding failed.", Toast.LENGTH_SHORT).show());
            }
        });
        geocodeInFlight++;
        updateLoadingSpinner();
    }

    private void updateLoadingSpinner() {
        if (routeLoadingSpinner == null) {
            return;
        }
        boolean show = routeLoading || geocodeInFlight > 0;
        routeLoadingSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
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
        requestPriceEstimate();
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

    private void initScheduleTimeInputs() {
        if (rideOrderHourInput == null || rideOrderMinuteInput == null) {
            return;
        }

        LocalTime now = LocalTime.now();
        rideOrderHourInput.setText(String.format("%02d", now.getHour()));
        rideOrderMinuteInput.setText(String.format("%02d", now.getMinute()));

        rideOrderHourInput.addTextChangedListener(new TimeFieldWatcher(0, 23, rideOrderMinuteInput));
        rideOrderMinuteInput.addTextChangedListener(new TimeFieldWatcher(0, 59, null));
    }

    private void initRidePriceCard() {
        if (rideOrderPriceContainer == null) {
            return;
        }
        rideOrderPriceContainer.removeAllViews();
        View card = getLayoutInflater().inflate(R.layout.stat_card, rideOrderPriceContainer, false);

        TextView tvValue = card.findViewById(R.id.stat_value);
        TextView tvLabel = card.findViewById(R.id.stat_label);

        tvValue.setText("--");
        tvLabel.setText("Estimated price");

        rideOrderPriceContainer.addView(card);
        rideOrderPriceValue = tvValue;
    }

    private void requestPriceEstimate() {
        if (lastRouteDistanceMeters == null) {
            updatePriceEstimate(null);
            return;
        }
        int vehicleTypeValue = mapVehicleType(selectedVehicleType);
        priceRequestSeq++;
        int requestId = priceRequestSeq;
        lastPriceDistanceMeters = lastRouteDistanceMeters;
        lastPriceVehicleType = vehicleTypeValue;
        lastPriceEstimate = null;
        updatePriceEstimate(null);

        PriceEstimateService.getInstance(this).estimate(lastRouteDistanceMeters, vehicleTypeValue, new PriceEstimateService.PriceEstimateCallback() {
            @Override
            public void onResult(Double price) {
                runOnUiThread(() -> {
                    if (requestId != priceRequestSeq) return;
                    if (lastRouteDistanceMeters == null || lastPriceDistanceMeters == null) return;
                    if (Math.abs(lastRouteDistanceMeters - lastPriceDistanceMeters) > 0.5) return;
                    if (lastPriceVehicleType == null || lastPriceVehicleType != mapVehicleType(selectedVehicleType)) return;
                    updatePriceEstimate(price);
                });
            }

            @Override
            public void onError(Throwable t) {
                runOnUiThread(() -> {
                    if (requestId != priceRequestSeq) return;
                    updatePriceEstimate(null);
                });
            }
        });
    }

    private void updatePriceEstimate(Double price) {
        if (rideOrderPriceValue == null) {
            return;
        }
        if (price == null) {
            rideOrderPriceValue.setText("--");
            lastPriceEstimate = null;
        } else {
            rideOrderPriceValue.setText("$" + String.format("%.2f", price));
            lastPriceEstimate = price;
        }
    }

    private int mapVehicleType(VehicleType type) {
        switch (type) {
            case STANDARD:
                return 0;
            case LUXURY:
                return 1;
            case VAN:
                return 2;
            default:
                return 0;
        }
    }

    private void submitRideOrder() {
        if (currentRole == Role.GUEST) {
            TopToast.show(this, "Guest limit", "Guests cannot place orders.");
            return;
        }
        List<WaypointDto> waypoints = RidePlanningStorage.getInstance().getWaypointsSnapshot();
        if (waypoints.size() < 2) {
            TopToast.show(this, "Order error", "Please select at least 2 waypoints.");
            return;
        }
        if (lastRouteDistanceMeters == null || lastRouteDurationSeconds == null) {
            TopToast.show(this, "Order error", "Route info is not ready yet.");
            return;
        }
        if (lastPriceEstimate == null) {
            TopToast.show(this, "Order error", "Price estimate is not ready yet.");
            return;
        }

        Long creatorId = UserStorage.getInstance().getCurrentUser().getValue() != null
                ? UserStorage.getInstance().getCurrentUser().getValue().getId()
                : null;
        if (creatorId == null) {
            TopToast.show(this, "Order error", "User not authenticated.");
            return;
        }

        List<String> passengers = collectPassengerEmails();
        List<RideOrderWaypoint> orderWaypoints = new ArrayList<>();
        for (WaypointDto wp : waypoints) {
            orderWaypoints.add(new RideOrderWaypoint(
                    0L,
                    wp.getLabel(),
                    wp.getLatitude(),
                    wp.getLongitude(),
                    false
            ));
        }

        String scheduledTime = buildScheduledTimeOrEmpty();
        if (!scheduledTime.isEmpty() && !validateScheduledTimeWindow(scheduledTime)) {
            return;
        }
        int vehicleTypeValue = mapVehicleType(selectedVehicleType);

        RideOrderRequest request = new RideOrderRequest(
                0L,
                creatorId,
                passengers,
                orderWaypoints,
                vehicleTypeValue,
                rideOptionBabyFriendly.isSelected(),
                rideOptionPetFriendly.isSelected(),
                scheduledTime,
                lastRouteDistanceMeters,
                lastRouteDurationSeconds,
                lastPriceEstimate
        );

        RideService.getInstance().orderRide(this, request, new RideService.OrderCallback() {
            @Override
            public void onSuccess(RideDto ride) {
                clearRidePlanningState();
                TopToast.show(MainActivity.this, "Ride order", "Ride ordered successfully.");
            }

            @Override
            public void onError(String message) {
                TopToast.show(MainActivity.this, "Order error", message);
            }
        });
    }

    private boolean validateScheduledTimeWindow(String scheduledTime) {
        try {
            LocalDateTime scheduled = LocalDateTime.parse(scheduledTime);
            LocalDateTime now = LocalDateTime.now();
            if (scheduled.isBefore(now)) {
                TopToast.show(this, "Order error", "Scheduled time cannot be in the past.");
                return false;
            }
            long minutesAhead = Duration.between(now, scheduled).toMinutes();
            if (minutesAhead > 300) {
                TopToast.show(this, "Order error", "Scheduled time must be within 5 hours.");
                return false;
            }
            return true;
        } catch (Exception e) {
            TopToast.show(this, "Order error", "Invalid scheduled time.");
            return false;
        }
    }

    private List<String> collectPassengerEmails() {
        List<String> emails = new ArrayList<>();
        if (rideOrderInviteContainer == null) {
            return emails;
        }
        for (int i = 0; i < rideOrderInviteContainer.getChildCount(); i++) {
            View row = rideOrderInviteContainer.getChildAt(i);
            com.google.android.material.textfield.TextInputEditText input =
                    row.findViewById(R.id.invite_email_input);
            if (input != null && input.getText() != null) {
                String value = input.getText().toString().trim();
                if (!value.isEmpty()) {
                    emails.add(value);
                }
            }
        }
        return emails;
    }

    private String buildScheduledTimeOrEmpty() {
        if (rideOrderScheduleCheck == null || !rideOrderScheduleCheck.isChecked()) {
            return "";
        }
        if (rideOrderHourInput == null || rideOrderMinuteInput == null
                || rideOrderHourInput.getText() == null || rideOrderMinuteInput.getText() == null) {
            return "";
        }
        String hh = rideOrderHourInput.getText().toString().trim();
        String mm = rideOrderMinuteInput.getText().toString().trim();
        if (hh.isEmpty() || mm.isEmpty()) {
            return "";
        }
        int hour = Integer.parseInt(hh);
        int minute = Integer.parseInt(mm);
        java.time.LocalDate today = java.time.LocalDate.now();
        return String.format("%sT%02d:%02d:00", today, hour, minute);
    }

    private void clearRidePlanningState() {
        RidePlanningService.getInstance().clear();
        if (rideOrderInviteContainer != null) {
            rideOrderInviteContainer.removeAllViews();
            addInviteEmailRow();
        }
        lastRouteDistanceMeters = null;
        lastRouteDurationSeconds = null;
        lastPriceEstimate = null;
        updatePriceEstimate(null);
        RouteService.getInstance().clearRoute(map);
        for (Marker marker : rideOrderMarkers) {
            map.getOverlays().remove(marker);
        }
        rideOrderMarkers.clear();
        map.invalidate();
    }

    private void addInviteEmailRow() {
        if (rideOrderInviteContainer == null) {
            return;
        }
        View row = getLayoutInflater().inflate(R.layout.invite_passenger_item, rideOrderInviteContainer, false);
        View remove = row.findViewById(R.id.invite_remove);
        com.google.android.material.textfield.TextInputEditText input =
                row.findViewById(R.id.invite_email_input);

        remove.setOnClickListener(v -> {
            if (rideOrderInviteContainer.getChildCount() <= 1) {
                input.setText("");
                input.requestFocus();
                return;
            }
            rideOrderInviteContainer.removeView(row);
        });

        int index = rideOrderInviteContainer.getChildCount() + 1;
        input.setAutofillHints("invite_email_" + index);
        input.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_YES);

        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                addInviteEmailRow();
                return true;
            }
            return false;
        });

        rideOrderInviteContainer.addView(row);
        input.requestFocus();
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

    private void requestAutocomplete(String rawQuery, boolean isFrom) {
        if (geocodingService == null) {
            return;
        }
        if (isFrom && !rideOrderFromInput.hasFocus()) {
            hideFromSuggestions();
            return;
        }
        if (!isFrom && !rideOrderToInput.hasFocus()) {
            hideToSuggestions();
            return;
        }
        String query = TextNormalizer.toLatin(rawQuery.trim());
        if (query.length() < 3) {
            if (isFrom) {
                fromSuggestions = new ArrayList<>();
                fromSuggestionsAdapter.setItems(new ArrayList<>());
                hideFromSuggestions();
            } else {
                toSuggestions = new ArrayList<>();
                toSuggestionsAdapter.setItems(new ArrayList<>());
                hideToSuggestions();
            }
            return;
        }

        geocodingService.search(query, 6, new GeocodingService.SearchCallback() {
            @Override
            public void onResult(List<com.example.ubre.ui.dtos.GeocodingResult> results) {
                runOnUiThread(() -> {
                    List<com.example.ubre.ui.dtos.GeocodingResult> safe = results == null ? new ArrayList<>() : results;
                    ArrayList<String> labels = new ArrayList<>();
                    for (com.example.ubre.ui.dtos.GeocodingResult r : safe) {
                        if (r != null && r.displayName != null) {
                            labels.add(TextNormalizer.toLatin(r.displayName));
                        }
                    }
                    if (isFrom) {
                        fromSuggestions = safe;
                        fromSuggestionsAdapter.setQuery(query);
                        fromSuggestionsAdapter.setItems(labels);
                        rideOrderFromSuggestionsView.setVisibility(labels.isEmpty() ? View.GONE : View.VISIBLE);
                    } else {
                        toSuggestions = safe;
                        toSuggestionsAdapter.setQuery(query);
                        toSuggestionsAdapter.setItems(labels);
                        rideOrderToSuggestionsView.setVisibility(labels.isEmpty() ? View.GONE : View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
            }
        });
    }

    private void hideFromSuggestions() {
        rideOrderFromSuggestionsView.setVisibility(View.GONE);
    }

    private void hideToSuggestions() {
        rideOrderToSuggestionsView.setVisibility(View.GONE);
    }

    private void applySuggestion(com.example.ubre.ui.dtos.GeocodingResult result, boolean isFrom) {
        if (result == null || result.lat == null || result.lon == null) {
            return;
        }
        double lat = Double.parseDouble(result.lat);
        double lon = Double.parseDouble(result.lon);
        String label = result.displayName == null ? "" : TextNormalizer.toLatin(result.displayName);
        WaypointDto waypoint = new WaypointDto(null, label, lat, lon);
        suppressAutocomplete = true;
        List<WaypointDto> current = RidePlanningStorage.getInstance().getWaypointsSnapshot();
        if (isFrom) {
            if (current.isEmpty()) {
                RidePlanningService.getInstance().addWaypoint(waypoint);
            } else {
                RidePlanningService.getInstance().updateWaypointAt(0, waypoint);
            }
            hideFromSuggestions();
            rideOrderFromInput.setText(label);
            rideOrderFromInput.clearFocus();
        } else {
            if (current.size() < 2) {
                RidePlanningService.getInstance().addWaypoint(waypoint);
            } else {
                RidePlanningService.getInstance().updateWaypointAt(1, waypoint);
            }
            hideToSuggestions();
            rideOrderToInput.setText(label);
            rideOrderToInput.clearFocus();
        }
        suppressAutocomplete = false;
    }

    private class SimpleAutocompleteWatcher implements TextWatcher {
        private final boolean isFrom;

        SimpleAutocompleteWatcher(boolean isFrom) {
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
                if (fromAutocompleteRunnable != null) {
                    autocompleteHandler.removeCallbacks(fromAutocompleteRunnable);
                }
                fromAutocompleteRunnable = () -> requestAutocomplete(s.toString(), true);
                autocompleteHandler.postDelayed(fromAutocompleteRunnable, 350);
            } else {
                if (toAutocompleteRunnable != null) {
                    autocompleteHandler.removeCallbacks(toAutocompleteRunnable);
                }
                toAutocompleteRunnable = () -> requestAutocomplete(s.toString(), false);
                autocompleteHandler.postDelayed(toAutocompleteRunnable, 350);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    private String formatShortLabel(String displayName) {
        return TextNormalizer.toLatin(displayName).trim();
    }

    private String ellipsizeLabel(String text, int maxChars) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxChars) {
            return text;
        }
        return text.substring(0, Math.max(0, maxChars - 3)).trim() + "...";
    }

    private void renderRideOrderWaypoints(List<WaypointDto> waypoints) {
        if (waypoints.isEmpty()) {
            rideOrderFromInput.setText("");
            rideOrderToInput.setText("");
            rideOrderUseMyLocation.setVisibility(View.VISIBLE);
            rideOrderStopsContainer.removeAllViews();
            return;
        }

        WaypointDto first = waypoints.get(0);
        rideOrderFromInput.setText(first.getLabel());
        rideOrderUseMyLocation.setVisibility(View.GONE);

        if (waypoints.size() >= 2) {
            WaypointDto second = waypoints.get(1);
            rideOrderToInput.setText(second.getLabel());
        } else {
            rideOrderToInput.setText("");
        }

        rideOrderStopsContainer.removeAllViews();
        for (int i = 0; i < waypoints.size(); i++) {
            int index = i;
            WaypointDto waypoint = waypoints.get(i);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_waypoint_chip));
            row.setPadding(dpToPx(12), dpToPx(8), dpToPx(8), dpToPx(8));

            TextView label = new TextView(this);
            String displayLabel = ellipsizeLabel(waypoint.getLabel(), 44);
            label.setText((i + 1) + ".\u00A0\u00A0" + displayLabel);
            label.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            label.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_medium));
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );
            label.setLayoutParams(labelParams);

            ImageView remove = new ImageView(this);
            remove.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_cancel));
            remove.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary));
            LinearLayout.LayoutParams removeParams = new LinearLayout.LayoutParams(
                    dpToPx(18),
                    dpToPx(18)
            );
            remove.setLayoutParams(removeParams);
            remove.setOnClickListener(v -> removeWaypointAt(index));

            row.addView(label);
            row.addView(remove);

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(0, dpToPx(6), 0, 0);
            row.setLayoutParams(rowParams);

            rideOrderStopsContainer.addView(row);
        }
    }

    private void removeWaypointAt(int index) {
        RidePlanningService.getInstance().removeWaypointAt(index);
    }

    private void syncRideOrderMarkers(List<WaypointDto> waypoints) {
        for (Marker marker : rideOrderMarkers) {
            map.getOverlays().remove(marker);
        }
        rideOrderMarkers.clear();

        for (WaypointDto waypoint : waypoints) {
            GeoPoint point = new GeoPoint(waypoint.getLatitude(), waypoint.getLongitude());
            Marker marker = new Marker(map);
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_waypoint_red));
            marker.setTitle(waypoint.getLabel());
            map.getOverlays().add(marker);
            rideOrderMarkers.add(marker);
        }
        map.invalidate();
    }

    private void setupLocationOverlay() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );
            return;
        }

        if (myLocationOverlay == null) {
            myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
            myLocationOverlay.setDrawAccuracyEnabled(true);
            Drawable pin = ContextCompat.getDrawable(this, R.drawable.ic_my_location_blue);
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

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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
                setupLocationOverlay();
            } else {
                Toast.makeText(this, "Location permission is required to show your position.", Toast.LENGTH_SHORT).show();
            }
        }
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
