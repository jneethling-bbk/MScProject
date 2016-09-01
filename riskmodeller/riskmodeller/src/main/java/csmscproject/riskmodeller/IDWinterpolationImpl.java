package csmscproject.riskmodeller;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Point;

/**
 * Implementation of the IDWinterpolation interface
 * 
 * An IDWinterpolation object calculates an inverse distance weighted
 * average for a point given a set of randomly distributed measurement
 * points. The location of the target point must be wrapped in a
 * org.geotools.referencing.GeodeticCalculator object as the starting
 * position of the calculator.  It is the responsibility of the calling
 * class to ensure this is set correctly.
 * 
 * @author Johannes Neethling
 *
 */

public class IDWinterpolationImpl implements IDWinterpolation {

	private GeodeticCalculator gc;
	private CoordinateReferenceSystem crs;
	private DefaultFeatureCollection pointCollection;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCalculator(GeodeticCalculator gc) {
		if (gc == null) {
			throw new IllegalArgumentException();
		}
		this.gc = gc;
		this.crs = gc.getCoordinateReferenceSystem();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPointCollection(DefaultFeatureCollection pointCollection) {
		if (pointCollection == null) {
			throw new IllegalArgumentException();
		}
		this.pointCollection = pointCollection;	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double interpolate() throws TransformException {
		double numerator = 0.0;
		double denominator = 0.0;
		SimpleFeatureIterator pointIterator = (SimpleFeatureIterator) pointCollection.features();
		try {
			while(pointIterator.hasNext()) {
				SimpleFeature pointFeature = pointIterator.next();
				double pointval = (Double) pointFeature.getAttribute("pollution");
				if (pointval > 0) {
					Point valuepoint = (Point) pointFeature.getDefaultGeometry();
					gc.setDestinationPosition(JTS.toDirectPosition(valuepoint.getCoordinate(), crs));
					double distance = gc.getOrthodromicDistance();
					if (distance < 5000) {
						numerator = numerator + (pointval/Math.pow(distance, 2));
						denominator = denominator + (1/Math.pow(distance, 2));
					}
				}
			}
		} finally {
			pointIterator.close();
		}
		double interpolation = 0.0;
		if (denominator > 0) {
			interpolation = numerator/denominator;
		}
		return interpolation;
	}

}
