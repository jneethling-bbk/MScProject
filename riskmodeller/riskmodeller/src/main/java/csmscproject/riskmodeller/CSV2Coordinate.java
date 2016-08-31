package csmscproject.riskmodeller;

/**
 * CSV2Coordinate takes a CSV line String and returns a coordinate
 * 
 * CSV2Coordinate takes a CSV line String and returns a 
 * com.vividsolutions.jts.geom.Coordinate.
 * 
 * @author Johannes Neethling
 *
 */

import com.vividsolutions.jts.geom.Coordinate;

public interface CSV2Coordinate {

	/**
	 * Set the line from the CSV file
	 * 
	 * @param String: the line of comma separated values
	 * @throws IllegalArgumentException if the string is null
	 */
	void setLine(String line);
	
	/**
	 * Build the coordinate
	 * 
	 * @return com.vividsolutions.jts.geom.Coordinate: the coordinate (or null if not valid)
	 */
	Coordinate getCoordinate();
	
}
