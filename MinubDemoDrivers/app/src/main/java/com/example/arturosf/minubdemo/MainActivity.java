package com.example.arturosf.minubdemo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String android_id;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private List<String> providers;
    private Location mLatLong;
    private Marker marker;
    private boolean currentSended = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(android_id);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        providers = locationManager.getAllProviders();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng currentLocation;
        if (null != mLatLong) {
            currentLocation = new LatLng(mLatLong.getLatitude(), mLatLong.getLongitude());
        } else {
            currentLocation = new LatLng(-34, 151);
        }
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(currentLocation).title(android_id));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }

    @Override
    public void onResume() {
        super.onResume();
        startProviders();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopProviders();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatLong = location;
        if (!currentSended) {
            showCurrentLocation();
            currentSended = true;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void startProviders() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Se necesita tu permiso para acceder a tu ubicación", Toast.LENGTH_SHORT).show();
            return;
        }
        for (String provider : providers) {
            locationManager.requestLocationUpdates(provider, 5000, 0, this);
        }
    }

    private void stopProviders() {
        locationManager.removeUpdates(this);
    }

    private void showCurrentLocation() {
        myRef.child("latitude").setValue(mLatLong.getLatitude());
        myRef.child("longitude").setValue(mLatLong.getLongitude());
        if (null != mMap) {
            LatLng currentPosition = new LatLng(mLatLong.getLatitude(), mLatLong.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentPosition).title(android_id).draggable(true));
            //marker = mMap.addMarker(new MarkerOptions().position(currentPosition).title(android_id).draggable(true));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));
            mMap.setOnMapClickListener(this);
            //mMap.setOnMarkerDragListener(this);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        myRef.child("latitude").setValue(marker.getPosition().latitude);
        myRef.child("longitude").setValue(marker.getPosition().longitude);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        myRef.child("latitude").setValue(latLng.latitude);
        myRef.child("longitude").setValue(latLng.longitude);
    }
}
