package com.hackathon.collisionavoidancewarning;

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
