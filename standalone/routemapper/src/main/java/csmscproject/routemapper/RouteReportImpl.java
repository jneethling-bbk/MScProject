package csmscproject.routemapper;

public class RouteReportImpl implements RouteReport {

	private String routeFileName;
	private Long routeLength;
	private Double slope;
	private Long accidentCount;
	private Long pollutionPercentage;
	
	public void setRouteFileName(String routeFileName) {
		if (routeFileName == null) {
			throw new IllegalArgumentException("The name of the route cannot be null");
		}
		this.routeFileName = routeFileName;
	}

	public void setRouteLength(Long routeLength) {
		if (routeLength <= 0) {
			throw new IllegalArgumentException("The lenght of the route must be more than zero");
		}
		this.routeLength = routeLength;		
	}

	public void setSlope(Double slope) {
		if (slope > 100 || slope < -100) {
			throw new IllegalArgumentException("The slope value must be between -100 and +100");
		}
		this.slope = slope;
	}

	public void setAccidentCount(Long accidentCount) {
		if (accidentCount < 0) {
			throw new IllegalArgumentException("The accident count cannot be negative");
		}
		this.accidentCount = accidentCount;
	}

	public void setPollutionPercentage(Long pollutionPercentage) {
		if (pollutionPercentage < 0 || pollutionPercentage > 100) {
			throw new IllegalArgumentException("The pollution percentage must be between 0 and 100");
		}
		this.pollutionPercentage = pollutionPercentage;	
	}

	public String getRouteFileName() {
		return routeFileName;
	}

	public Long getRouteLength() {
		return routeLength;
	}

	public Double getSlope() {
		return slope;
	}

	public Long getAccidentCount() {
		return accidentCount;
	}

	public Long getPollutionPercentage() {
		return pollutionPercentage;
	}

}
