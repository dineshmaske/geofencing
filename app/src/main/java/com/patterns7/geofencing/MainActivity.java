package com.patterns7.geofencing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import com.patterns7.adaptor.GeofencingAdaptor;
import com.patterns7.database.DatabaseHandler;
import com.patterns7.database.SimpleGeofence;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    ListView geofenceListView;
    TextView errorMessageView;

    GeofencingAdaptor adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        geofenceListView = (ListView)findViewById(R.id.geofence_list_view);
        errorMessageView = (TextView)findViewById(R.id.error_message);

        DatabaseHandler db = new DatabaseHandler(this);
        ArrayList<SimpleGeofence> geofences = db.getGeofences();
        if(geofences.size() > 0) {
            errorMessageView.setVisibility(View.GONE);

            // set up the drawer's list view with items and click listener
            adapter = new GeofencingAdaptor(this, R.layout.geofence_list_item, geofences);
            geofenceListView.setAdapter(adapter);

        } else {
            errorMessageView.setVisibility(View.VISIBLE);
        }
    }

    public void openRegisterPage(View view){
        Intent intent = new Intent(this, AddGeofencingActivity.class);
        startActivity(intent);
    }

}
