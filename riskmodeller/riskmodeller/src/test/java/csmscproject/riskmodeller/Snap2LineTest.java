package csmscproject.riskmodeller;

import static org.junit.Assert.*;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

public class Snap2LineTest {

	private Snap2LineImpl testInstance;
	
	@Test(expected = IllegalArgumentException.class)
	public final void setLineNullTest() {
		
		testInstance = new Snap2LineImpl();
		testInstance.setLine(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public final void setPointNullTest() {
		
		testInstance = new Snap2LineImpl();
		testInstance.setPoint(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public final void setPreviousNullTest() {
		
		testInstance = new Snap2LineImpl();
		testInstance.setPrevious(null);
	}
	
	@Test(expected = NullPointerException.class)
	public final void snapWithLineNotSetTest() {
		
		testInstance = new Snap2LineImpl();
		testInstance.setPoint(new Coordinate(-0.198465, 51.505538));
		testInstance.setPrevious(new SnapImpl());
		testInstance.snap();
	}
	
	@Test(expected = NullPointerException.class)
	public final void snapWithPointNotSetTest() {
		
		Coordinate c1 = new Coordinate(-0.198465, 51.505538);
		Coordinate c2 = new Coordinate(-0.198465, 51.505000);
		Coordinate[] coords = {c1, c2};
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        LineString line = geometryFactory.createLineString(coords);
        LocationIndexedLine testLine = new LocationIndexedLine(line); 
		
        testInstance = new Snap2LineImpl();
		testInstance.setLine(testLine);
		testInstance.setPrevious(new SnapImpl());
		testInstance.snap();
	}
	
	@Test(expected = NullPointerException.class)
	public final void snapWithPreviousNotSetTest() {
		
		Coordinate c1 = new Coordinate(-0.198465, 51.505538);
		Coordinate c2 = new Coordinate(-0.198465, 51.505000);
		Coordinate[] coords = {c1, c2};
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        LineString line = geometryFactory.createLineString(coords);
        LocationIndexedLine testLine = new LocationIndexedLine(line); 
		
        testInstance = new Snap2LineImpl();
        testInstance.setPoint(new Coordinate(-0.198400, 51.505538));
        testInstance.setLine(testLine);
		testInstance.snap();
	}
	
	@Test
	public final void snapValidTest() {
		
		double delta = 0.000001;
		Coordinate c1 = new Coordinate(-0.198465, 51.505538);
		Coordinate c2 = new Coordinate(-0.198465, 51.505000);
		Coordinate[] coords = {c1, c2};
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        LineString line = geometryFactory.createLineString(coords);
        LocationIndexedLine testLine = new LocationIndexedLine(line);
        
        Coordinate testPoint = new Coordinate(-0.198460, 51.505535);
		LinearLocation here = testLine.project(testPoint);
        Coordinate point = testLine.extractPoint(here);
        double dist = point.distance(testPoint);
		
        testInstance = new Snap2LineImpl();
        testInstance.setPoint(testPoint);
        testInstance.setLine(testLine);
        Snap previous = new SnapImpl();
        previous.setMinDist(10.0);
        previous.setMinDistPoint(new Coordinate(-0.1, 51.5));
        
        testInstance.setPrevious(previous);
		Snap testSnap = testInstance.snap();
		assertEquals(testSnap.getMinDist(), dist, delta);
		assertEquals(testSnap.getMinDistPoint().x, point.x, delta);
		assertEquals(testSnap.getMinDistPoint().y, point.y, delta);
	}

}
