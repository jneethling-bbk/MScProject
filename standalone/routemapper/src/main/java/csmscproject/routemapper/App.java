package csmscproject.routemapper;

import java.io.IOException;
import org.geotools.ows.ServiceException;

public class App {
    
	public static void main( String[] args ) throws ServiceException, IOException {
		
		App app = new App();		
		MapModel model = new MapModel();
		MapView view = new MapView();		
		MapController controller = new MapController(model, view);
		app.launchApp(controller, view);
	}
	
	private void launchApp(MapController controller, MapView view) {		
		try {
			controller.configureMapView();
		} catch (ServiceException e) {
			handleCriticalError(view);
		} catch (IOException e) {
			handleCriticalError(view);
		} catch (NullPointerException e) {
			handleCriticalError(view);
		}
	}
	
	private void handleCriticalError(MapView view) {
		view.displayErrorMessage("Problem with web the map servers, please try again later");
		System.exit(0);
	}
}
