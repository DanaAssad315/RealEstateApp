package com.example.project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;

public class Home_page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Home_page";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        userEmail = getIntent().getStringExtra("user_email");
        Log.d(TAG, "User email from intent: " + userEmail);

        if (userEmail != null && !userEmail.isEmpty()) {
            ReservationManager.initialize(this, userEmail);
            FavoritesManager.initialize(this, userEmail);
        } else {
            Log.e(TAG, "User email is null or empty - cannot initialize ReservationManager and FavoritesManager ");
        }

        initializeUI();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        updateUsernameDisplay();
    }
    private void initializeUI() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void updateUsernameDisplay() {

            TextView usernameTextView = navigationView.getHeaderView(0).findViewById(R.id.username);
            if (userEmail != null && !userEmail.isEmpty()) {
                String username = userEmail.contains("@") ? userEmail.split("@")[0] : userEmail;
                usernameTextView.setText("Welcome, " + username);
            } else {
                usernameTextView.setText("Welcome, User");

            }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_properties) {
            selectedFragment = new PropertiesFragment();
        } else if (id == R.id.nav_reservations) {
            selectedFragment = new ReservationsFragment();
        } else if (id == R.id.nav_favorites) {
            selectedFragment = new FavoritesFragment();
        } else if (id == R.id.nav_featured) {
            selectedFragment = new FeaturedFragment();
        } else if (id == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        } else if (id == R.id.nav_contact) {
            selectedFragment = new ContactFragment();
        } else if (id == R.id.nav_logout) {
            handleLogout();
            return true;
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleLogout() {
        ReservationManager.clearInstance();
        FavoritesManager.clearInstancee();
        Intent intent = new Intent(Home_page.this, LoginRegisterActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}