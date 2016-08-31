package csmscproject.riskmodeller;

/**
 * Node2Feature takes a XML Node and returns a Geotools Feature
 * 
 * Node2Feature takes a org.w3c.dom.Node and a 
 * org.geotools.feature.simple.SimpleFeatureBuilder
 * and returns a org.opengis.feature.simple.SimpleFeature.
 * 
 * @author Johannes Neethling
 *
 */

import org.w3c.dom.Node;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

public interface Node2Feature {

	/**
	 * Set the XML node to be converted
	 * 
	 * @param org.w3c.dom.Node: the Node
	 * @throws IllegalArgumentException if the Node is null
	 */
	void setNode(Node node);
	
	/**
	 * Set the feature builder to be used
	 * 
	 * @param org.geotools.feature.simple.SimpleFeatureBuilder: the builder
	 * @throws IllegalArgumentException if the builder is null
	 */
	void setFeatureBuilder(SimpleFeatureBuilder featureBuilder);
	
	/**
	 * Build the feature
	 * 
	 * @return org.opengis.feature.simple.SimpleFeature: the feature
	 */
	SimpleFeature getFeature();
	
}
