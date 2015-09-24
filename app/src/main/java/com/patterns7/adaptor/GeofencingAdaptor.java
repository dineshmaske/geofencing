package com.patterns7.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.patterns7.database.SimpleGeofence;
import java.util.ArrayList;
import java.util.List;
import com.patterns7.geofencing.R;

/**
 * Created by Dinesh on 9/23/2015.
 *
 */
public class GeofencingAdaptor extends ArrayAdapter<SimpleGeofence> {

    Context context;
    ArrayList<SimpleGeofence> geofences = new ArrayList<>();

    public GeofencingAdaptor(Context context, int layoutResourceId,
                              ArrayList<SimpleGeofence> geofences) {
        super(context, layoutResourceId, geofences);

        this.geofences = geofences;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        SimpleGeofence simpleGeofence = geofences.get(position);
        TextView name;
        TextView address;
        TextView latlong;

        convertView = null;
        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.geofence_list_item, null);

            name = (TextView) convertView.findViewById(R.id.nameTextView);
            address = (TextView) convertView.findViewById(R.id.addressTextView);
            latlong = (TextView) convertView.findViewById(R.id.latLongTextView);

            name.setText(simpleGeofence.getName());
            address.setText(simpleGeofence.getAddress());
            latlong.setText(simpleGeofence.getLatitude()+" "+simpleGeofence.getLongitude());
        }

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getCount() {
        return geofences.size();
    }

    public void addItem(SimpleGeofence item) {
        //
        geofences.add(item);
        notifyDataSetChanged();
    }

    public void addItemAll(List<SimpleGeofence> item) {
        //
        geofences.addAll(item);
        notifyDataSetChanged();
    }
}
