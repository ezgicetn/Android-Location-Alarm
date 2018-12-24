package com.example.air.locationalarm;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback{
    private MapView mapView;
    private GoogleMap gmap;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    EditText titleEdt, detail;
    Button saveBtn;
    Boolean existing = false;
    Reminder r;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view_);
        titleEdt = findViewById(R.id.titleEditText);
        detail = findViewById(R.id.detailEditText);
        saveBtn = findViewById(R.id.saveBtn);
        Bundle bundle = getIntent().getExtras();
            int ID = 0;
            if(bundle != null){
                ID = bundle.getInt("ID");
                r = databaseHelper.getSaved(ID);
                titleEdt.setText(r.getTitle());
                detail.setText(r.getDetail());
                existing = true;
            }



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(existing){
                    r.setTitle(titleEdt.getText().toString());
                    r.setDetail(detail.getText().toString());
                    databaseHelper.editReminder(r);
                }
                else {
                    String title = titleEdt.getText().toString();
                    String det = detail.getText().toString();
                    Reminder reminder = new Reminder(title, det);
                    databaseHelper.addReminder(reminder);
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

            }
        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        LatLng ny = new LatLng(40.7143528, -74.0059731);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }
}
