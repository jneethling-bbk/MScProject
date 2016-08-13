package csmscproject.routemapper;

import static org.junit.Assert.*;

import org.junit.Test;

public class RouteReportTest {

	private RouteReportImpl testInstance;
	
	@Test(expected = IllegalArgumentException.class)
	public void setRouteFileNameNullTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setRouteFileName(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void setRouteLengthNullTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setRouteLength(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setRouteLengthNegativeTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setRouteLength(-1L);
	}
	
	@Test(expected = NullPointerException.class)
	public void setSlopeNullTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setSlope(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setSlopeBigNegativeTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setSlope(-101.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setSlopeBigPositiveTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setSlope(101.0);
	}
	
	@Test(expected = NullPointerException.class)
	public void setAccidentCountNullTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setAccidentCount(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setAccidentCountNegativeTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setAccidentCount(-1L);
	}
	
	@Test(expected = NullPointerException.class)
	public void setPollutionPercentageNullTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setPollutionPercentage(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setPollutionPercentageNegativeTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setPollutionPercentage(-1L);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setPollutionPercentageBigPositiveTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setPollutionPercentage(101L);
	}
	
	@Test
	public void getRouteFileNameNotSetTest() {
		
		testInstance = new RouteReportImpl();
		String expectedString = null;
		String resultString = testInstance.getRouteFileName();
		assertEquals(expectedString, resultString);
	}
	
	@Test
	public void getRouteFileNameValidTest() {
		
		testInstance = new RouteReportImpl();
		String expectedString = "DummyFileName";
		testInstance.setRouteFileName(expectedString);
		String resultString = testInstance.getRouteFileName();
		assertEquals(expectedString, resultString);
	}
	
	@Test
	public void getRouteLengthNotSetTest() {
		
		testInstance = new RouteReportImpl();
		Long expectedVal = null;
		Long resultVal = testInstance.getRouteLength();
		assertEquals(expectedVal, resultVal);
	}
	
	@Test
	public void getRouteLengthValidTest() {
		
		testInstance = new RouteReportImpl();
		Long expectedVal = 2500L;
		testInstance.setRouteLength(expectedVal);
		Long resultVal = testInstance.getRouteLength();
		assertEquals(expectedVal, resultVal);
	}

	@Test
	public void getSlopeNotSetTest() {
		
		testInstance = new RouteReportImpl();
		Double expectedVal = null;
		Double resultVal = testInstance.getSlope();
		assertEquals(expectedVal, resultVal);
	}
	
	@Test
	public void getSlopeValidTest() {
		
		testInstance = new RouteReportImpl();
		Double expectedVal = 2.5;
		testInstance.setSlope(expectedVal);
		Double resultVal = testInstance.getSlope();
		assertEquals(expectedVal, resultVal);
	}
	
	@Test
	public void getAccidentCountNotSetTest() {
		
		testInstance = new RouteReportImpl();
		Long expectedVal = null;
		Long resultVal = testInstance.getAccidentCount();
		assertEquals(expectedVal, resultVal);
	}
	
	@Test
	public void getAccidentCountValidTest() {
		
		testInstance = new RouteReportImpl();
		Long expectedVal = 5L;
		testInstance.setAccidentCount(expectedVal);
		Long resultVal = testInstance.getAccidentCount();
		assertEquals(expectedVal, resultVal);
	}
	
	@Test
	public void getPollutionPercentageNotSetTest() {
		
		testInstance = new RouteReportImpl();
		Long expectedVal = null;
		Long resultVal = testInstance.getPollutionPercentage();
		assertEquals(expectedVal, resultVal);
	}
	
	@Test
	public void getPollutionPercentageValidTest() {
		
		testInstance = new RouteReportImpl();
		Long expectedVal = 5L;
		testInstance.setPollutionPercentage(expectedVal);
		Long resultVal = testInstance.getPollutionPercentage();
		assertEquals(expectedVal, resultVal);
	}
	
}
