package csmscproject.routemapper;

public class App {
    
	public static void main( String[] args ) {
		MapModel model = new MapModel();
		MapView view = new MapView();		
		MapController controller = new MapController(model, view);
		controller.DisplayMap();
		
	}
}
