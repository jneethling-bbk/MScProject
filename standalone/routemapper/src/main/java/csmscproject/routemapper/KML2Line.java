package csmscproject.routemapper;

/**
 * KML2Line takes a DOM from a KML file and returns a Geotools line collection
 * 
 * KML2Line takes a DOM created from a KML file and
 * returns a org.geotools.feature.DefaultFeatureCollection.
 * The KML file must validate against the official KML XSD
 * and must contain a route as its first feature.
 * 
 * @author Johannes Neethling
 *
 */

import java.io.IOException;

import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Document;

public interface KML2Line {

	/**
	 * Set the DOM to be converted
	 * 
	 * @param org.w3c.dom.Document: the DOM
	 * @throws IllegalArgumentException if the DOM is null
	 */
	void setDOM(Document routeDoc);
	
	/**
	 * Set the CRS for the output
	 * 
	 * @param org.opengis.referencing.crs.CoordinateReferenceSystem: the CRS
	 * @throws IllegalArgumentException if the file is null
	 */
	void setCRS(CoordinateReferenceSystem displayCRS);
	
	/**
	 * Build the line collection
	 * 
	 * @return org.geotools.feature.DefaultFeatureCollection: the line collection
	 * @throws IOException if the file is not valid or does not contain a route as first feature
	 */
	DefaultFeatureCollection getLine() throws IOException;
	
	/**
	 * Get the line segment count, used to calibrate the progress bar
	 * 
	 * @return int: the segment count
	 */
	int getSegmentCount();
	
}
