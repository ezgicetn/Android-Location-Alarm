package com.example.air.locationalarm;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST =9001;
    ListView listView;
    List<Reminder> reminderList;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        databaseHelper = new DatabaseHelper(this);
        reminderList = databaseHelper.getReminders();

        ReminderListAdapter listAdapter = new ReminderListAdapter(reminderList, this);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this,MapViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("ID", position+1);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        if(CheckServices()){
            init();
        }
    }
    private void init(){
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MapViewActivity.class);
                startActivity(intent);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    public boolean CheckServices(){
        Log.d(TAG, "CheckServices: Checking google services");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(available== ConnectionResult.SUCCESS){
            //Google Play Services are working
            Log.d(TAG, "CheckServices: Google Play Services working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //There is an error, it can be fixed
            Log.d(TAG, "CheckServices: There is an error but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"You can't make map requests",Toast.LENGTH_SHORT).show();
        }
        return false;

    }
}
