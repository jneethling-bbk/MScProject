package csmscproject.routemapper;

public interface RiskAppetite {
	void setMaxAccidentCount(Long count);
	void setMaxPollutionPercentage(Long percentage);
	Long getMaxAccidentCount();
	Long getMaxPollutionPercentage();
}
