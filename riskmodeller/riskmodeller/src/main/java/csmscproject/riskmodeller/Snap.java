package csmscproject.riskmodeller;

/**
 * A Snap is a candidate point for snapping to a line
 * 
 * A Snap is a candidate point for snapping to a line.
 * It encapsulates the point and keeps track of the
 * snapping distance for that point
 * 
 * @author Johannes Neethling
 *
 */

import com.vividsolutions.jts.geom.Coordinate;

public interface Snap {

	/**
	 * Set the snapping distance for this point
	 * 
	 * @param double: the snapping distance
	 * @throws IllegalArgumentException if the value is negative
	 */
	void setMinDist(double minDist);
	
	/**
	 * Set the coordinate point to be evaluated
	 * 
	 * @param com.vividsolutions.jts.geom.Coordinate: the point (or null if none)
	 */
	void setMinDistPoint(Coordinate minDistPpoint);
	
	/**
	 * Get the snapping distance for this point
	 * 
	 * @return double: the distance
	 */
	double getMinDist();
	
	/**
	 * Get the coordinate
	 * 
	 * @return com.vividsolutions.jts.geom.Coordinate: the coordinate point
	 */
	Coordinate getMinDistPoint();
}
