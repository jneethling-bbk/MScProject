package csmscproject.routemapper;

import java.io.IOException;

import org.geotools.ows.ServiceException;

public class MapController {

	private MapModel model;
	private MapView view;
	
	public MapController(MapModel model, MapView view) {
		this.model = model;
		this.view = view;		
	}
	
	public void DisplayMap() {
		try {
			view.setDisplayLayer(model.getBackdrop());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		view.displayMap();
	}
}
