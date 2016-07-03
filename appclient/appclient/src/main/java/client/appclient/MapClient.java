package client.appclient;

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

import common.appcommon.AppServiceInterface;

//import server.appserver.AppServiceInterface;


public class MapClient extends JFrame {

	private static final long serialVersionUID = 5075090817334845128L;
	public static void main(String[] args) {
        MapClient mc = new MapClient();
        mc.run();
	}
    private void getServerError() {
		System.out.println("Connection to server failed...");
		System.out.println("The server may be down at this time, please try again later.");
	}
    private void run() {
        try {
        	Remote service = Naming.lookup("//127.0.0.1:1099/route_analysis");
        	AppServiceInterface appService = (AppServiceInterface) service;
        	//System.out.println(appService.getURLString());
        	URL url = new URL(appService.getURLString());
            WebMapServer wms = new WebMapServer(url);
            
            WMSCapabilities capabilities = wms.getCapabilities(); 
            Layer[] layers = WMSUtils.getNamedLayers(capabilities);
            Layer myLayer = null;
            for (Layer l : layers) {
            	if (l.getName().equals("OSM-WMS")) {
            		myLayer = l;
            	}
            }
            WMSLayer displayLayer = new WMSLayer(wms, myLayer );
            CoordinateReferenceSystem myCRS = displayLayer.getCoordinateReferenceSystem();
            
            MapContent mapcontent = new MapContent();
            mapcontent.setTitle("J Neethling: Birkbeck Computer Science MSc Project POC"); 
            mapcontent.addLayer(displayLayer);
            JMapFrame myFrame = new JMapFrame(mapcontent);
            JMapPane myPane = myFrame.getMapPane();
            ReferencedEnvelope outbounds = new ReferencedEnvelope(-77278, 45656, 6689116, 6734899, myCRS);
            myPane.setDisplayArea(outbounds);
            myFrame.enableStatusBar(true);
            myFrame.enableToolBar(true);
            myFrame.setExtendedState(myFrame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
            myFrame.setVisible(true);
        } catch (NotBoundException ex) {
            getServerError();
        } catch (MalformedURLException ex) {
            getServerError();
        } catch (RemoteException ex) {
            getServerError();
        } catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
