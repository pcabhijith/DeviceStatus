package com.example.hp.devicestatus;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.navigation);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        final HomeFragment homeFragment = new HomeFragment();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content_frame, homeFragment); // newInstance() is a static factory method.
        transaction.commit();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        if (id == R.id.nav_battery) {
                           /* BatteryFragment batteryFragment = new BatteryFragment();
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.content_frame, batteryFragment); // newInstance() is a static factory method.
                            transaction.addToBackStack(null);
                            transaction.commit();
                            mDrawerLayout.closeDrawers();*/

                        } else if (id == R.id.nav_deviceinfo) {
                          /*  DeviceFragment deviceFragment = new DeviceFragment();
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.content_frame, deviceFragment); // newInstance() is a static factory method.
                            transaction.addToBackStack(null);
                            transaction.commit();
                            mDrawerLayout.closeDrawers();*/

                        } else if (id == R.id.nav_network) {
                            /*NetworkFragment networkFragment = new NetworkFragment();
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.content_frame, networkFragment); // newInstance() is a static factory method.
                            transaction.addToBackStack(null);
                            transaction.commit();
                            mDrawerLayout.closeDrawers();*/

                        } else if (id == R.id.nav_storage) {
                            /*StorageFragment storageFragment = new StorageFragment();
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.content_frame, storageFragment); // newInstance() is a static factory method.
                            transaction.addToBackStack(null);
                            transaction.commit();
                            mDrawerLayout.closeDrawers();*/

                        } else if (id == R.id.nav_weather) {
                            /*WeatherFragment weatherFragment = new WeatherFragment();
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.content_frame, weatherFragment); // newInstance() is a static factory method.
                            transaction.addToBackStack(null);
                            transaction.commit();
                            mDrawerLayout.closeDrawers();*/

                        } else if (id == R.id.nav_home) {
                            HomeFragment homeFragment = new HomeFragment();
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.content_frame, homeFragment); // newInstance() is a static factory method.
                            transaction.addToBackStack(null);
                            transaction.commit();
                            mDrawerLayout.closeDrawers();

                        }
                        return true;
                    }
                });

        //run time permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawers();
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


}
