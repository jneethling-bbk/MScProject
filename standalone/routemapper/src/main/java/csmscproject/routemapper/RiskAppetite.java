package csmscproject.routemapper;

/**
 * RiskAppetite represents the maximum risk that can be tolerated
 * 
 * RiskAppetite represents the maximum risk that can be tolerated by the user
 * It contains a value for each risk vector as specified by the user and is
 * used to control route visualisation.
 * 
 * @author Johannes Neethling
 *
 */

public interface RiskAppetite {
	
	/**
	 * Set the maximum number of accident hot spots that can be tolerated
	 * 
	 * @param Long: count
	 * @throws IllegalArgumentException if the value is negative or null
	 */
	void setMaxAccidentCount(Long count);
	
	/**
	 * Set the maximum percentage of the route subject to severe air pollution that can be tolerated
	 * 
	 * @param Long: percentage
	 * @throws IllegalArgumentException if the value is null or not between 0 and 100
	 */
	void setMaxPollutionPercentage(Long percentage);
	
	/**
	 * Get the number of accident hot spots that can be tolerated
	 * 
	 * @return Long: accidentCount
	 */
	Long getMaxAccidentCount();
	
	/**
	 * Get the percentage of the route subject to severe air pollution that can be tolerated
	 * 
	 * @return Long: pollutionPercentage
	 */
	Long getMaxPollutionPercentage();
}
