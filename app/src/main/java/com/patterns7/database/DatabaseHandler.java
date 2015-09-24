package com.patterns7.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper{

	
	 // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;
 
    // Database Name
    private static final String DATABASE_NAME = "patterns7";

    public static final String TABLE_GEOFENCE = "geofence";
	
	//geofence table Columns
    public static final String _ID = "_id";
    public static final String GEOFENCE_ID = "geofence_id";
	public static final String LOCATION_NAME = "location"; 
	public static final String RADIUS = "radius"; 
	public static final String EXPRIATION_DURATION = "expirationDuration"; 
	public static final String TRANSITION_TYPE = "transitionType"; 
	public static final String ADDRESS = "address";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    Context context;


	public DatabaseHandler(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
        
        
        String CREATE_GEOFENCE_TABLE = 
    			"CREATE TABLE IF NOT EXISTS " + TABLE_GEOFENCE + " (" + 
    				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GEOFENCE_ID + " TEXT, " +
    				LATITUDE + " INTEGER," + 
    				LONGITUDE + " INTEGER," + 
    				LOCATION_NAME + " TEXT," +
                    ADDRESS + " TEXT," +
    				RADIUS + " INTEGER," + 
    				EXPRIATION_DURATION + " INTEGER," + 
    				TRANSITION_TYPE + " INTEGER" + 
    			");";


        
        db.execSQL(CREATE_GEOFENCE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GEOFENCE);

        clearPreferences();
 
        // Create tables again
        onCreate(db);
	}

    private void clearPreferences() {

        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("RTAPrefs", 0); // 0
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    public SimpleGeofence getGeofence(String id) {
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	 
        Cursor cursor = db.query(TABLE_GEOFENCE, new String[]{_ID, GEOFENCE_ID,
                        LATITUDE, LONGITUDE, LOCATION_NAME, ADDRESS, RADIUS, EXPRIATION_DURATION, TRANSITION_TYPE}, GEOFENCE_ID + "=?",
                new String[]{id}, null, null, null, null);

        if (cursor != null && cursor.moveToNext() ){

            SimpleGeofence simpleGeofence = new SimpleGeofence();

            simpleGeofence.setId(Long.parseLong(cursor.getString(0)));
            simpleGeofence.setGeofenceId(cursor.getString(1));
        	simpleGeofence.setLatitude(Double.parseDouble(cursor.getString(2)));
        	simpleGeofence.setLongitude(Double.parseDouble(cursor.getString(3)));
        	simpleGeofence.setName(cursor.getString(4));
        	simpleGeofence.setAddress(cursor.getString(5));
        	simpleGeofence.setRadius(Float.parseFloat(cursor.getString(6)));
        	simpleGeofence.setExpirationDuration(Long.parseLong(cursor.getString(7)));
        	simpleGeofence.setTransitionType(Integer.parseInt(cursor.getString(8)));

            cursor.close();

            return simpleGeofence;
        }
        db.close();
        
		return null;
    }
    
    public void setGeofence(SimpleGeofence geofence) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	 
        ContentValues values = new ContentValues();

        values.put(GEOFENCE_ID, geofence.getGeofenceId());
        values.put(LATITUDE, geofence.getLatitude());
        values.put(LONGITUDE, geofence.getLongitude()); 
        values.put(LOCATION_NAME, geofence.getName());
        values.put(ADDRESS, geofence.getAddress());
        values.put(RADIUS, geofence.getRadius()); 
        values.put(EXPRIATION_DURATION, geofence.getExpirationDuration()); 
        values.put(TRANSITION_TYPE, geofence.getTransitionType()); 
        
        // Inserting Row
        db.insert(TABLE_GEOFENCE, null, values);
        db.close(); // Closin
    }
    
    public void clearGeofence(String id) {
        /*
         * Remove a flattened geofence object from storage by
         * removing all of its keys
         */
    	
    	SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GEOFENCE, GEOFENCE_ID + " = ?",
                new String[]{id});
        db.close();

    }
    
    public int updateGeofence(SimpleGeofence geofence) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();

        values.put(LATITUDE, geofence.getLatitude());
        values.put(LONGITUDE, geofence.getLongitude()); 
        values.put(LOCATION_NAME, geofence.getName());
        values.put(RADIUS, geofence.getRadius()); 
        values.put(EXPRIATION_DURATION, geofence.getExpirationDuration()); 
        values.put(TRANSITION_TYPE, geofence.getTransitionType());
        
        return db.update(TABLE_GEOFENCE, values, GEOFENCE_ID + " = ?",
                new String[] { geofence.getGeofenceId() });
	}

    public ArrayList<SimpleGeofence> getGeofences() {

        ArrayList<SimpleGeofence> simpleGeofences = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GEOFENCE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SimpleGeofence simpleGeofence = new SimpleGeofence();

                simpleGeofence.setId(cursor.getLong(0));
                simpleGeofence.setGeofenceId(cursor.getString(1));
                simpleGeofence.setLatitude(cursor.getDouble(2));
                simpleGeofence.setLongitude(cursor.getDouble(3));
                simpleGeofence.setName(cursor.getString(4));
                simpleGeofence.setAddress(cursor.getString(5));
                simpleGeofence.setRadius(cursor.getFloat(6));
                simpleGeofence.setExpirationDuration(cursor.getLong(7));
                simpleGeofence.setTransitionType(cursor.getInt(8));
                // Adding contact to list
                simpleGeofences.add(simpleGeofence);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return simpleGeofences;
    }
    
    public void deleteAll() {
    	
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_GEOFENCE);
        db.close();
    }
    
}
