package csmscproject.routemapper;

import static org.junit.Assert.*;

import org.junit.Test;

public class RiskAppetiteTest {

	private RiskAppetiteImpl testInstance;

	@Test(expected = IllegalArgumentException.class)
	public void setMaxAccidentCountNegativeTest() {
		
		testInstance = new RiskAppetiteImpl();
		testInstance.setMaxAccidentCount(-1L);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setMaxPollutionPercentageNegativeTest() {
		
		testInstance = new RiskAppetiteImpl();
		testInstance.setMaxPollutionPercentage(-1L);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setMaxPollutionPercentageBigPositiveTest() {
		
		testInstance = new RiskAppetiteImpl();
		testInstance.setMaxPollutionPercentage(101L);
	}
	
	@Test
	public void getAccidentCountNotSetTest() {
		
		testInstance = new RiskAppetiteImpl();
		Long expectedVal = null;
		Long resultVal = testInstance.getMaxAccidentCount();
		assertEquals(expectedVal, resultVal);
	}
	
	@Test
	public void getAccidentCountValidTest() {
		
		testInstance = new RiskAppetiteImpl();
		Long expectedVal = 5L;
		testInstance.setMaxAccidentCount(expectedVal);
		Long resultVal = testInstance.getMaxAccidentCount();
		assertEquals(expectedVal, resultVal);
	}
	
	@Test
	public void getPollutionPercentageNotSetTest() {
		
		testInstance = new RiskAppetiteImpl();
		Long expectedVal = null;
		Long resultVal = testInstance.getMaxPollutionPercentage();
		assertEquals(expectedVal, resultVal);
	}
	
	@Test
	public void getPollutionPercentageValidTest() {
		
		testInstance = new RiskAppetiteImpl();
		Long expectedVal = 5L;
		testInstance.setMaxPollutionPercentage(expectedVal);
		Long resultVal = testInstance.getMaxPollutionPercentage();
		assertEquals(expectedVal, resultVal);
	}
	
}
