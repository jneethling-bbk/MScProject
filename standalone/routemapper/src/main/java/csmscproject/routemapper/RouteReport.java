package csmscproject.routemapper;

/**
 * A RouteReport contains the result of a route evaluation
 * 
 * A RouteReport contains the result of a route evaluation.
 * It contains all the data metrics required to
 * display a report and is also used to control route
 * visualisation in conjunction with RiskAppetite.
 * 
 * @author Johannes Neethling
 *
 */

public interface RouteReport {
	
	/**
	 * Set the route file's name
	 * 
	 * @param String: routeFileName
	 * @throws IllegalArgumentException if the string is null
	 */
	void setRouteFileName(String routeFileName);
	
	/**
	 * Set the route's length
	 * 
	 * @param Long: routeLength
	 * @throws IllegalArgumentException if the value is zero, negative or null
	 */
	void setRouteLength(Long routeLength);
	
	/**
	 * Set the route's slope gradient
	 * 
	 * @param Double: slope
	 * @throws IllegalArgumentException if the value is null or not between -100 and 100
	 */
	void setSlope(Double slope);
	
	/**
	 * Set the number of accident hot spots that the route encounters
	 * 
	 * @param Long: accidentCount
	 * @throws IllegalArgumentException if the value is negative or null
	 */
	void setAccidentCount(Long accidentCount);
	
	/**
	 * Set the percentage of the route that is subject to severe air pollution
	 * 
	 * @param Long: pollutionPercentage
	 * @throws IllegalArgumentException if the value is null or not between 0 and 100
	 */
	void setPollutionPercentage(Long pollutionPercentage);
	
	/**
	 * Get the route file's name
	 * 
	 * @return String: routeFileName
	 */
	String getRouteFileName();
	
	/**
	 * Get the route's length
	 * 
	 * @return Long: routeLength
	 */
	Long getRouteLength();
	
	/**
	 * Get the route's slope gradient
	 * 
	 * @return Double: slope
	 */
	Double getSlope();
	
	/**
	 * Get the number of accident hot spots that the route encounters
	 * 
	 * @return Long: accidentCount
	 */
	Long getAccidentCount();
	
	/**
	 * Get the percentage of the route that is subject to severe air pollution
	 * 
	 * @return Long: pollutionPercentage
	 */
	Long getPollutionPercentage();
}
