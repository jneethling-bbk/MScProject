package csmscproject.riskmodeller;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Point;

public class CustomFeatureTypeBuilderTest {

	private CustomFeatureTypeBuilderImpl testInstance;
	
	@Test(expected = IllegalArgumentException.class)
	public void setNameNullTest() {
		
		testInstance = new CustomFeatureTypeBuilderImpl();
		testInstance.setName(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setAttributesEmptyMapTest() {
		testInstance = new CustomFeatureTypeBuilderImpl();
		testInstance.setAttributes(new HashMap<String, Class<?>>());
	}
	
	@Test(expected = NullPointerException.class)
	public void setGeometryTypeNullTest() {
		testInstance = new CustomFeatureTypeBuilderImpl();
		testInstance.setGeometryType(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setGeometryTypeIllegalValueTest() {
		testInstance = new CustomFeatureTypeBuilderImpl();
		testInstance.setGeometryType(String.class);
	}
	
	@Test(expected = NullPointerException.class)
	public void buildFeatureTypeNameNotSetTest() {
		CoordinateReferenceSystem testCRS = null;
		testInstance = new CustomFeatureTypeBuilderImpl();
		try {
			testCRS = CRS.decode("EPSG:3857");
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		testInstance.setCRS(testCRS);
		testInstance.setGeometryType(Point.class);
		testInstance.buildFeatureType();
	}
	
	@Test(expected = NullPointerException.class)
	public void buildFeatureTypeGeometryTypeNotSetTest() {
		CoordinateReferenceSystem testCRS = null;
		testInstance = new CustomFeatureTypeBuilderImpl();
		testInstance.setName("TestType");
		try {
			testCRS = CRS.decode("EPSG:3857");
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		testInstance.setCRS(testCRS);
		testInstance.buildFeatureType();
	}
	
	@Test
	public void buildFeatureTypeCrsAndAttributesNotSetTest() {
		//Should be able to work without a crs and without any attributes
		//Note: the geometry type itself counts as 1 attribute
		testInstance = new CustomFeatureTypeBuilderImpl();
		testInstance.setName("TestType");
		testInstance.setGeometryType(Point.class);
		SimpleFeatureType testType = testInstance.buildFeatureType();
		assertEquals("TestType", testType.getTypeName());
		assertTrue(testType.getAttributeCount() == 1);
		assertNull(testType.getCoordinateReferenceSystem());
		assertEquals(Point.class, testType.getGeometryDescriptor().getType().getBinding());
	}
	
	@Test
	public void buildFeatureTypeCompleteTest() {
		CoordinateReferenceSystem testCRS = null;
		Map<String, Class<?>> testAttributes = new HashMap<>();
		testAttributes.put("description", String.class);
		testAttributes.put("value", double.class);
		testInstance = new CustomFeatureTypeBuilderImpl();
		testInstance.setName("TestType");
		testInstance.setAttributes(testAttributes);
		try {
			testCRS = CRS.decode("EPSG:3857");
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		testInstance.setCRS(testCRS);
		testInstance.setGeometryType(Point.class);
		SimpleFeatureType testType = testInstance.buildFeatureType();
		assertEquals("TestType", testType.getTypeName());
		assertTrue(testType.getAttributeCount() == 3);
		assertEquals("EPSG:3857", CRS.toSRS(testType.getCoordinateReferenceSystem()));
		assertEquals(Point.class, testType.getGeometryDescriptor().getType().getBinding());
	}
}
