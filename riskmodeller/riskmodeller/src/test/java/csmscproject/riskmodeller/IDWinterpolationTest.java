package csmscproject.riskmodeller;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class IDWinterpolationTest {

	private IDWinterpolationImpl testInstance;
	
	@Test(expected=IllegalArgumentException.class)
	public final void setCalculatorNullTest() {

		testInstance = new IDWinterpolationImpl();
		testInstance.setCalculator(null);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void setPointCollectionNullTest() {
		
		testInstance = new IDWinterpolationImpl();
		testInstance.setPointCollection(null);
	}

	@Test(expected=NullPointerException.class)
	public final void interpolateWithCalculatorNotSetTest() {
		
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
		
		Coordinate c1 = new Coordinate(-0.198465, 51.505538);
		Coordinate c2 = new Coordinate(-0.198465, 51.505000);
		
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		Point p1 = geometryFactory.createPoint(c1);
		Point p2 = geometryFactory.createPoint(c2);
		
    	featureBuilder.add("test1");
    	featureBuilder.add(1.0);
    	featureBuilder.add(p1);
    	SimpleFeature feature1 = featureBuilder.buildFeature(null);
    	
    	featureBuilder.add("test2");
    	featureBuilder.add(2.0);
    	featureBuilder.add(p2);
    	SimpleFeature feature2 = featureBuilder.buildFeature(null);
    	
    	DefaultFeatureCollection pointCollection = new DefaultFeatureCollection();
    	pointCollection.add(feature1);
    	pointCollection.add(feature2);
		
		testInstance = new IDWinterpolationImpl();
		testInstance.setPointCollection(pointCollection);
		try {
			testInstance.interpolate();
		} catch (MismatchedDimensionException e) {
			e.printStackTrace();
		} catch (TransformException e) {
			e.printStackTrace();
		}	
	}
	
	@Test
	public final void interpolateWithCalculatorStartingPositionNotSetTest() {
		
		CoordinateReferenceSystem crs = null;
		try {	
			crs = CRS.decode("EPSG:4326");
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		
		GeodeticCalculator gc = new GeodeticCalculator(crs);
		
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
		
		Coordinate c1 = new Coordinate(-0.01, 51.01);
		
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		Point p1 = geometryFactory.createPoint(c1);
		
    	featureBuilder.add("test1");
    	featureBuilder.add(1.0);
    	featureBuilder.add(p1);
    	SimpleFeature feature1 = featureBuilder.buildFeature(null);
    	
    	DefaultFeatureCollection pointCollection = new DefaultFeatureCollection();
    	pointCollection.add(feature1);
		
		testInstance = new IDWinterpolationImpl();
		testInstance.setCalculator(gc);
		testInstance.setPointCollection(pointCollection);
		Double interpolation = 0.0;
		try {
			interpolation = testInstance.interpolate();
		} catch (MismatchedDimensionException e) {
			e.printStackTrace();
		} catch (TransformException e) {
			e.printStackTrace();
		}
		double delta = 0.0000001;
		assertEquals(0.0, interpolation, delta);
	}
	
	@Test(expected=NullPointerException.class)
	public final void interpolateWithPointCollectionNotSetTest() {
		
		CoordinateReferenceSystem crs = null;
		try {	
			crs = CRS.decode("EPSG:4326");
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		
		GeodeticCalculator gc = new GeodeticCalculator(crs);
		try {
			gc.setStartingPosition(JTS.toDirectPosition(new Coordinate(0, 51), crs));
		} catch (TransformException e1) {
			e1.printStackTrace();
		}
		
		testInstance = new IDWinterpolationImpl();
		testInstance.setCalculator(gc);
		try {
			testInstance.interpolate();
		} catch (MismatchedDimensionException e) {
			e.printStackTrace();
		} catch (TransformException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public final void interpolateValidTest() {
		
		CoordinateReferenceSystem crs = null;
		try {	
			crs = CRS.decode("EPSG:4326");
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		
		GeodeticCalculator gc = new GeodeticCalculator(crs);
		try {
			gc.setStartingPosition(JTS.toDirectPosition(new Coordinate(0, 51), crs));
		} catch (TransformException e1) {
			e1.printStackTrace();
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
		
		//must be less than 5000 meters from the target point to get the expected value back!
		Coordinate c1 = new Coordinate(-0.01, 51.01);
		
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		Point p1 = geometryFactory.createPoint(c1);
		
    	featureBuilder.add("test1");
    	featureBuilder.add(1.0);
    	featureBuilder.add(p1);
    	SimpleFeature feature1 = featureBuilder.buildFeature(null);
    	
    	DefaultFeatureCollection pointCollection = new DefaultFeatureCollection();
    	pointCollection.add(feature1);
		
		testInstance = new IDWinterpolationImpl();
		testInstance.setCalculator(gc);
		testInstance.setPointCollection(pointCollection);
		Double testInterpolation = 0.0;
		try {
			testInterpolation = testInstance.interpolate();
		} catch (MismatchedDimensionException e) {
			e.printStackTrace();
		} catch (TransformException e) {
			e.printStackTrace();
		}
		double delta = 0.000001;
		assertEquals(1.0, testInterpolation, delta);
	}
}
