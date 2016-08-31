package csmscproject.riskmodeller;

/**
 * Implementation of the Snap2Line interface
 * 
 * A Snap2Line object evaluates candidate points for
 * snapping to a line and returns the new Snap
 * 
 * @author Johannes Neethling
 *
 */

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

public class Snap2LineImpl implements Snap2Line {

	private LocationIndexedLine line;
	private Coordinate pt;
	private Snap previous;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLine(LocationIndexedLine line) {
		if (line == null) {
			throw new IllegalArgumentException();
		}
		this.line = line;		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPoint(Coordinate pt) {
		if (pt == null) {
			throw new IllegalArgumentException();
		}
		this.pt = pt;		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPrevious(Snap previous) {
		if (previous == null) {
			throw new IllegalArgumentException();
		}
		this.previous = previous;		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Snap snap() {
        double minDist = previous.getMinDist();
        Coordinate minDistPoint = previous.getMinDistPoint();
		LinearLocation here = line.project(pt);
        Coordinate point = line.extractPoint(here);
        double dist = point.distance(pt);
        if (dist < minDist) {
            minDist = dist;
            minDistPoint = point;
        }
        Snap newSnap = new SnapImpl();
        newSnap.setMinDist(minDist);
        newSnap.setMinDistPoint(minDistPoint);
		return newSnap;
	}
}
