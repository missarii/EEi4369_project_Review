package com.s23001792.thiriposa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.HashMap;
import java.util.Map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Spinner districtSpinner;
    private Button btnZoomIn, btnZoomOut, btnNormal, btnSatellite, btnHybrid, btnTerrain, btnMyLocation;

    private Map<String, LatLng> districtMOHMap;
    private Marker currentMarker, userMarker;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize Views
        districtSpinner = findViewById(R.id.districtSpinner);
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        btnNormal = findViewById(R.id.btnNormal);
        btnSatellite = findViewById(R.id.btnSatellite);
        btnHybrid = findViewById(R.id.btnHybrid);
        btnTerrain = findViewById(R.id.btnTerrain);
        btnMyLocation = findViewById(R.id.btnMyLocation);

        // Initialize Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupDistrictMOHMap();
        setupSpinner();

        // Button listeners
        btnZoomIn.setOnClickListener(v -> {
            if (mMap != null) mMap.animateCamera(CameraUpdateFactory.zoomIn());
        });

        btnZoomOut.setOnClickListener(v -> {
            if (mMap != null) mMap.animateCamera(CameraUpdateFactory.zoomOut());
        });

        btnNormal.setOnClickListener(v -> setMapType(GoogleMap.MAP_TYPE_NORMAL));
        btnSatellite.setOnClickListener(v -> setMapType(GoogleMap.MAP_TYPE_SATELLITE));
        btnHybrid.setOnClickListener(v -> setMapType(GoogleMap.MAP_TYPE_HYBRID));
        btnTerrain.setOnClickListener(v -> setMapType(GoogleMap.MAP_TYPE_TERRAIN));

        btnMyLocation.setOnClickListener(v -> showUserLocation());
    }

    private void setMapType(int type) {
        if (mMap != null) {
            mMap.setMapType(type);
            Toast.makeText(this, "Map type changed", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDistrictMOHMap() {
        districtMOHMap = new HashMap<>();
        districtMOHMap.put("Colombo", new LatLng(6.9271, 79.8612));
        districtMOHMap.put("Gampaha", new LatLng(7.0844, 79.9401));
        districtMOHMap.put("Kalutara", new LatLng(6.5859, 79.9606));
        districtMOHMap.put("Kandy", new LatLng(7.2906, 80.6337));
        districtMOHMap.put("Matale", new LatLng(7.4710, 80.6230));
        districtMOHMap.put("Nuwara Eliya", new LatLng(6.9497, 80.7891));
        districtMOHMap.put("Galle", new LatLng(6.0328, 80.2170));
        districtMOHMap.put("Matara", new LatLng(5.9480, 80.5356));
        districtMOHMap.put("Hambantota", new LatLng(6.1243, 81.1193));
        districtMOHMap.put("Jaffna", new LatLng(9.6615, 80.0255));
        districtMOHMap.put("Kilinochchi", new LatLng(9.3989, 80.3787));
        districtMOHMap.put("Mannar", new LatLng(8.9833, 79.9000));
        districtMOHMap.put("Vavuniya", new LatLng(8.7546, 80.5007));
        districtMOHMap.put("Mullaitivu", new LatLng(9.3000, 81.2000));
        districtMOHMap.put("Batticaloa", new LatLng(7.7167, 81.7000));
        districtMOHMap.put("Ampara", new LatLng(7.2949, 81.6700));
        districtMOHMap.put("Trincomalee", new LatLng(8.5667, 81.2333));
        districtMOHMap.put("Kurunegala", new LatLng(7.4869, 80.3644));
        districtMOHMap.put("Puttalam", new LatLng(8.0333, 79.8333));
        districtMOHMap.put("Anuradhapura", new LatLng(8.3114, 80.4037));
        districtMOHMap.put("Polonnaruwa", new LatLng(7.9333, 81.0000));
        districtMOHMap.put("Badulla", new LatLng(6.9931, 81.0550));
        districtMOHMap.put("Moneragala", new LatLng(6.8706, 81.3356));
        districtMOHMap.put("Ratnapura", new LatLng(6.6820, 80.3990));
        districtMOHMap.put("Kegalle", new LatLng(7.2559, 80.3426));
    }

    private void setupSpinner() {
        String[] districts = districtMOHMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, districts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapter);

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String district = districts[position];
                showMOHCenter(district);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void showMOHCenter(String district) {
        LatLng latLng = districtMOHMap.get(district);
        if (latLng == null || mMap == null) return;

        if (currentMarker != null) currentMarker.remove();

        currentMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("MOH Center - " + district)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f));

        // Calculate distance from user
        if (userMarker != null) {
            float[] results = new float[1];
            Location.distanceBetween(userMarker.getPosition().latitude,
                    userMarker.getPosition().longitude,
                    latLng.latitude,
                    latLng.longitude, results);
            float distanceKm = results[0] / 1000f;
            Toast.makeText(this, String.format("Distance to MOH center: %.2f km", distanceKm), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Showing MOH Center for " + district, Toast.LENGTH_SHORT).show();
        }
    }

    private void showUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null && mMap != null) {
                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                if (userMarker != null) userMarker.remove();

                userMarker = mMap.addMarker(new MarkerOptions()
                        .position(userLatLng)
                        .title("You are here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f));

                Toast.makeText(this, "Current location displayed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unable to get location. Try emulator location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showUserLocation();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLngBounds sriLankaBounds = new LatLngBounds(
                new LatLng(5.9, 79.0),
                new LatLng(10.1, 82.1)
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(sriLankaBounds, 50));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }
}
