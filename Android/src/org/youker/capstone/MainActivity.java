package org.youker.capstone;

import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.youker.capstone.activities.CreateRiddleActivity;
import org.youker.capstone.fragments.GoogleMapFragment;

/**
 * Created by Mitch Youker on 11/16/2017.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private FragmentManager fragmentManager;
    private GoogleMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getFragmentManager();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mapFragment = new GoogleMapFragment();
        fragmentManager.beginTransaction().replace(R.id.content_frame, mapFragment).commit(); // Sets the view to the Map Fragment (Google Map)
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //navigation items click event
        if (item != null) {
            int id = item.getItemId();

            if (id == R.id.nav_map) {
                if (!mapFragment.isAdded()) {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, mapFragment).commit();
                }
            } else if (id == R.id.nav_add_location) {
                Location lastLocation = mapFragment.getLastLocation();
                Log.d("test", lastLocation.getLatitude() + "");

                Intent intent = new Intent(getApplicationContext(), CreateRiddleActivity.class);
                intent.putExtra("latitude", lastLocation.getLatitude()  + "");
                intent.putExtra("longitude", lastLocation.getLongitude() + "");
                startActivity(intent);
            } else if (id == R.id.nav_leaderboard) {
                // TO-DO
            } else if (id == R.id.nav_logout) {
                // TO-DO
            } else if (id == R.id.nav_info) {

            }

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

}
