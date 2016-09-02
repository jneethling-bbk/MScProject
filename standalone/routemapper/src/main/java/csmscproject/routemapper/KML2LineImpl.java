package csmscproject.routemapper;

/**
 * Implementation of the KML2Line interface
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
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public class KML2LineImpl implements KML2Line {

	private Document routeDoc;
	private CoordinateReferenceSystem displayCRS;
	
	/**
	 * {@inheritDoc}
	 */
	public void setDOM(Document routeDoc) {
		if (routeDoc == null) {
			throw new IllegalArgumentException();
		}
		this.routeDoc = routeDoc;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setCRS(CoordinateReferenceSystem displayCRS) {
		if (displayCRS == null) {
			throw new IllegalArgumentException();
		}
		this.displayCRS = displayCRS;		
	}

	/**
	 * {@inheritDoc}
	 */
	public DefaultFeatureCollection getLine() throws IOException {
		CoordinateReferenceSystem computationCRS = null;
		try {
			computationCRS = CRS.decode("EPSG:4326");
		} catch (NoSuchAuthorityCodeException e) {
			// Only caused by coding errors
			e.printStackTrace();
		} catch (FactoryException e) {
			// Only caused by coding errors
			e.printStackTrace();
		}
        
        routeDoc.getDocumentElement().normalize();       
        NodeList nList = routeDoc.getElementsByTagName("coordinates");
		if (nList.getLength() < 1) {
			// Maybe create a custom exception
			throw new IOException();
		}
		Node cNode = nList.item(0);
		String raw = cNode.getTextContent();
		String[] masterArray = raw.split(" ");
		Coordinate[] tempcoords = new Coordinate[masterArray.length];
		int index = 0;
		for (String s : masterArray) {
			String[] subArray = s.split(",");
			
			if (subArray.length > 2 && isNumeric(subArray[1]) && isNumeric(subArray[0])) {
				Coordinate c = new Coordinate(Double.parseDouble(subArray[1]), Double.parseDouble(subArray[0]));
				tempcoords[index] = c;
				index++;
			}
		}
		Coordinate[] coords = new Coordinate[index];
		for (int n=0; n<index; n++) {
			coords[n] = tempcoords[n];
		}
        
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        LineString line = geometryFactory.createLineString(coords);
        
        MathTransform transform = null;
		try {
			transform = CRS.findMathTransform(computationCRS, displayCRS, true);
		} catch (FactoryException e) {
			// Only caused by coding errors
			e.printStackTrace();
		}
        Geometry transformedLine = null;
		try {
			transformedLine = JTS.transform(line, transform);
		} catch (MismatchedDimensionException e) {
			// Only caused by coding errors
			e.printStackTrace();
		} catch (TransformException e) {
			// Only caused by coding errors
			e.printStackTrace();
		}
        
        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
        //set the name first
        b.setName("Route");
        //then add the geometry properties
        b.setCRS(displayCRS); // set crs first
        b.add("the_geom", MultiLineString.class); // then add geometry
        //then build the type
        final SimpleFeatureType ROUTE = b.buildFeatureType();
        
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(ROUTE);
        featureBuilder.add(transformedLine);
        SimpleFeature feature = featureBuilder.buildFeature(null);
        DefaultFeatureCollection lineCollection = new DefaultFeatureCollection();
        lineCollection.add(feature);
        return lineCollection;
	}
		
	// Private helper method to ensure data is numeric
    private boolean isNumeric(String str) {  
        try {  
          Double.parseDouble(str);  
        } catch(NumberFormatException nfe) {  
          return false;  
        }  
        return true;  
      }
}
