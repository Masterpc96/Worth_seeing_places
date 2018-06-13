package com.example.michaelurban.viewpager.Activities.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michaelurban.viewpager.Activities.OwnClasses.Place;
import com.example.michaelurban.viewpager.Activities.Repo.PlacesServices;
import com.example.michaelurban.viewpager.Activities.adapters.InfoWindow;
import com.example.michaelurban.viewpager.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements InternetConnectivityListener, OnMapReadyCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.noNet)
    TextView textView;

    @BindView(R.id.noNetLayout)
    ConstraintLayout noNetLayout;

    @BindView(R.id.mapView)
    MapView mapView;

    // setting variable for map view
    UiSettings settings;

    // Google Map object
    GoogleMap map;


    boolean connected = true;

    Retrofit retrofit;

    // final value for checking permission
    static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    // manager for gps
    LocationManager manager;


    // My service for GET to api
    PlacesServices services;


    // checker for internet connection
    InternetAvailabilityChecker checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initiate Internet checker
        InternetAvailabilityChecker.init(this);

        // get instance
        checker = InternetAvailabilityChecker.getInstance();

        // set listener for object
        checker.addInternetConnectivityListener(this);


        // init location manager
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // set binding class
        ButterKnife.bind(this);

        // create map view
        mapView.onCreate(savedInstanceState);

        // init map
        mapView.getMapAsync(this);

        // setting up the toolbar as an action bar
        setSupportActionBar(toolbar);
    }

    /**
     * onInternetConnectionActivityChanged is method that must be override
     * it notify us when the phone losses internet connection
     *
     * @param isConnected is boolean variable that indicate currently state
     */
    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (isConnected) {
            // internet is provided so disable noNetLayout and enable map view
            noNetLayout.setVisibility(View.GONE);
            map.getUiSettings().setAllGesturesEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            addMarkers();
        } else {
            // insternet is no provided so show noNetLayout and disable map view
            noNetLayout.setVisibility(View.VISIBLE);
            if(map!=null){
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.getUiSettings().setAllGesturesEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(false);
                textView.setText(R.string.noNet);
            }

        }
        connected = isConnected;
    }


    /**
     * This method initial google maps
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        // setting up google mpa
        settings = googleMap.getUiSettings();
        // set zoom button
        settings.setZoomControlsEnabled(true);
        // set compass button
        settings.setCompassEnabled(true);
        // set navigate button
        settings.setMapToolbarEnabled(true);


        // set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // set listener for on my location button
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    Toast.makeText(getApplicationContext(), R.string.gps, Toast.LENGTH_SHORT).show();
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        // check if user allowed to use gps service
        if (checkLocationPermission()) {
            googleMap.setMyLocationEnabled(true);
        }

        // add markers to map
     //   addMarkers();

        // set adapter for Info view
        googleMap.setInfoWindowAdapter(new InfoWindow(getApplicationContext()));

        map = googleMap;
    }

    private void addMarkers(){
        // create retrofit from builder
        retrofit = new Retrofit.Builder().
                baseUrl("http://michal.xvshosting.pl/").addConverterFactory(GsonConverterFactory.create()).build();

        // get service for
        services = retrofit.create(PlacesServices.class);

        // get all places from from service
        Call<ArrayList<Place>> places = services.getPlaces();

        // set callback on call
        places.enqueue(new Callback<ArrayList<Place>>() {
            @Override
            public void onResponse(Call<ArrayList<Place>> call, Response<ArrayList<Place>> response) {
                if(response.body() != null){
                    for(Place place : response.body()){
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(place.getLatitude(), place.getLongitude()))
                                .title(place.getName())
                                .snippet(place.getPhotoURL()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Place>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "FAILURE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * this method check permission and show you a prompt dialog
     *
     * @return
     */
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        map.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }


    /**
     * this method add to action bar our menu
     *
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_type, menu);
        return true;
    }


    /**
     * this method check which item was clicked
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;

            case R.id.satellite:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            case R.id.terrain:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        checker.onNetworkChange(connected);
        mapView.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
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

}
