package org.youker.capstone.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.youker.capstone.R;
import org.youker.capstone.activities.SolveRiddleActivity;
import org.youker.capstone.models.Riddle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by Mitch Youker on 11/16/2017.
 */

public class GoogleMapFragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;

    private MapView mapView;

    private Location lastLocation;
    private LocationRequest locationRequest;

    private HashMap<String, Riddle> riddles;
    private View solve;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);

        mapView = rootView.findViewById(R.id.map);
        solve = getActivity().findViewById(R.id.solve);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        riddles = new HashMap<>();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;


                /**
                 * Code adapted and used from https://stackoverflow.com/questions/45794926/use-of-googleapiclient-hanging-app
                 */
                if (Build.VERSION.SDK_INT >= 23) { // Greater than or equal to SDK 23
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //location permission was granted
                        buildGoogleApiClient();
                    } else {
                        //location permission was not granted (fine and or coarse)
                        checkLocationPermission();
                    }
                } else {
                    // Location check is automatically granted under 23
                    buildGoogleApiClient();
                }
                /** End adapted code **/


                /** Google maps settings **/
                googleMap.setPadding(10,10,10,125 ); //Moves the Google logo; Better appearance when we a user clicks on a marker

                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                CameraUpdateFactory.zoomTo(18);
                googleMap.setMinZoomPreference(18);
                googleMap.setMaxZoomPreference(19);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        marker.showInfoWindow(); // Shows the riddle in the title of the marker

                        solve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity().getApplicationContext(), SolveRiddleActivity.class);
                                final Riddle riddle = riddles.get(marker.getTag());

                                marker.hideInfoWindow();
                                solve.setVisibility(View.INVISIBLE);

                                intent.putExtra("riddle_id", riddle.getId()  + "");
                                intent.putExtra("riddle_text", riddle.getQuestion() + "");
                                intent.putExtra("riddle_answer", riddle.getAnswer() + "");
                                startActivity(intent);
                            }
                        });

                        Location location = new Location("");
                        location.setLatitude(marker.getPosition().latitude);
                        location.setLongitude(marker.getPosition().longitude);

                        float distanceBetween = lastLocation.distanceTo(location);
                        if (distanceBetween < 30) { // less than 30 meters (i.e. a room)
                            solve.setVisibility(View.VISIBLE);
                        } else {
                            solve.setVisibility(View.INVISIBLE);
                        }

                        return true; // Use this method as marker click event; Doesn't call any native code ( a.k.a. move camera to marker)
                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        solve = getActivity().findViewById(R.id.solve);
                        solve.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        return rootView;
    }

    /**
     * Code adapted and used from https://stackoverflow.com/questions/45794926/use-of-googleapiclient-hanging-app
     */
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    /**
     * Code from https://stackoverflow.com/questions/44491484/ask-for-location-permission-during-run-time-android
     */
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        float zoom = googleMap.getCameraPosition().zoom;

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //Updates Markers
        new RiddlesUpdateTask().execute(location);

        lastLocation = location;
    }


    public void updateGoogleMapUI(HashMap<String, Riddle> latestRiddles) {
        //Remove old riddles
        for (Riddle riddle : riddles.values()) {
            if (!latestRiddles.containsKey(riddle.getId())) {
                riddle.destroy();
                riddles.remove(riddle);
            }
        }

        //Add new riddles
        for (Riddle newRiddle : latestRiddles.values()) {
            if (!riddles.containsKey(newRiddle.getId())) {
                newRiddle.createMarker(googleMap);
                riddles.put(newRiddle.getId(), newRiddle);
            }
        }
    }

    public Location getLastLocation() {
        return lastLocation;
    }
















    public class RiddlesUpdateTask extends AsyncTask<Location, String, String> {

        private String GET_RIDDLES_URL = "http://67.249.252.255/getRiddles.php";

        @Override
        protected void onPostExecute(String content) {
            HashMap<String, Riddle> riddles = parseJSON(content);
            updateGoogleMapUI(riddles);
        }

        @Override
        protected String doInBackground(Location... locations) {
            String content = getJSONResponse(locations[0]);
            return content;
        }

        @Override
        protected void onPreExecute() {}

        private String getJSONResponse(Location location) {
            try {
                URL url = new URL(GET_RIDDLES_URL + "?lat=" + URLEncoder.encode(location.getLatitude() + "", "UTF-8") + "&long=" + URLEncoder.encode(location.getLongitude() + "", "UTF-8"));
                Log.d("GDHD", url.toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

                // Condenses all the input lines into one string
                String line, content = "";
                while ((line = reader.readLine()) != null) {
                    content += line;
                }

                return content;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        public HashMap<String, Riddle> parseJSON(String content) {
            HashMap<String, Riddle> riddles = new HashMap<>();

            try {
                JSONObject jsonContent = new JSONObject(content);
                JSONArray jsonRiddles = jsonContent.getJSONArray("riddles");
                for (int i = 0; i < jsonRiddles.length(); i++) {
                    Riddle riddle = new Riddle(jsonRiddles.getJSONObject(i));
                    riddles.put(riddle.getId(), riddle);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return riddles;
        }
    }

}
