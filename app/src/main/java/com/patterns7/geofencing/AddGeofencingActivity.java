package com.patterns7.geofencing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import com.patterns7.database.DatabaseHandler;
import com.patterns7.database.SimpleGeofence;
import com.google.android.gms.location.Geofence;

public class AddGeofencingActivity extends Activity {

    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private EditText nameEditText;
    private EditText addressEditText;
    private EditText radiosEditText;

    private static final long SECONDS_PER_HOUR = 60;
    private static final long MILLISECONDS_PER_SECOND = 1000;
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_TIME = GEOFENCE_EXPIRATION_IN_HOURS * SECONDS_PER_HOUR * MILLISECONDS_PER_SECOND;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_geofencing);

        latitudeEditText = (EditText) findViewById(R.id.textLatitude);
        longitudeEditText = (EditText) findViewById(R.id.textLongitude);
        nameEditText = (EditText) findViewById(R.id.textName);
        addressEditText = (EditText) findViewById(R.id.textAddress);
        radiosEditText = (EditText) findViewById(R.id.textRedious);

    }

    public void saveGeofence(View view){

        Double latitude = null;
        Double longitude = null;
        float radios = 100;
        String name = nameEditText.getText().toString();
        String address= addressEditText.getText().toString();

        try{
            latitude = Double.parseDouble(latitudeEditText.getText().toString());
        } catch (Exception e){
            Log.d("Patterns", "Exception : " + e.toString());
        }

        try{
            longitude = Double.parseDouble(longitudeEditText.getText().toString());
        } catch (Exception e){
            Log.d("Patterns", "Exception : " + e.toString());
        }

        try{
            radios= Float.parseFloat(radiosEditText.getText().toString());
        } catch (Exception e){
            Log.d("Patterns", "Exception : " + e.toString());
        }

        if(latitude != null && longitude  != null && !name.isEmpty()){

            DatabaseHandler db = new DatabaseHandler(this);

            SimpleGeofence simpleGeofence = new SimpleGeofence();
            simpleGeofence.setLatitude(latitude);
            simpleGeofence.setLongitude(longitude);
            simpleGeofence.setName(name);
            simpleGeofence.setAddress(address);
            simpleGeofence.setRadius(radios);
            simpleGeofence.setExpirationDuration(GEOFENCE_EXPIRATION_TIME);
            simpleGeofence.setTransitionType(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

            db.setGeofence(simpleGeofence);

            Toast.makeText(this, "Geofence added successfully in table", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            if(latitude == null){
                latitudeEditText.setError("Enter valid latitude");
            } else {
                latitudeEditText.setError(null);
            }

            if(longitude  == null){
                longitudeEditText.setError("Enter valid longitude");
            } else {
               longitudeEditText.setError(null);
            }

            if(name.isEmpty()){
                nameEditText.setError("Please enter name");
            } else {
                nameEditText.setError(null);
            }
        }
    }

    public void back(View view){
        finish();
    }
}
