package com.example.ubre.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.example.ubre.R;
import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.LoginApi;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.services.WsConnectionOwner;
import com.example.ubre.ui.storages.CurrentRideStorage;
import com.example.ubre.ui.storages.ProfileChangeStorage;
import com.example.ubre.ui.storages.RidePlanningStorage;
import com.example.ubre.ui.storages.UserStorage;
import com.google.android.material.navigation.NavigationView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainDrawerController {

    private final MainActivity activity;
    private final DrawerLayout drawer;
    private final NavigationView navigationView;
    private final LoginApi loginApi;

    MainDrawerController(MainActivity activity, DrawerLayout drawer, NavigationView navigationView) {
        this.activity = activity;
        this.drawer = drawer;
        this.navigationView = navigationView;
        this.loginApi = ApiClient.getClient().create(LoginApi.class);
    }

    void init() {
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(item -> {
            drawer.closeDrawer(GravityCompat.START);

            int itemId = item.getItemId();

            if (itemId == R.id.nav_account_settings) {
                if (UserStorage.getInstance().getCurrentUser().getValue() != null
                        && UserStorage.getInstance().getCurrentUser().getValue().getRole() == Role.DRIVER) {
                    activity.showFragment(AccountSettingsFragment.newInstance());
                    return true;
                } else {
                    activity.showFragment(AccountSettingsFragment.newInstance());
                    return true;
                }
            } else if (itemId == R.id.nav_ride_history) {
                activity.showFragment(RideHistoryFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_favourites) {
                activity.showFragment(RideFavoritesFragment.newInstance());
                return true;
            }
            else if (itemId == R.id.nav_price_adjustment) {
                activity.showFragment(PriceAdjustmentFragment.newInstance());
                return true;
            }else if (itemId == R.id.nav_profile_changes) {
                activity.showFragment(ProfileChangesFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_register_driver) {
                activity.showFragment(RegisterDriverFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_reports) {
                activity.showFragment(ReportsFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_block_users) {
                activity.showFragment(BlockUsersFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_log_out) {
                logout();
                return true;
            } else if (itemId == R.id.nav_log_in) {
                goToLogin();
                return true;
            } else if (itemId == R.id.nav_register) {
                goToLogin();
                return true;
            } else if (itemId == R.id.nav_panic_notifications) {
                activity.showFragment(PanicNotificationsFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_scheduled) {
            activity.showFragment(ScheduledRidesFragment.newInstance());
            return true;
        }

            return true;
        });

        View header = navigationView.getHeaderView(0);
        if (header != null) {
            ImageView backIcon = header.findViewById(R.id.nav_back);
            backIcon.setOnClickListener(v ->
                    drawer.closeDrawer(GravityCompat.START)
            );
        }
    }

    void setMenuOptions(Role role) {
        Role safeRole = role == null ? Role.GUEST : role;
        activity.setCurrentRole(safeRole);
        navigationView.getMenu().clear();

        int menuRes;
        switch (safeRole) {
            case ADMIN: menuRes = R.menu.drawer_admin; break;
            case DRIVER: menuRes = R.menu.drawer_driver; break;
            case REGISTERED_USER: menuRes = R.menu.drawer_registered_user; break;
            default: menuRes = R.menu.drawer_guest; break;
        }

        navigationView.inflateMenu(menuRes);

        MenuItem logout = navigationView.getMenu().findItem(R.id.nav_log_out);
        if (logout != null) {
            SpannableString logoutText = new SpannableString(logout.getTitle());
            logoutText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, R.color.error)), 0, logoutText.length(), 0);
            logout.setTitle(logoutText);
        }

        activity.updateMapSearchVisibility();
        activity.updateGuestRideOrderState();
    }

    @SuppressLint("SetTextI18n")
    void fillDrawerHeader() {
        UserDto user = UserStorage.getInstance().getCurrentUser().getValue();

        View header = navigationView.getHeaderView(0);
        if (header == null) {
            return;
        }
        ImageView avatar = header.findViewById(R.id.img_avatar);
        TextView name = header.findViewById(R.id.txt_name);
        TextView phone = header.findViewById(R.id.txt_phone);

        if (user == null) {
            name.setText("John Doe");
            phone.setText("+381 XX XXX XXXX");
            Glide.with(activity).load(R.drawable.img_default_avatar).circleCrop().into(avatar);
            return;
        }

        name.setText(user.getName() + " " + user.getSurname());
        phone.setText(user.getPhone());

        byte[] avatarBytes = UserStorage.getInstance().getCurrentUserAvatar().getValue();

        if (avatarBytes != null) {
            Glide.with(activity).asBitmap().load(avatarBytes).circleCrop().into(avatar);
        } else {
            Glide.with(activity).load(R.drawable.img_default_avatar).circleCrop().into(avatar);
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(activity, LoginSignupActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    // WS connect/stop is owned by WsConnectionOwner (single owner).
    private void logout() {
        SharedPreferences sharedPreferences = activity.getApplicationContext()
                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (token == null) {
            WsConnectionOwner.getInstance(activity.getApplicationContext())
                    .stop("Logout.noToken");
            UserStorage.getInstance().clearUserStorage();
            ProfileChangeStorage.getInstance().clearProfileChangeStorage();
            RidePlanningStorage.getInstance().clear();
            CurrentRideStorage.getInstance().clear();
            Toast.makeText(activity.getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
            goToLogin();
            return;
        }

        loginApi.logout("Bearer " + token).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    WsConnectionOwner.getInstance(activity.getApplicationContext())
                            .stop("Logout.success");
                    sharedPreferences.edit().clear().apply();
                    UserStorage.getInstance().clearUserStorage();
                    ProfileChangeStorage.getInstance().clearProfileChangeStorage();
                    RidePlanningStorage.getInstance().clear();
                    CurrentRideStorage.getInstance().clear();
                    Toast.makeText(activity.getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                    goToLogin();
                } else {
                    Toast.makeText(activity.getApplicationContext(), "Logout failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(activity.getApplicationContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
