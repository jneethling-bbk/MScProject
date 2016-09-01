package csmscproject.riskmodeller;

/**
 * An IDWinterpolation object calculates an inverse distance weighted average
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

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.referencing.operation.TransformException;

public interface IDWinterpolation {

	/**
	 * Set the geodetic calculator containing the target point
	 * 
	 * @param org.geotools.referencing.GeodeticCalculator: the calculator
	 * @throws IllegalArgumentException if the calculator is null
	 */
	void setCalculator(GeodeticCalculator gc);
	
	/**
	 * Set the collection containing the measurement points
	 * 
	 * @param org.geotools.feature.DefaultFeatureCollection: the measurement points
	 * @throws IllegalArgumentException if the collection is null
	 */
	void setPointCollection(DefaultFeatureCollection pointCollection);
	
	/**
	 * Get the evaluated interpolation for the target point
	 * 
	 * @return Double: the interpolated value
	 * @throws TransformException if the geodetic calculator is not valid
	 */
	Double interpolate() throws TransformException;	
}
