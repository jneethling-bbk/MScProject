package csmscproject.routemapper;

public interface RouteReport {

	void setRouteFileName(String routeFileName);
	void setRouteLength(Long routeLength);
	void setSlope(Double slope);
	void setAccidentCount(Long accidentCount);
	void setPollutionPercentage(Long pollutionPercentage);
	String getRouteFileName();
	Long getRouteLength();
	Double getSlope();
	Long getAccidentCount();
	Long getPollutionPercentage();
}
