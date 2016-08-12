package csmscproject.routemapper;

public class RiskAppetiteImpl implements RiskAppetite {

	private Long maxAccidentCount;
	private Long MaxPollutionPercentage;
	
	public void setMaxAccidentCount(Long count) {
		if (count < 0) {
			throw new IllegalArgumentException("The accident count cannot be negative");
		}
		this.maxAccidentCount = count;
		
	}

	public void setMaxPollutionPercentage(Long percentage) {
		if (percentage < 0 || percentage > 100) {
			throw new IllegalArgumentException("The pollution percentage must be between 0 and 100");
		}
		this.MaxPollutionPercentage = percentage;
		
	}

	public Long getMaxAccidentCount() {
		return this.maxAccidentCount;
	}

	public Long getMaxPollutionPercentage() {
		return this.MaxPollutionPercentage;
	}

}
