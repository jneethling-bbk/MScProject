package csmscproject.riskmodeller;

/**
 * A CustomFeatureTypeBuilder wraps up all properties required to build a SimpleFeatureType
 * 
 * A CustomFeatureTypeBuilder wraps up all properties required
 * to build a Geotools SimpleFeatureType.  Normally building a
 * feature type requires that properties be added in a specific
 * order i.e. first the name, then all the attributes (if any)
 * then the coordinate reference system and finally the geometry type.
 * This class makes it easier to build a feature type and makes the code
 * in the calling class more readable and modular.
 * 
 * @author Johannes Neethling
 *
 */

import java.util.Map;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public interface CustomFeatureTypeBuilder {

	/**
	 * Set the name for the new feature type
	 * 
	 * @param String: the name
	 * @throws IllegalArgumentException if the string is null
	 */
	void setName(String name);
	
	/**
	 * Set the attributes for the new feature type
	 * 
	 * @param HashMap<String, Class<?>>: Key/Value set of attribute names and their data types (or null if none)
	 * @throws IllegalArgumentException if the map is empty
	 */
	void setAttributes(Map<String, Class<?>> attributes);
	
	/**
	 * Set the coordinate reference system for the new feature type
	 * 
	 * @param org.opengis.referencing.crs.CoordinateReferenceSystem: the CRS (or null if none)
	 */
	void setCRS(CoordinateReferenceSystem crs);
	
	/**
	 * Set the geometry for the new feature type
	 * 
	 * @param Class<?>: The class binding of the geometry type
	 * @throws IllegalArgumentException if not one of the com.vividsolutions.jts.geom.* bindings
	 */
	void setGeometryType(Class<?> geomType);
	
	/**
	 * Build the feature type
	 * 
	 * @return org.opengis.feature.simple.SimpleFeatureType: the completed feature type
	 * @throws NullPointerExcpetion if the name and/or geometry type are not set up
	 */
	SimpleFeatureType buildFeatureType();
}
