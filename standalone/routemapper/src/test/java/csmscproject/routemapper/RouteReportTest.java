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
		testInstance.setRouteLength(-1);
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
	@Test(expected = IllegalArgumentException.class)
	public void setAccidentCountNegativeTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setAccidentCount(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void setPollutionPercentageNegativeTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setPollutionPercentage(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void setPollutionPercentageBigPositiveTest() {
		
		testInstance = new RouteReportImpl();
		testInstance.setPollutionPercentage(101);
	}
	

}
