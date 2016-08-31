package csmscproject.riskmodeller;

/**
 * An implementation of the CSV2Coordinate interface
 * 
 * CSV2Coordinate takes a CSV line String and returns a 
 * com.vividsolutions.jts.geom.Coordinate.  It filters
 * out locations not in London, UK (token[5] == 1 indicating
 * the attendance of the Metropolitan Police at the accident
 * scene) and trivial accidents (token[6] < 3 indicating
 * severe injuries (2) or fatalities (1).  It also checks
 * for numeric validity of the X and Y tokens.
 * 
 * @author Johannes Neethling
 *
 */

import com.vividsolutions.jts.geom.Coordinate;

public class CSV2CoordinateImpl implements CSV2Coordinate {

	private String line;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLine(String line) {
		if (line == null) {
			throw new IllegalArgumentException("The string cannot be null");
		}
		this.line = line;		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Coordinate getCoordinate() {
		Coordinate c = null;
		String tokens[] = line.split("\\,");
        if (tokens.length > 6 && Integer.parseInt(tokens[5]) == 1 && Integer.parseInt(tokens[6]) < 3 && isNumeric(tokens[3]) && isNumeric(tokens[4])) {
        	double latitude = Double.parseDouble(tokens[4]);
        	double longitude = Double.parseDouble(tokens[3]);
        	c = new Coordinate(longitude, latitude);
        }

		return c;
	}
    
	//Private helper method to check if data is numeric before attempting to parse
    private boolean isNumeric(String str) {  
    	try {  
    		Double.parseDouble(str);  
        } catch(NumberFormatException nfe) {  
            return false;  
        }  
            return true;  
    }
}
