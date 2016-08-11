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
	

}
