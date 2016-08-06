package csmscproject.routemapper;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.JTS;
import org.geotools.map.FeatureLayer;
import org.geotools.map.WMSLayer;
import org.geotools.ows.ServiceException;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class MapModel {
	
    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
	
	public WMSLayer getBackdrop() throws ServiceException, IOException, NullPointerException {
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
		
		// Feature type of risk layers must be polygons!
        Class<?> geomBinding = featureSource.getSchema().getGeometryDescriptor().getType().getBinding();
        boolean isPolygon = geomBinding != null 
                && (Polygon.class.isAssignableFrom(geomBinding) ||
                    MultiPolygon.class.isAssignableFrom(geomBinding));
        
        if (!isPolygon) {
            throw new IOException();
        }
		
		FeatureLayer layer = new FeatureLayer(featureSource, createPolygonStyle(color));
		layer.setTitle(layerTitle);
		return layer;
	}
	
    //Create a Style to draw the polygon features of the risk model    
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
	
	public FeatureLayer getRouteLayer(File file, String layerTitle) throws IOException {

		FileDataStore store = FileDataStoreFinder.getDataStore(file);
		if (store == null) {
			throw new IOException();
		}
		SimpleFeatureSource featureSource = store.getFeatureSource();
		
		// Feature type of the route layer must be lines!
        Class<?> geomBinding = featureSource.getSchema().getGeometryDescriptor().getType().getBinding();
        boolean isLine = geomBinding != null 
                && (LineString.class.isAssignableFrom(geomBinding) ||
                    MultiLineString.class.isAssignableFrom(geomBinding));
        
        if (!isLine) {
            throw new IOException();
        }
		
		FeatureLayer layer = new FeatureLayer(featureSource, createLineStyle());
		layer.setTitle(layerTitle);
		return layer;
	}
	
	//Create a Style to draw the line features of the user route  
    private Style createLineStyle() {
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.DARK_GRAY),
                filterFactory.literal(2));

        LineSymbolizer sym = styleFactory.createLineSymbolizer(stroke, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }
    public int getNumIntersects(FeatureLayer userRouteLayer, FeatureLayer riskLayer) {
    	int val = 0;
		FeatureCollection lineCollection = null;
		FeatureCollection polyCollection = null;
        //CoordinateReferenceSystem crs = null;
        
		try {
			lineCollection = userRouteLayer.getFeatureSource().getFeatures();
			polyCollection = riskLayer.getFeatureSource().getFeatures();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Set<String> polyIds = new HashSet<String>();
		
	    SimpleFeatureIterator lineIterator = (SimpleFeatureIterator) lineCollection.features();
	    
	    try {
	        while( lineIterator.hasNext() ){
	        	SimpleFeatureIterator polyIterator = (SimpleFeatureIterator) polyCollection.features();
	        	SimpleFeature lineFeature = lineIterator.next();
	        	//System.out.println(lineFeature.getID());
	            MultiLineString lines = (MultiLineString) lineFeature.getDefaultGeometry();

	            try {
	            	while (polyIterator.hasNext()) {
	            		SimpleFeature polyFeature = polyIterator.next();
	            		//System.out.println(polyFeature.getID());
	            		MultiPolygon polys = (MultiPolygon) polyFeature.getDefaultGeometry();
	            		int np = polys.getNumGeometries();
	            		Polygon polyArray[] = new Polygon[np];
	            		
	            		for ( int j = 0; j < np; j++ ) {
	            			polyArray[j] = (Polygon) polys.getGeometryN(j);
	            			if (lines.intersects(polyArray[j])) {
	            				polyIds.add(polyFeature.getID());
	            			}
	            		}
	            	}
	            	
	            	} finally {
	            		polyIterator.close();
	            }
	    }
	            
	            //val = val + lines.getNumPoints();
	            //Geometry test = lines.intersection(arg0);

	    } finally {
	        lineIterator.close();
	    }
    	val = polyIds.size();
    	for (String s : polyIds) {
    		System.out.println(s);
    	}
    	return val;
    }

	public double getRouteLen(FeatureLayer userRouteLayer) {
		double val = 0.0;
		FeatureCollection lineCollection = null;
        CoordinateReferenceSystem mapcrs = null;
        CoordinateReferenceSystem measurecrs = null;
        MathTransform transform = null;
        Geometry startPTransformed = null;
        Geometry endPTransformed = null;
        
		try {
			mapcrs = CRS.decode("EPSG:3857");
			measurecrs = CRS.decode("EPSG:4326");
			//measurecrs = CRS.decode("EPSG:27700");
			transform = CRS.findMathTransform(mapcrs, measurecrs, true);
		} catch (NoSuchAuthorityCodeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FactoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        GeodeticCalculator gc = new GeodeticCalculator(measurecrs);
		try {
			lineCollection = userRouteLayer.getFeatureSource().getFeatures();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    SimpleFeatureIterator iterator = (SimpleFeatureIterator) lineCollection.features();
	    try {
	        while( iterator.hasNext() ){
	            SimpleFeature feature = iterator.next();
	            MultiLineString geom = (MultiLineString) feature.getDefaultGeometry();
	            int n = geom.getNumGeometries();
	            LineString lines[] = new LineString[n];
	            for ( int i = 0; i < n; i++ ) {
	            	lines[ i ] = (LineString) geom.getGeometryN( i );
		            Point startP = lines[i].getStartPoint();
		            Point endP = lines[i].getEndPoint();
		            
		            try {
						startPTransformed = JTS.transform(startP, transform);
						endPTransformed = JTS.transform(endP, transform);
		        	    Coordinate start = startPTransformed.getCoordinate();
		        	    Coordinate end = endPTransformed.getCoordinate();
		        	    gc.setStartingPosition( JTS.toDirectPosition( start, measurecrs ) );
		        	    gc.setDestinationPosition( JTS.toDirectPosition( end, measurecrs ) );
		        	    
					} catch (MismatchedDimensionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (TransformException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		                	    
	        	    double distance = gc.getOrthodromicDistance();
	        	    val = val + distance;
	        	    
	            }
   	    
	        }
	    }
	    finally {
	        iterator.close();
	    }
		return val;
	}

}
