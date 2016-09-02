package csmscproject.riskmodeller;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.vividsolutions.jts.geom.Point;

public class Node2FeatureTest {

	private Node2FeatureImpl testInstance;
	
	
	@Test(expected = IllegalArgumentException.class)
	public final void setNodeNullTest() {
		
		testInstance = new Node2FeatureImpl();
		testInstance.setNode(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public final void setFeatureBuilderNullTest() {
		
		testInstance = new Node2FeatureImpl();
		testInstance.setFeatureBuilder(null);
	}
	
	@Test(expected = NullPointerException.class)
	public final void getFeatureWithNodeNotSetTest() {
		
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.decode("EPSG:4326");
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		Map<String, Class<?>> testAttributes = new HashMap<>();
		testAttributes.put("name", String.class);
		testAttributes.put("pollution", Double.class);		
		CustomFeatureTypeBuilder testBuilder = new CustomFeatureTypeBuilderImpl();
		testBuilder.setName("Sites");
		testBuilder.setAttributes(testAttributes);
		testBuilder.setCRS(crs);
		testBuilder.setGeometryType(Point.class);
		final SimpleFeatureType TEST = testBuilder.buildFeatureType();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TEST);
		
		testInstance = new Node2FeatureImpl();
		testInstance.setFeatureBuilder(featureBuilder);
		testInstance.getFeature();
		
	}

	@Test(expected = NullPointerException.class)
	public final void getFeatureWithFeatureBuilderNotSetTest() {
		
		String xmlString = "<LocalAuthority><Site SiteName='Barking and Dagenham - Rush Green' Latitude='51.563752' Longitude='0.177891'><Species AirQualityIndex='1'/><Species AirQualityIndex='2'/></Site></LocalAuthority>";		
		DocumentBuilder db = null;
		Document doc = null;
		
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlString));

		try {
			doc = db.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		NodeList nList = doc.getElementsByTagName("Site");
		Node testNode = nList.item(0);
		
		testInstance = new Node2FeatureImpl();
		testInstance.setNode(testNode);
		testInstance.getFeature();
	}
	
	@Test(expected = NullPointerException.class)
	public final void getFeatureWithInValidXMLTest() {
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.decode("EPSG:4326");
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		Map<String, Class<?>> testAttributes = new HashMap<>();
		testAttributes.put("name", String.class);
		testAttributes.put("pollution", Double.class);		
		CustomFeatureTypeBuilder testBuilder = new CustomFeatureTypeBuilderImpl();
		testBuilder.setName("Sites");
		testBuilder.setAttributes(testAttributes);
		testBuilder.setCRS(crs);
		testBuilder.setGeometryType(Point.class);
		final SimpleFeatureType TEST = testBuilder.buildFeatureType();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TEST);
		
		String xmlString = "<LocalAuthority><Site SiteName='Barking and Dagenham - Rush Green'><Species AirQualityIndex='1'/><Species AirQualityIndex='2'/></Site></LocalAuthority>";		
		DocumentBuilder db = null;
		Document doc = null;
		
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlString));

		try {
			doc = db.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		NodeList nList = doc.getElementsByTagName("Site");
		Node testNode = nList.item(0);
		
		testInstance = new Node2FeatureImpl();
		testInstance.setNode(testNode);
		testInstance.setFeatureBuilder(featureBuilder);
		testInstance.getFeature();
	}
	
	@Test
	public final void getFeatureValidTest() {
		
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.decode("EPSG:4326");
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		Map<String, Class<?>> testAttributes = new HashMap<>();
		testAttributes.put("name", String.class);
		testAttributes.put("pollution", Double.class);		
		CustomFeatureTypeBuilder testBuilder = new CustomFeatureTypeBuilderImpl();
		testBuilder.setName("Sites");
		testBuilder.setAttributes(testAttributes);
		testBuilder.setCRS(crs);
		testBuilder.setGeometryType(Point.class);
		final SimpleFeatureType TEST = testBuilder.buildFeatureType();
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TEST);
		
		String xmlString = "<LocalAuthority><Site SiteName='Barking and Dagenham - Rush Green' Latitude='51.563752' Longitude='0.177891'><Species AirQualityIndex='1'/><Species AirQualityIndex='2'/></Site></LocalAuthority>";		
		DocumentBuilder db = null;
		Document doc = null;
		
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlString));

		try {
			doc = db.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		NodeList nList = doc.getElementsByTagName("Site");
		Node testNode = nList.item(0);
		
		testInstance = new Node2FeatureImpl();
		testInstance.setNode(testNode);
		testInstance.setFeatureBuilder(featureBuilder);
		SimpleFeature feature = testInstance.getFeature();
		double testValue = (double) feature.getAttribute("pollution");
		double delta = 0.000001;
		assertEquals(1.5, testValue, delta);
		Point testPoint = (Point) feature.getDefaultGeometry();
		assertEquals(testPoint.getCoordinate().y, 0.177891, delta);
		assertEquals(testPoint.getCoordinate().x, 51.563752, delta);
	}
}
