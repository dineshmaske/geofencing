package com.patterns7.geofencing;

import android.app.PendingIntent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import com.patterns7.database.DatabaseHandler;
import com.patterns7.database.SimpleGeofence;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.patterns7.services.GeofenceTransitionsIntentService;

import java.util.ArrayList;
import java.util.List;


public class AddGeofencingActivity extends Activity implements
        ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private EditText nameEditText;
    private EditText addressEditText;
    private EditText radiosEditText;
    private ImageView deleteGeofence;

    private static final long SECONDS_PER_HOUR = 60;
    private static final long MILLISECONDS_PER_SECOND = 1000;
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    private static final long GEOFENCE_EXPIRATION_TIME = GEOFENCE_EXPIRATION_IN_HOURS * SECONDS_PER_HOUR * MILLISECONDS_PER_SECOND;


    protected static final String TAG = "creating-and-monitoring-geofences";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;
    protected List<String> removeGeofenceList;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    private Intent intent;
    private String geofenceId = "";
    private String status = "CREATE";
    SimpleGeofence geofence;

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
        deleteGeofence = (ImageView) findViewById(R.id.deleteGeofence);



        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();
        removeGeofenceList = new ArrayList<>();
        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

        intent = getIntent();
        savedInstanceState = intent.getExtras();
        status = savedInstanceState.getString("status", "CREATE");
        geofenceId = savedInstanceState.getString("geofenceId");
        if(status.equals("UPDATE")){
            if(geofenceId != null && !geofenceId.isEmpty()){
                DatabaseHandler provider = new DatabaseHandler(this);
                geofence = provider.getGeofence(geofenceId);
                if(geofence != null){
                    latitudeEditText.setText(String.valueOf(geofence.getLatitude()));
                    longitudeEditText.setText(String.valueOf(geofence.getLongitude()));
                    nameEditText.setText(geofence.getName());
                    addressEditText.setText(geofence.getAddress());
                    radiosEditText.setText(String.valueOf(geofence.getRadius()));

                    deleteGeofence.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }


    public void saveGeofence(View view){

            Double latitude = null;
            Double longitude = null;
            float radios = 100;
            String name = nameEditText.getText().toString();
            String address = addressEditText.getText().toString();

            try {
                latitude = Double.parseDouble(latitudeEditText.getText().toString());
            } catch (Exception e) {
                Log.d("Patterns", "Exception : " + e.toString());
            }

            try {
                longitude = Double.parseDouble(longitudeEditText.getText().toString());
            } catch (Exception e) {
                Log.d("Patterns", "Exception : " + e.toString());
            }

            try {
                radios = Float.parseFloat(radiosEditText.getText().toString());
            } catch (Exception e) {
                Log.d("Patterns", "Exception : " + e.toString());
            }

            if (latitude != null && longitude != null && !name.isEmpty()) {

                if(status.equals("UPDATE")){

                    if(geofence != null){

                        geofence.setLatitude(latitude);
                        geofence.setLongitude(longitude);
                        geofence.setName(name);
                        geofence.setAddress(address);
                        geofence.setRadius(radios);
                        geofence.setExpirationDuration(GEOFENCE_EXPIRATION_TIME);
                        geofence.setTransitionType(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

                        updateGeofence(geofence);
                    }

                } else {
                    SimpleGeofence simpleGeofence = new SimpleGeofence();
                    simpleGeofence.setGeofenceId("Patterns7"+System.currentTimeMillis());
                    simpleGeofence.setLatitude(latitude);
                    simpleGeofence.setLongitude(longitude);
                    simpleGeofence.setName(name);
                    simpleGeofence.setAddress(address);
                    simpleGeofence.setRadius(radios);
                    simpleGeofence.setExpirationDuration(GEOFENCE_EXPIRATION_TIME);
                    simpleGeofence.setTransitionType(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

                    createGeofences(simpleGeofence);
                }

                Toast.makeText(this, "Geofence updated in table", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                if (latitude == null) {
                    latitudeEditText.setError("Enter valid latitude");
                } else {
                    latitudeEditText.setError(null);
                }

                if (longitude == null) {
                    longitudeEditText.setError("Enter valid longitude");
                } else {
                    longitudeEditText.setError(null);
                }

                if (name.isEmpty()) {
                    nameEditText.setError("Please enter name");
                } else {
                    nameEditText.setError(null);
                }
            }
    }

    public void back(View view){
        finish();
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     *
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            Toast.makeText(
                    this,
                    "Operation successfully completed",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }


    public void updateGeofence(SimpleGeofence simpleGeofence) {

        DatabaseHandler db = new DatabaseHandler(this);
        db.updateGeofence(simpleGeofence);

        removeGeofencesById(simpleGeofence.getGeofenceId());

        mGeofenceList = new ArrayList<>();
        mGeofenceList.add(simpleGeofence.toGeofence());

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * In this sample, the geofences are predetermined and are hard-coded here. A real app might
     * dynamically create geofences based on the user's location.
     */
    public void createGeofences(SimpleGeofence simpleGeofence) {

        DatabaseHandler db = new DatabaseHandler(this);
        db.setGeofence(simpleGeofence);
        mGeofenceList.add(simpleGeofence.toGeofence());

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }


    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesById(String id) {

        removeGeofenceList = new ArrayList<>();
        removeGeofenceList.add(id);
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    removeGeofenceList
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    public void deleteGeofence(View view){
        DatabaseHandler db = new DatabaseHandler(this);
        db.clearGeofence(geofenceId);

        removeGeofencesById(geofenceId);

        Toast.makeText(this, "Geofence remove successfully", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
