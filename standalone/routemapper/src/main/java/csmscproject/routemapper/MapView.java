package csmscproject.routemapper;

import javax.swing.JFrame;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.WMSLayer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class MapView extends JFrame {

	private static final long serialVersionUID = -5894832586538467097L;
	private WMSLayer displayLayer;
	
	public void setDisplayLayer(WMSLayer displayLayer) {
		this.displayLayer = displayLayer;
	}
	
	public void displayMap() {
		CoordinateReferenceSystem myCRS = displayLayer.getCoordinateReferenceSystem();    
		MapContent mapcontent = new MapContent();
		mapcontent.setTitle("Cycle route analysis client application"); 
		mapcontent.addLayer(displayLayer);
		JMapFrame myFrame = new JMapFrame(mapcontent);
		JMapPane myPane = myFrame.getMapPane();
		ReferencedEnvelope outbounds = new ReferencedEnvelope(-77278, 45656, 6689116, 6734899, myCRS);
		myPane.setDisplayArea(outbounds);
		myFrame.enableStatusBar(true);
		myFrame.enableToolBar(true);
		myFrame.setExtendedState(myFrame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		myFrame.setVisible(true);
	}
}
