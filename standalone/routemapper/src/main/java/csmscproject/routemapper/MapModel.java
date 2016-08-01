package csmscproject.routemapper;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.WMSLayer;
import org.geotools.ows.ServiceException;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory;

public class MapModel {
	
    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
	
	public WMSLayer getBackdrop() throws IOException, ServiceException {
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
        displayLayer.setTitle("Backdrop");
		return displayLayer;
	}
	
	
	public FeatureLayer getRiskLayer(File file, String layerTitle) throws IOException {
		Color color;
		if (layerTitle.equals("Accidents")) {
			color = Color.RED;
		} else {
			color = Color.ORANGE;
		}
		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		if (store == null) {
			throw new IOException();
		}
		SimpleFeatureSource featureSource = store.getFeatureSource();
		FeatureLayer layer = new FeatureLayer(featureSource, createPolygonStyle(color));
		layer.setTitle(layerTitle);
		return layer;
	}
	
    /**
     * Create a Style to draw polygon features with a thin red outline and
     * a red fill
     */
    private Style createPolygonStyle(Color color) {

        // create a partially opaque outline stroke
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(color),
                filterFactory.literal(0.1),
                filterFactory.literal(0.5));

        // create a partial opaque fill
        Fill fill = styleFactory.createFill(
                filterFactory.literal(color),
                filterFactory.literal(0.5));

        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }
}
