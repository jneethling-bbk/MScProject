package csmscproject.riskmodeller;

import static org.junit.Assert.*;

import org.junit.Test;

public class CSV2CoordinateTest {

	private CSV2CoordinateImpl testInstance;
	String goodLine = "201501BS70001, 525130, 180050, -0.198465, 51.505538, 1, 2";
	
	@Test(expected = IllegalArgumentException.class)
	public void setLineNullTest() {
		
		testInstance = new CSV2CoordinateImpl();
		testInstance.setLine(null);
	}
	
	@Test
	public void getCoordinateNotEnoughTokensTest() {
		String testLine = "201501BS70001,525130,180050,-0.198465,51.505538,1";
		testInstance = new CSV2CoordinateImpl();
		testInstance.setLine(testLine);
		assertNull(testInstance.getCoordinate());		
	}
	
	@Test
	public void getCoordinateNotInLondonTest() {
		String testLine = "201501BS70001,525130,180050,-0.198465,51.505538,2,1";
		testInstance = new CSV2CoordinateImpl();
		testInstance.setLine(testLine);
		assertNull(testInstance.getCoordinate());		
	}
	
	@Test
	public void getCoordinateNotSevereTest() {
		String testLine = "201501BS70001,525130,180050,-0.198465,51.505538,1,3";
		testInstance = new CSV2CoordinateImpl();
		testInstance.setLine(testLine);
		assertNull(testInstance.getCoordinate());		
	}
	
	@Test
	public void getCoordinateNotNumericXTest() {
		String testLine = "201501BS70001,525130,180050,A-0.198465,51.505538,1,1";
		testInstance = new CSV2CoordinateImpl();
		testInstance.setLine(testLine);
		assertNull(testInstance.getCoordinate());		
	}
	
	@Test
	public void getCoordinateNotNumericYTest() {
		String testLine = "201501BS70001,525130,180050,-0.198465,A51.505538,1,1";
		testInstance = new CSV2CoordinateImpl();
		testInstance.setLine(testLine);
		assertNull(testInstance.getCoordinate());		
	}
	
	@Test
	public void getCoordinateValidTest() {
		String testLine = "201501BS70001,525130,180050,-0.198465,51.505538,1,1";
		double testX = -0.198465;
		double testY = 51.505538;
		double delta = 0.000001;
		testInstance = new CSV2CoordinateImpl();
		testInstance.setLine(testLine);
		assertEquals(testX, testInstance.getCoordinate().x, delta);	
		assertEquals(testY, testInstance.getCoordinate().y, delta);	
	}

}
