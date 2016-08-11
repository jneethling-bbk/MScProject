package csmscproject.routemapper;

public class RouteReportImpl implements RouteReport {

	private String routeFileName;
	private Integer routeLength;
	private Double slope;
	private Integer accidentCount;
	private Integer pollutionPercentage;
	
	public void setRouteFileName(String routeFileName) {
		if (routeFileName == null) {
			throw new IllegalArgumentException("The name of the route cannot be null");
		}
		this.routeFileName = routeFileName;
	}

	public void setRouteLength(Integer routeLength) {
		//if (routeLength <= 0 || routeLength == null) {
			//throw new IllegalArgumentException("The lenght of the route must be more than zero");
		//}
		this.routeLength = routeLength;		
	}

	public void setSlope(Double slope) {
		//if (slope > 100 || slope < -100) {
			//throw new IllegalArgumentException("The slope value must be between -100 and +100");
		//}
		this.slope = slope;
	}

	public void setAccidentCount(Integer accidentCount) {
		//if (accidentCount < 0) {
			//throw new IllegalArgumentException("The accident count cannot be negative");
		//}
		this.accidentCount = accidentCount;
	}

	public void setPollutionPercentage(Integer pollutionPercentage) {
		//if (pollutionPercentage < 0 || pollutionPercentage > 100) {
			//throw new IllegalArgumentException("The pollution percentage must be between 0 and 100");
		//}
		this.pollutionPercentage = pollutionPercentage;	
	}

	public String getRouteFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getRouteLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Double getSlope() {
		// TODO Auto-generated method stub
		return 0.0;
	}

	public Integer getAccidentCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Integer getPollutionPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}

}
