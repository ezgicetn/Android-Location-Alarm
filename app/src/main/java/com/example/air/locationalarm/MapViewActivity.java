package com.example.air.locationalarm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback{
    private static final String TAG = "MapViewActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
    private static final LatLngBounds AUSTRALIA = new LatLngBounds(
            new LatLng(SYDNEY.latitude, SYDNEY.longitude), new LatLng(BRISBANE.latitude, BRISBANE.longitude));
    private Marker mPerth;
    private Marker mSydney;
    private Marker mBrisbane;
    private GoogleMap gMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static Location mLastLocation;
    private boolean mPermissionsGranted = false;
    private Marker marker;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    EditText titleEdt, detail;
    Button saveBtn;
    FloatingActionButton locBtn;
    Boolean existing = false;
    Reminder r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_);
        titleEdt = findViewById(R.id.titleEditText);
        detail = findViewById(R.id.detailEditText);
        saveBtn = findViewById(R.id.saveBtn);
        locBtn = findViewById(R.id.currentLocation);
        Bundle bundle = getIntent().getExtras();
        int ID = 0;
        if (bundle != null) {
            ID = bundle.getInt("ID");
            r = databaseHelper.getSaved(ID);
            titleEdt.setText(r.getTitle());
            detail.setText(r.getDetail());
            existing = true;
        }
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (existing) {
                    r.setTitle(titleEdt.getText().toString());
                    r.setDetail(detail.getText().toString());
                    databaseHelper.editReminder(r);
                } else {
                    String title = titleEdt.getText().toString();
                    String det = detail.getText().toString();
                    Reminder reminder = new Reminder(title, det);
                    databaseHelper.addReminder(reminder);
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        getLocPermission();
        /*double lat = mLastLocation.getLatitude();
        double lng = mLastLocation.getLongitude();
        String loc = "loc: "+lat+" , "+lng+".";
        Toast.makeText(this, loc,Toast.LENGTH_LONG).show();*/


    }
    private void getLocation() {
        Log.d(TAG, "getDeviceLocation: getting device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location!!");
                            Location currentLoc = (Location) task.getResult();

                            moveCamera(new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude()), 15f,"My Location");

                        } else {
                            Log.d(TAG, "onComplete: current location not found");
                            Toast.makeText(MapViewActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }
    private void getLocPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mPermissionsGranted = true;
                initMap();

            } else {
                ActivityCompat.requestPermissions(this, permissions, 123);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mPermissionsGranted = false;
                            return;
                        }
                    }
                    mPermissionsGranted = true;
                    //if permissions granted initialize map
                    initMap();

                }
            }
        }
    }
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapViewActivity.this);
    }
    private void moveCamera(LatLng latLng, float zoom,String title) {
        Log.d(TAG, "MoveCamera: moving camera to" + latLng);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //create and drop marker on map
        if(title!="My Location"){
            if(marker != null)
                marker.remove();
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            marker = gMap.addMarker(options);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
       /* gMap = googleMap;
        // Add some markers to the map, and add a data object to each marker.
        mPerth = gMap.addMarker(new MarkerOptions()
                .position(PERTH)
                .title("Perth"));
        mPerth.setTag(0);

        mSydney = gMap.addMarker(new MarkerOptions()
                .position(SYDNEY)
                .title("Sydney"));
        mSydney.setTag(0);

        mBrisbane = gMap.addMarker(new MarkerOptions()
                .position(BRISBANE)
                .title("Brisbane"));
        mBrisbane.setTag(0);
        //gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AUSTRALIA.getCenter(),1));*/
            Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
            gMap = googleMap;

            if (mPermissionsGranted) {
                getLocation();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                gMap.setMyLocationEnabled(true); //blue dot on map
               // gMap.getUiSettings().setMyLocationButtonEnabled(false); //Google's find my location button
               // gMap.getUiSettings().setMapToolbarEnabled(false); //Set default directions button false

                //init();

        }

    }


}
