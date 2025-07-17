package com.example.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class AdminActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;

            if (id == R.id.nav_statistics) {
                selectedFragment = new StatisticsFragment();
            }
            else if (id == R.id.nav_delete_customers) {
                selectedFragment = new DeleteUserFragment();
            }
            else if (id == R.id.nav_add_admin) {
                selectedFragment = new AddAdminFragment();
            }
            else if (id == R.id.nav_offers) {
                selectedFragment = new CreateOfferFragment();
            }

            else if (id == R.id.nav_reservations) {
              selectedFragment = new AllReservationsFragment();
             }
            else if (id == R.id.nav_logout) {
                logout();
                return true;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.admin_fragment_container, selectedFragment)
                        .commit();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_fragment_container, new StatisticsFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_statistics);
        }
    }

    private void logout() {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        sharedPrefManager.clear();

        Intent intent = new Intent(this, LoginRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}