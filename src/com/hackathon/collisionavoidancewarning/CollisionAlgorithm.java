package com.hackathon.collisionavoidancewarning;

	/* The app is smart, it gives you more assistance when you need it.
	 * 
	 * It integrates the contextual information about 
	 * 1) weather 
	 * 2) lighting
	 * 3) other user's activity
	 *  
	 * i.e., knows when it is dark, snowing, raining or when the other person
	 * is walking or driving, and adjusts the detection threshold accordingly.
	 * 
	 * boolean isRaining();
	 * boolean isSnowing();
	 * boolean isDark();
	 * boolean isCar();
	 * boolean isPedestrian();
	 * 
	 * int getThreshold(boolean rain, boolean snow, boolean dark, ...
	 * 								  boolean car, boolean pedestrian);
	 * 
	 */
	
public class CollisionAlgorithm {
	public CollisionAlgorithm() {
	
	}
	
	/* Assumes it is called with string representation of float */
	
	 boolean isClose(String dist) {
		boolean result = false;
		
		if ( !collision ) {
			if ( Float.parseFloat(dist) < range ) 
				return true;
		}
		return result;
	}
	 
	 /* Assumes it is called with string representation of float */
	 
	 boolean isAway(String dist) {
		 boolean result = false;
		 
		 if ( !collision ) {
			 if ( Float.parseFloat(dist) > (range + 7) ) {
				 return true;
			 }
		 }	 
		 return result;
	 }
	 
	public void setRange(int range) {
		this.range = range;
	}
	
	private int range = 50;
	private final boolean collision = false;
}
