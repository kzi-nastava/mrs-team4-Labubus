package com.example.ubre.ui.main;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
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
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.enums.UserStatus;
import com.example.ubre.ui.enums.VehicleType;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.dtos.VehicleDto;
import com.example.ubre.ui.services.LoginService;
import com.example.ubre.ui.services.ServiceUtils;
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

    private MapView map;
    private View btnMenu;
    private View btnChat;
    private DrawerLayout drawer;
    private UserDto currentUser;
    private VehicleDto currentVehicle; // If role is DRIVER, that is drivers vehicle
    LoginService loginService = ServiceUtils.loginService;



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

        // Example role assignment; in a real app, this would come from user authentication
        UserDto currentUser = new UserDto(1L, Role.ADMIN, "", "registered@user.com", "John", "Doe", "1234567890", "123 Main St", UserStatus.ACTIVE);
        currentVehicle = new VehicleDto(1L, "Toyota Prius", VehicleType.STANDARD, "ABC-123", 4, true, false);

        setMenuOptions(currentUser.getRole());
        fillDrawerHeader(currentUser);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(item -> {
            drawer.closeDrawer(GravityCompat.START);

            int itemId = item.getItemId();

            if (itemId == R.id.nav_account_settings) {
                if (currentUser.getRole() == Role.DRIVER) {
                    showFragment(AccountSettingsFragment.newInstance(currentUser, currentVehicle));
                    return true;
                } else {
                    showFragment(AccountSettingsFragment.newInstance(currentUser, null));
                    return true;
                }
            }
            else if (itemId == R.id.nav_ride_history) { showFragment(RideHistoryFragment.newInstance(currentUser)); return true; }
            else if (itemId == R.id.nav_profile_changes) {
                showFragment(ProfileChangesFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_log_out) {
                logout();
                return true;
            }

            return true;
        });

        btnMenu = findViewById(R.id.btn_menu);
        btnChat = findViewById(R.id.btn_chat);


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
    private void fillDrawerHeader(UserDto user) {
        NavigationView nav = findViewById(R.id.nav_view);

        ImageView avatar = nav.getHeaderView(0).findViewById(R.id.img_avatar);
        TextView name = nav.getHeaderView(0).findViewById(R.id.txt_name);
        TextView phone = nav.getHeaderView(0).findViewById(R.id.txt_phone);

        name.setText(user.getName() + " " + user.getSurname());
        phone.setText(user.getPhone());

        String url = user.getAvatarUrl();
        if (url != null && !url.isEmpty()) {
            Glide.with(this).load(url).circleCrop().into(avatar);
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

    private void logout() {

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            Intent intent = new Intent(MainActivity.this, LoginSignupActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        loginService.logout("Bearer " + token).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Logout", "Logout");

                if (response.isSuccessful()) {
                    sharedPreferences.edit().clear().apply();
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
    }


}
