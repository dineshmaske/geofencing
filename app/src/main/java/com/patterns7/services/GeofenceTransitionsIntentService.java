package com.patterns7.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.patterns7.database.DatabaseHandler;
import com.patterns7.database.SimpleGeofence;
import com.patterns7.geofencing.GeofenceErrorMessages;
import com.patterns7.geofencing.MainActivity;
import com.patterns7.geofencing.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener for geofence transition changes.
 *
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "geofence-transitions-service";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.d(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            // Send notification and log the transition details.
            sendNotification(triggeringGeofences, geofenceTransition);
        } else {
            // Log the error.
            Log.d(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }


    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(List<Geofence> triggeringGeofences, int geofenceTransition) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        for (Geofence geofence : triggeringGeofences) {

            DatabaseHandler provider = new DatabaseHandler(this);
            SimpleGeofence item = provider.getGeofence(geofence.getRequestId());
            String address = "";
            if(item != null){
                Log.d("Patterns7tech",item.toString());
                address = item.getAddress();
            }

            // Create an explicit content Intent that starts the main Activity
            Intent notificationIntent = new Intent(this, MainActivity.class);

            // Construct a task stack
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

            // Adds the main Activity to the task stack as the parent
            stackBuilder.addParentStack(MainActivity.class);

            // Push the content Intent onto the stack
            stackBuilder.addNextIntent(notificationIntent);

            // Get a PendingIntent containing the entire back stack
            PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            // Get a notification builder that's compatible with platform versions >= 4
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            // Set the notification contents
            builder.setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle(geofenceTransitionString)
                    .setContentText("You "+geofenceTransitionString+" "+address).setContentIntent(notificationPendingIntent);

            builder.setAutoCancel(true);
            builder.setLights(Color.BLUE, 500, 500);
            long[] pattern = {500,500,500,500,500,500,500,500,500};
            builder.setVibrate(pattern);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);

            NotificationCompat.BigTextStyle inboxStyle =
                    new NotificationCompat.BigTextStyle();

            inboxStyle.bigText("You've "+geofenceTransitionString+" "+address+".");

            builder.setStyle(inboxStyle);

            // Get an instance of the Notification manager
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Issue the notification
            mNotificationManager.notify(0, builder.build());

        }

    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}