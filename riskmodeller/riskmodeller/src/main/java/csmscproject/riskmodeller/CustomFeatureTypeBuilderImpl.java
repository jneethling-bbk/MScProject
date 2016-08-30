package csmscproject.riskmodeller;

/**
 * An implementation of the CustomFeatureTypeBuilder interface
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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class CustomFeatureTypeBuilderImpl implements CustomFeatureTypeBuilder {

	private String name;
	private Map<String, Class<?>> attributes;
	private CoordinateReferenceSystem crs;
	private Class<?> geomType;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("The name of the feature type cannot be null");
		}
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAttributes(Map<String, Class<?>> attributes) {
		if (attributes.isEmpty()) {
			throw new IllegalArgumentException("The attributes cannot be an emty HashMap");
		}
		this.attributes = attributes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCRS(CoordinateReferenceSystem crs) {
		this.crs = crs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGeometryType(Class<?> geomType) {
		boolean valid = false;
		if (geomType.equals(Point.class) || geomType.equals(Polygon.class) || geomType.equals(MultiPolygon.class) || geomType.equals(LineString.class) || geomType.equals(MultiLineString.class)) {
			valid = true;
		}
		if (!valid) {
			throw new IllegalArgumentException("The geometry type must be one either Point, Line or Polygon");
		}
		this.geomType = geomType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleFeatureType buildFeatureType() {
    	SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        //set the name first
        builder.setName(name);
        //add some properties
        if (attributes != null) {
        	Iterator<Entry<String, Class<?>>> it = attributes.entrySet().iterator();
        	while (it.hasNext()) {
        		Map.Entry pair = (Map.Entry)it.next();
        		builder.add((String) pair.getKey(), (Class<?>) pair.getValue());
        	}
        }
        //then add the geometry properties
        builder.setCRS(crs); // set crs first
        builder.add("the_geom", geomType); // then add geometry
        //then build the type
        SimpleFeatureType type = builder.buildFeatureType();	
    	return type;
	}

}
