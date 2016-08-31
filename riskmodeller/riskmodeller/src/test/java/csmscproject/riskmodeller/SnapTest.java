package csmscproject.riskmodeller;

import static org.junit.Assert.*;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

public class SnapTest {

	private SnapImpl testInstance;
	
	@Test(expected = IllegalArgumentException.class)
	public void setMinDistNegativeTest() {
		
		testInstance = new SnapImpl();
		testInstance.setMinDist(-1);
	}
	
	@Test
	public void getMinDistNotSetTest() {
		
		testInstance = new SnapImpl();
		Double expectedVal = 0.0;
		Double resultVal = testInstance.getMinDist();
		assertEquals(expectedVal, resultVal);
	}
	
	@Test
	public void getMinDistValidTest() {
		
		testInstance = new SnapImpl();
		testInstance.setMinDist(0.1);
		Double expectedVal = 0.1;
		Double resultVal = testInstance.getMinDist();
		assertEquals(expectedVal, resultVal);
	}
	
	@Test
	public void getMinDistPointNotSetTest() {
		
		testInstance = new SnapImpl();
		assertNull(testInstance.getMinDistPoint());
	}

	@Test
	public void getMinDistPointValidTest() {
		
		testInstance = new SnapImpl();
		double longitude = -0.198465;
		double latitude = 51.505538;
		double delta = 0.000001;
		Coordinate testCoordinate = new Coordinate(longitude, latitude);
		testInstance.setMinDistPoint(testCoordinate);
		assertEquals(longitude, testInstance.getMinDistPoint().x, delta);	
		assertEquals(latitude, testInstance.getMinDistPoint().y, delta);	
	}
	
}
