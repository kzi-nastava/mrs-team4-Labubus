package com.example.ubre.ui.main;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.ubre.R;
import com.example.ubre.ui.model.Role;
import com.example.ubre.ui.model.UserDto;
import com.google.android.material.navigation.NavigationView;
import com.bumptech.glide.Glide;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapController;

import java.time.Instant;

public class MainActivity extends AppCompatActivity {

    private MapView map;
    private View btnMenu;
    private DrawerLayout drawer;
    private UserDto currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setDecorFitsSystemWindows(false);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            boolean hasFragments = getSupportFragmentManager().getBackStackEntryCount() > 0;

            if (hasFragments) {
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                if (map != null) map.setVisibility(View.GONE);
                if (btnMenu != null) btnMenu.setVisibility(View.GONE);
            } else {
                findViewById(R.id.fragment_container).setVisibility(View.GONE);
                if (map != null) map.setVisibility(View.VISIBLE);
                if (btnMenu != null) btnMenu.setVisibility(View.VISIBLE);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
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
        UserDto currentUser = new UserDto("1", Role.ADMIN, "", "registered@user.com", "John", "Doe", "1234567890", "123 Main St");
        setMenuOptions(currentUser.getRole());
        fillDrawerHeader(currentUser);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(item -> {
            drawer.closeDrawer(GravityCompat.START);

            int itemId = item.getItemId();

            if (itemId == R.id.nav_account_settings) { showFragment(AccountSettingsFragment.newInstance(currentUser)); return true; }
            else if (itemId == R.id.nav_ride_history) { showFragment(RideHistoryFragment.newInstance(currentUser)); return true; }

            return true;
        });

        btnMenu = findViewById(R.id.btn_menu);

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

    private void showFragment(Fragment f) {
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
        map.setVisibility(View.GONE);
        if (btnMenu != null) btnMenu.setVisibility(View.GONE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, f)
                .addToBackStack(null)
                .commit();
    }


}
