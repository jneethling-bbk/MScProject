package csmscproject.routemapper;

/**
 * Implementation of RouteReport interface
 * 
 * A RouteReport contains the result of a route evaluation.
 * It contains all the data metrics required to
 * display a report and is also used to control route
 * visualisation in conjunction with RiskAppetite.
 * 
 * @author Johannes Neethling
 *
 */

public class RouteReportImpl implements RouteReport {

	private String routeFileName;
	private Long routeLength;
	private Double slope;
	private Long accidentCount;
	private Long pollutionPercentage;
	
	/**
	 * {@inheritDoc}
	 */
	public void setRouteFileName(String routeFileName) {
		if (routeFileName == null) {
			throw new IllegalArgumentException("The name of the route cannot be null");
		}
		this.routeFileName = routeFileName;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setRouteLength(Long routeLength) {
		if (routeLength <= 0) {
			throw new IllegalArgumentException("The lenght of the route must be more than zero");
		}
		this.routeLength = routeLength;		
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSlope(Double slope) {
		if (slope > 100 || slope < -100) {
			throw new IllegalArgumentException("The slope value must be between -100 and +100");
		}
		this.slope = slope;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setAccidentCount(Long accidentCount) {
		if (accidentCount < 0) {
			throw new IllegalArgumentException("The accident count cannot be negative");
		}
		this.accidentCount = accidentCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPollutionPercentage(Long pollutionPercentage) {
		if (pollutionPercentage < 0 || pollutionPercentage > 100) {
			throw new IllegalArgumentException("The pollution percentage must be between 0 and 100");
		}
		this.pollutionPercentage = pollutionPercentage;	
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRouteFileName() {
		return routeFileName;
	}

	/**
	 * {@inheritDoc}
	 */
	public Long getRouteLength() {
		return routeLength;
	}

	/**
	 * {@inheritDoc}
	 */
	public Double getSlope() {
		return slope;
	}

	/**
	 * {@inheritDoc}
	 */
	public Long getAccidentCount() {
		return accidentCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public Long getPollutionPercentage() {
		return pollutionPercentage;
	}
}
