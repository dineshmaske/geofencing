package com.patterns7.geofencing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.patterns7.database.DatabaseHandler;
import com.patterns7.database.SimpleGeofence;

public class GeofencingDetailsActivity extends Activity {

    private TextView geofencingNameView;
    private TextView geofencingAddressView;
    private TextView geofencingLatitudeView;
    private TextView geofencingLongitudeView;
    private TextView geofencingRediousView;

    private Intent intent;

    SimpleGeofence geofence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_geofencing_details);

        geofencingNameView = (TextView) findViewById(R.id.geofencingNameView);
        geofencingAddressView = (TextView) findViewById(R.id.geofencingAddressView);
        geofencingLatitudeView = (TextView) findViewById(R.id.geofencingLatitudeView);
        geofencingLongitudeView = (TextView) findViewById(R.id.geofencingLongitudeView);
        geofencingRediousView = (TextView) findViewById(R.id.geofencingRediousView);

        intent = getIntent();
        savedInstanceState = intent.getExtras();
        String geofenceId = savedInstanceState.getString("geofenceId");

        if(geofenceId != null && !geofenceId.isEmpty()){
            DatabaseHandler provider = new DatabaseHandler(this);
            geofence = provider.getGeofence(geofenceId);
            if(geofence != null){
                geofencingNameView.setText("Name : "+geofence.getName());
                geofencingAddressView.setText("Address : "+geofence.getAddress());
                geofencingLatitudeView.setText("Latitude : "+geofence.getLatitude());
                geofencingLongitudeView.setText("Longitude : "+geofence.getLongitude());
                geofencingRediousView.setText("Radios : "+geofence.getRadius()+" Meters");
            }
        }

    }

    public void updateGeofencingDetails(View view){
        if(geofence != null) {
            Intent intent = new Intent(this, AddGeofencingActivity.class);
            intent.putExtra("status", "UPDATE");
            intent.putExtra("geofenceId", geofence.getGeofenceId());
            startActivity(intent);
        }
    }


    public void back(View view){
        finish();
    }
}
