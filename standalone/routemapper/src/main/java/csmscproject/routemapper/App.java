package csmscproject.routemapper;

import javax.swing.JOptionPane;

public class App {
    
	public static void main( String[] args ) {
		
		MapModel model = new MapModel();
		MapView view = new MapView();		
		MapController controller = new MapController(model, view);
		boolean ok = controller.configureMapView();
		if (!ok) {
            JOptionPane.showMessageDialog(null, "Could not connect to WMS");
            System.exit(0);
		}
		
	}
}
