package com.example.arturosf.minubdemo.users;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.arturosf.minubdemo.users.entities.Coordenadas;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private HashMap<String, Marker> taxis = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        startListenerDrivers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startListenerDrivers() {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Coordenadas coordenadas = postSnapshot.getValue(Coordenadas.class);
                    if (!taxis.containsKey(postSnapshot.getKey())) {
                        Marker taxi = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(coordenadas.getLatitude(), coordenadas.getLongitude()))
                                .title(postSnapshot.getKey()));
                        taxis.put(postSnapshot.getKey(), taxi);
                    } else {
                        taxis.get(postSnapshot.getKey()).setPosition(new LatLng(coordenadas.getLatitude(), coordenadas.getLongitude()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("asdasd", "Failed to read value.", error.toException());
            }
        });
    }
}
