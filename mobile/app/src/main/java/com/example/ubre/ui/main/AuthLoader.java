package com.example.ubre.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.services.RideService;
import com.example.ubre.ui.services.UserService;

class AuthLoader {

    private static final String TAG = "AuthLoader";

    private final Context appContext;
    private final MainDrawerController drawerController;

    AuthLoader(Context appContext, MainDrawerController drawerController) {
        this.appContext = appContext;
        this.drawerController = drawerController;
    }

    void loadUserIfAuthenticated() {
        SharedPreferences sharedPreferences = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        if (drawerController != null) {
            drawerController.setMenuOptions(Role.GUEST);
        }

        if (token != null && !token.isEmpty()) {
            try { UserService.getInstance(appContext).loadCurrentUser(); }
            catch (Exception e) { Log.e(TAG, "Failed to load current user", e); }
            try { UserService.getInstance(appContext).loadCurrentUserAvatar(); }
            catch (Exception e) { Log.e(TAG, "Failed to load current user avatar", e); }
            try { RideService.getInstance().getCurrentRide(appContext); }
            catch (Exception e) { Log.e(TAG, "Failed to load current ride", e); }
            String roleString = sharedPreferences.getString("role", "GUEST");
            Role role;
            try {
                role = Role.valueOf(roleString);
            } catch (IllegalArgumentException e) {
                role = Role.GUEST;
            }
            if (role == Role.DRIVER) {
                try { UserService.getInstance(appContext).loadCurrentUserVehicle(); }
                catch (Exception e) { Log.e(TAG, "Failed to load current user vehicle", e); }
                try { UserService.getInstance(appContext).loadCurrentUserStats(); }
                catch (Exception e) { Log.e(TAG, "Failed to load current user stats", e); }
            }
        }
    }
}
