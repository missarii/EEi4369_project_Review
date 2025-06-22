package com.s23001792.thiriposa;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.*;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ClusterManager<MyItem> clusterManager;

    private EditText searchEditText;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        searchButton.setOnClickListener(v -> {
            String loc = searchEditText.getText().toString().trim();
            if (!loc.isEmpty()) searchLocation(loc);
            else Toast.makeText(this, "Enter a location name", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setupClusterer();
        loadAllMOH();  // load all villages

        // Center on Sri Lanka
        LatLngBounds sriLanka = new LatLngBounds(
                new LatLng(5.9, 79.0),   // SW bound
                new LatLng(10.1, 82.1)   // NE bound
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(sriLanka, 50));
    }

    private void setupClusterer() {
        clusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);
    }

    private void loadAllMOH() {
        List<LatLng> mohLatLngs = getMOHCoordinatesAllVillages();

        for (LatLng latLng : mohLatLngs) {
            MyItem offsetItem = new MyItem(latLng.latitude, latLng.longitude);
            clusterManager.addItem(offsetItem);
        }
        clusterManager.cluster();
    }

    private List<LatLng> getMOHCoordinatesAllVillages() {
        List<LatLng> list = new ArrayList<>();
        // TODO: replace with real MOH data (village-level lat/lng)
        list.add(new LatLng(6.9271, 79.8612)); // example Colombo
        list.add(new LatLng(7.2906, 80.6337)); // example Kandy
        // plus thousands more from file/database...
        return list;
    }

    private void searchLocation(String locationName) {
        Geocoder geocoder = new Geocoder(this, Locale.forLanguageTag("en-LK"));
        try {
            List<Address> addrs = geocoder.getFromLocationName(locationName + ", Sri Lanka", 1);
            if (addrs != null && !addrs.isEmpty()) {
                Address addr = addrs.get(0);
                LatLng ll = new LatLng(addr.getLatitude(), addr.getLongitude());
                mMap.addMarker(new MarkerOptions().position(ll).title(locationName));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 12f));
            } else {
                Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Search error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
