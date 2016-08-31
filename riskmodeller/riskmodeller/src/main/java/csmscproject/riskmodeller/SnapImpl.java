package csmscproject.riskmodeller;

/**
 * Implementation for the Snap interface
 * 
 * A Snap is a candidate point for snapping to a line.
 * It encapsulates the point and keeps track of the
 * snapping distance for that point
 * 
 * @author Johannes Neethling
 *
 */

import com.vividsolutions.jts.geom.Coordinate;

public class SnapImpl implements Snap {

	private double minDist;
	private Coordinate minDistPoint;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMinDist(double minDist) {
		if (minDist < 0) {
			throw new IllegalArgumentException();
		}
		this.minDist = minDist;		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMinDistPoint(Coordinate minDistPoint) {
		this.minDistPoint = minDistPoint;		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getMinDist() {
		return minDist;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Coordinate getMinDistPoint() {
		return minDistPoint;
	}
}
