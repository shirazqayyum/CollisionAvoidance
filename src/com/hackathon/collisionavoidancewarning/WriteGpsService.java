package com.hackathon.collisionavoidancewarning;

import java.io.PrintWriter;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class WriteGpsService extends Service implements LocationListener{

	@Override
	public void onCreate(){
		/* Grab the location manager and poll for position when position changes */
	    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}
	
	/** method for clients */
    public void setWriter (PrintWriter print_writer){
    	mWriter = print_writer;
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
	
	@Override
	public void onLocationChanged(Location location) {
		mWriter.println(location.toString());	
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
	

	/* Binder given to clients */
    private final IBinder mBinder = new LocalBinder();
    private PrintWriter mWriter;
	
}
