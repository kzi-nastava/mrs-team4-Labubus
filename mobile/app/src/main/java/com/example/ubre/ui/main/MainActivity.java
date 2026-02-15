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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.ubre.ui.enums.VehicleType;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.dtos.VehicleDto;
import com.example.ubre.ui.apis.LoginApi;
import com.example.ubre.ui.services.UserService;
import com.example.ubre.ui.storages.ReviewStorage;
import com.example.ubre.ui.storages.ProfileChangeStorage;
import com.example.ubre.ui.storages.UserStorage;
import com.google.android.material.navigation.NavigationView;
import com.bumptech.glide.Glide;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapController;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private MapView map;
    private View btnMenu;
    private View btnChat;
    private DrawerLayout drawer;
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
                if (btnChat != null) btnChat.setVisibility(View.GONE);
            } else {
                findViewById(R.id.fragment_container).setVisibility(View.GONE);
                if (map != null) map.setVisibility(View.VISIBLE);
                if (btnMenu != null) btnMenu.setVisibility(View.VISIBLE);
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


        MapController controller = (MapController) map.getController();
        controller.setZoom(14.0);
        controller.setCenter(new GeoPoint(45.2671, 19.8335));

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
        btnChat = findViewById(R.id.btn_chat);


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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }

    private void setMenuOptions(Role role) {
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
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        map.setVisibility(View.INVISIBLE);
        if (btnMenu != null) btnMenu.setVisibility(View.GONE);
        if (btnChat != null) btnChat.setVisibility(View.GONE);

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
}
