package csmscproject.routemapper;

/**
 * Implementation of RiskAppetite interface
 * 
 * RiskAppetite represents the maximum risk that can be tolerated by the user
 * It contains a value for each risk vector as specified by the user and is
 * used to control route visualisation.
 * 
 * @author Johannes Neethling
 *
 */

public class RiskAppetiteImpl implements RiskAppetite {

	private Long maxAccidentCount;
	private Long MaxPollutionPercentage;
	
	/**
	 * {@inheritDoc}
	 */
	public void setMaxAccidentCount(Long count) {
		if (count < 0) {
			throw new IllegalArgumentException("The accident count cannot be negative");
		}
		this.maxAccidentCount = count;
		
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMaxPollutionPercentage(Long percentage) {
		if (percentage < 0 || percentage > 100) {
			throw new IllegalArgumentException("The pollution percentage must be between 0 and 100");
		}
		this.MaxPollutionPercentage = percentage;
		
	}

	/**
	 * {@inheritDoc}
	 */
	public Long getMaxAccidentCount() {
		return this.maxAccidentCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public Long getMaxPollutionPercentage() {
		return this.MaxPollutionPercentage;
	}
}
