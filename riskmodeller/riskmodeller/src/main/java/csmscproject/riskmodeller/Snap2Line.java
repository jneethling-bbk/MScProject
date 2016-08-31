package csmscproject.riskmodeller;

/**
 * A Snap2Line object evaluates candidate points for snapping to a line
 * 
 * A Snap2Line object evaluates candidate points for
 * snapping to a line and returns the new snapped point
 * location if appropriate
 * 
 * @author Johannes Neethling
 *
 */

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

public interface Snap2Line {

	/**
	 * Set the line that the point is evaluated against
	 * 
	 * @param com.vividsolutions.jts.linearref.LocationIndexedLine: the line
	 * @throws IllegalArgumentException if the line is null
	 */
	void setLine(LocationIndexedLine line);
	
	/**
	 * Set the point to be evaluated
	 * 
	 * @param com.vividsolutions.jts.geom.Coordinate: the point
	 * @throws IllegalArgumentException if the point is null
	 */
	void setPoint(Coordinate pt);
	
	/**
	 * Set the previous snap that was evaluated for comparison
	 * 
	 * @param csmscproject.riskmodeller.Snap: the previous snap candidate
	 * @throws IllegalArgumentException if the Snap is null
	 */
	void setPrevious(Snap previous);
	
	/**
	 * Get the evaluated Snap
	 * 
	 * @return csmscproject.riskmodeller.Snap: the Snap
	 */
	Snap snap();
}
