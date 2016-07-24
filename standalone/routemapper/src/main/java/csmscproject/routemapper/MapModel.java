package csmscproject.routemapper;

import java.io.IOException;
import java.net.URL;

import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.map.WMSLayer;
import org.geotools.ows.ServiceException;

public class MapModel {

	private WMSLayer displayLayer;
	
	public WMSLayer getBackdrop() throws ServiceException, IOException {
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
        displayLayer = new WMSLayer(wms, myLayer );
		return displayLayer;
	}
}
