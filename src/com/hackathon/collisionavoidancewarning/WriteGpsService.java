package com.hackathon.collisionavoidancewarning;

import java.io.PrintWriter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class WriteGpsService extends Service implements LocationListener{

	@Override
	public void onCreate(){
		/* Grab the location manager and poll for position when position changes */
	    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, this);
	    
	    mBroadcaster = LocalBroadcastManager.getInstance(this);
	}
	
	@Override
    public void onDestroy(){
		mWriter.println("#");
		super.onDestroy();
	}
	
	/* method for clients */
    public void setWriter (PrintWriter print_writer){
    	mWriter = print_writer;
    }

    public void setExternalLocation(String external_location) {
    	mExternalLocation = external_location;
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		WriteGpsService getService() {
			/* Return this instance of WriteGpsService so clients can call public methods */
			return WriteGpsService.this;
		}	  
	}
	
	/* Android has an activity recognition system that allows the phone   
	 * to determine the user's current activity, such as, walking,
	 * driving or standing still etc.
	 * 
	 * We are packaging this along with the location data (implemented below)
	 * and inform the system on the other end to make better and informed decisions
	 * about the mobility of the current device.
	 */
	
	
	// The app is smart..it gives you more assistance when you need it..it knows when ts snowing..raingin or dark
	// checking the weather - rain snow
	// checking light intensity - ajdust warning radius
	//
	@Override
	public void onLocationChanged(Location location) {
		if (mWriter != null) {		
			String to_send = ((Double) location.getLatitude()).toString()+ " ";
			to_send += ((Double) location.getLongitude()).toString()+ " ";
			to_send += ((Long) location.getTime()).toString()+ " ";
			to_send += ((Float) location.getBearing()).toString()+ " ";
			to_send += ((Float) location.getSpeed()).toString();
			
			mWriter.println(to_send);
			if (mExternalLocation.length() != 0 && mExternalLocation != "#" ) {
				calculateDistance(to_send);
			} 
		}
		Log.d("MyLoc", "local: " + location.toString() );
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub	
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub	
	}
	
	public void calculateDistance(String d) {
		/* parse the external location first */
		
		float[] results = { 0 };
		String[] inArray = mExternalLocation.split("\\s+");
		String[] outArray = d.split("\\s+");
		Location.distanceBetween(Double.parseDouble(inArray[0]), Double.parseDouble(inArray[1]), 
				Double.parseDouble(outArray[0]), Double.parseDouble(outArray[1]), results);
		sendResult(""+results[0]);
	}
	
	public void sendResult(String message) {
	    Intent intent = new Intent(DIST_RESULT);
	    if(message != null)
	        intent.putExtra(DIST_MSG, message);
	    mBroadcaster.sendBroadcast(intent);
	}

	/* Binder given to clients */
    private final IBinder mBinder = new LocalBinder();
    
    private PrintWriter mWriter;
    private String mExternalLocation = "";
    private LocalBroadcastManager mBroadcaster;
    public static final String DIST_RESULT = "com.hackathon.collisionavoidancewarning.REQUEST_PROCESSED";
    public static final String DIST_MSG = "MSG"; 
}
