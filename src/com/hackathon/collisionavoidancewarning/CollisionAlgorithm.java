package com.hackathon.collisionavoidancewarning;

	
/**
 *	The collision warning is set up to provide more assistance when the driver needs it.
 * 
 * The decision whether to issue a warning or not integrates the contextual information about 
 * 1) weather 
 * 2) lighting
 * 3) the other user's activity
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
 * 					boolean car, boolean pedestrian);
 * 
 *
 * @author Clark
 * @author Shiraz
 *
 *
 */
public class CollisionAlgorithm {
	public CollisionAlgorithm() {
	
	}
	
	/** Current algorithm just looks at range to the other object 
	 * The method checks whether the given distance is within the threshold 
	 *  
	 * @param dist Assumes it is called with string representation of float 
	 * @return true/false
	 */
	public boolean isClose(String dist) {
		boolean result = false;
		
		if ( !collision ) {
			if ( Float.parseFloat(dist) < range ) 
				return true;
		}
		return result;
	}
	 
	 
	 /** Checks whether the given distance is greater than the defined threshold
	 * @param dist As string representation of float
	 * @return true/false
	 */
	public boolean isAway(String dist) {
		 boolean result = false;
		 
		 if ( !collision ) {
			 if ( Float.parseFloat(dist) > (range + 7) ) {
				 return true;
			 }
		 }	 
		 return result;
	 }
	 
	/** Let the client externally set the range to be used for collision detection
	 * @param range in meters. Defaults to 50m
	 */
	public void setRange(int range) {
		this.range = range;
	}
	
	private int range = 50;
	private final boolean collision = false;
}
