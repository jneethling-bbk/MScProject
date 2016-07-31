package csmscproject.routemapper;

public class App {
    
	public static void main( String[] args ) {
		
		MapModel mModel = new MapModel();
		MapView mView = new MapView();		
		MapController mController = new MapController(mModel, mView);
		mController.DisplayMap();
		
	}
}
