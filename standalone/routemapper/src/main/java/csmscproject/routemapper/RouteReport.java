package csmscproject.routemapper;

public interface RouteReport {

	void setRouteFileName(String routeFileName);
	void setRouteLength(Integer routeLength);
	void setSlope(Double slope);
	void setAccidentCount(Integer accidentCount);
	void setPollutionPercentage(Integer pollutionPercentage);
	String getRouteFileName();
	Integer getRouteLength();
	Double getSlope();
	Integer getAccidentCount();
	Integer getPollutionPercentage();
}
