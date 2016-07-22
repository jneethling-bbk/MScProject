package csmscproject.routemapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.JFrame;

import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.WMSLayer;
import org.geotools.ows.ServiceException;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class App extends JFrame {
    
	private static final long serialVersionUID = -3073064282477474179L;

	public static void main( String[] args ) {
        App a = new App();
        try {
			a.run();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	private void run() throws ServiceException, IOException {
		String wmsUrlString = "http://ows.terrestris.de/osm/service?Service=WMS&Version=1.1.1&Request=GetCapabilities";
		String wmsLayerName = "OSM-WMS";
		
		URL url = new URL(wmsUrlString);
		
    	WebMapServer wms = new WebMapServer(url);            
        WMSCapabilities capabilities = wms.getCapabilities(); 
        Layer[] layers = WMSUtils.getNamedLayers(capabilities);
        Layer myLayer = null;
        for (Layer l : layers) {
        	if (l.getName().equals(wmsLayerName)) {
        		myLayer = l;
        	}
        }
        WMSLayer displayLayer = new WMSLayer(wms, myLayer );
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
