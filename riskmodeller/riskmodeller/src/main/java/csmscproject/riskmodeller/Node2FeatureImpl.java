package csmscproject.riskmodeller;

/**
 * Implementation of the Node2Feature interface
 * 
 * Node2Feature takes a org.w3c.dom.Node and a 
 * org.geotools.feature.simple.SimpleFeatureBuilder
 * and returns a org.opengis.feature.simple.SimpleFeature.
 * The calling class must ensure that the XML is well-
 * formed and structured correctly for the application.
 * 
 * @author Johannes Neethling
 *
 */

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class Node2FeatureImpl implements Node2Feature {

	private Node node;
	private SimpleFeatureBuilder featureBuilder;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNode(Node node) {
		if (node == null) {
			throw new IllegalArgumentException();
		}
		this.node = node;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFeatureBuilder(SimpleFeatureBuilder featureBuilder) {
		if (featureBuilder == null) {
			throw new IllegalArgumentException();
		}
		this.featureBuilder = featureBuilder;
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleFeature getFeature() {
		double lat = Double.parseDouble(node.getAttributes().getNamedItem("Latitude").getNodeValue());
    	double lon = Double.parseDouble(node.getAttributes().getNamedItem("Longitude").getNodeValue());
    	String sitename = node.getAttributes().getNamedItem("SiteName").getNodeValue();
    	
    	NodeList sList = node.getChildNodes();
    	
    	double total = 0.0;
    	int validMeasures = 0;
    	for (int j=0; j<sList.getLength(); j++) {
    		Node sNode = sList.item(j);
    		double reading = Double.parseDouble(sNode.getAttributes().getNamedItem("AirQualityIndex").getNodeValue());
    		if (reading > 0) {
    			total = total + reading;
    			validMeasures = validMeasures + 1;
    		}
    	}

    	double val = total/validMeasures;
    	Coordinate c = new Coordinate(lat, lon);
    	GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
    	Point point = geometryFactory.createPoint(c);
    	featureBuilder.add(sitename);
    	featureBuilder.add(val);
    	featureBuilder.add(point);
    	SimpleFeature feature = featureBuilder.buildFeature(null);
		return feature;
	}

}
