package com.example.air.locationalarm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.TextView;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG = "MapViewActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
   /* private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
    private static final LatLngBounds AUSTRALIA = new LatLngBounds(
            new LatLng(SYDNEY.latitude, SYDNEY.longitude), new LatLng(BRISBANE.latitude, BRISBANE.longitude));
    private Marker mPerth;
    private Marker mSydney;
    private Marker mBrisbane;*/
    private GoogleMap gMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mPermissionsGranted = false;
    private Marker marker;
    private double lat, lng, plat, plng;
    private Location currentLoc, prevLoc;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    EditText titleEdt, detail;
    Button saveBtn;
    TextView llText;
    FloatingActionButton locBtn;
    Boolean existing = false;
    Boolean locRequested = false;
    Reminder r;
    String city, country, postalCode,tv,tmptitle;
    Geocoder geocoder;
    List<Address> addresses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_);
        titleEdt = findViewById(R.id.titleEditText);
        detail = findViewById(R.id.detailEditText);
        llText = findViewById(R.id.latlong);
        saveBtn = findViewById(R.id.saveBtn);
        locBtn = findViewById(R.id.currentLocation);

        geocoder = new Geocoder(this, Locale.getDefault());

        //addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        //String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        Bundle bundle = getIntent().getExtras();
        int ID = 0;
        if (bundle != null) {
            //checks if an existing reminder is clicked
            ID = bundle.getInt("ID");
            r = databaseHelper.getSaved(ID);
            titleEdt.setText(r.getTitle());
            detail.setText(r.getDetail());
            plat = r.getLat();
            plng = r.getLng();
            tmptitle = r.getTitle();
            try {
                addresses = geocoder.getFromLocation(plat, plng, 1);
                city = addresses.get(0).getLocality();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            tv = city+", "+country+", "+postalCode;
            llText.setText(tv);

            Log.d("ploc", "ploc: "+plat+ " , " +plng+" .");
            existing = true;
        }
        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goes to current location
                locRequested = true;
                lat = currentLoc.getLatitude();
                lng = currentLoc.getLongitude();
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                    city = addresses.get(0).getLocality();
                    country = addresses.get(0).getCountryName();
                    postalCode = addresses.get(0).getPostalCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tv = "current: "+city+", "+country+", "+postalCode;
                llText.setText(tv);
                Log.d("loc", "loc: "+lat+ " , " +lng+" .");
                getLocation();
            }
        });
       // Log.d("loc", "loc "+ lat+ ", "+lng+".");
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (existing) {
                    //updates existing reminder
                    r.setTitle(titleEdt.getText().toString());
                    r.setDetail(detail.getText().toString());
                    r.setLat(lat);
                    r.setLng(lng);
                    databaseHelper.editReminder(r);
                } else {
                    //adds new reminder
                    String title = titleEdt.getText().toString();
                    String det = detail.getText().toString();
                    Reminder reminder = new Reminder(title, det, lat, lng);
                    databaseHelper.addReminder(reminder);
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        getLocPermission();
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
                            currentLoc = (Location) task.getResult();
                            //goes to current location
                            if(locRequested){
                                moveCamera(new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude()), 15f, "My Location");

                            }
                            //clicked on existing reminder, shows its place
                            else {
                                moveCamera(new LatLng(plat,plng), 15f, tmptitle);
                            }
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
