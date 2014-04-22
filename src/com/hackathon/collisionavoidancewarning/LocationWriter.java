package com.hackathon.collisionavoidancewarning;

import java.io.PrintWriter;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationWriter implements LocationListener {

	public LocationWriter(PrintWriter w, Context c) {
		mWriter = w;
		mContext = c;
		/* Grab the location manager and poll for position when position changes */
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, this);
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
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
	private PrintWriter mWriter;
	private Context mContext;
	
}
