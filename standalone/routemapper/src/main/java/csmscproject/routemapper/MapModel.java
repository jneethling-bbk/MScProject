package csmscproject.routemapper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wms.WebMapServer;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
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
//import org.opengis.geometry.coordinate.LineSegment;
//import org.opengis.geometry.coordinate.LineString;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class MapModel {
	
	public static final String PROGRESS = "progress";
	private int progress = 0;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
    private static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
    private static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
    
    private CoordinateReferenceSystem displayCRS;
    private CoordinateReferenceSystem computationCRS;
    private int numberOfSegments;
    private int counter;
    
	public void setProgress(int progress) {
		int oldProgress = this.progress;
		this.progress = progress;

		PropertyChangeEvent evt = new PropertyChangeEvent(this, PROGRESS, oldProgress, progress);
		pcs.firePropertyChange(evt);
	}
	
	public void reset() {
		setProgress(0);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
			pcs.addPropertyChangeListener(listener);
	}
    
    public List<String> getServers() {
		String wmsUrlString1 = "http://ows.terrestris.de/osm/service?Service=WMS&Version=1.1.1&Request=GetCapabilities";
		String wmsUrlString2 = "http://129.206.228.72/cached/osm?Service=WMS&Version=1.1.1&Request=GetCapabilities";
		String wmsUrlString3 = "http://osm-demo.wheregroup.com/service?Service=WMS&Version=1.1.1&Request=GetCapabilities";
		List<String> servers = new ArrayList<String>();
		servers.add(wmsUrlString1);
		servers.add(wmsUrlString2);
		servers.add(wmsUrlString3);
		return servers;
    }
    
    public WebMapServer getWMS(URL capabilitiesURL) throws ServiceException, IOException {
    	if (capabilitiesURL == null) {
    		throw new NullPointerException();
    	}
    	WebMapServer wms = new WebMapServer(capabilitiesURL);
    	return wms;
    }
    
    public WMSLayer getBackdrop(WebMapServer wms, org.geotools.data.ows.Layer myLayer) {
        WMSLayer displayLayer = new WMSLayer(wms, myLayer );
        displayCRS = displayLayer.getCoordinateReferenceSystem();
        displayLayer.setTitle("Backdrop");
		return displayLayer;
	}
    
    public GridCoverage2D getDEM(File file) throws IOException, NoSuchAuthorityCodeException, FactoryException {
    	AbstractGridFormat format = GridFormatFinder.findFormat(file);
    	GridCoverage2DReader reader = null;
    	try {
    		reader = format.getReader(file);
    	} catch (UnsupportedOperationException e) {
    		throw new IOException();
    	}
    	GridCoverage2D coverage = reader.read(null);
    	return coverage;
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
        	// Maybe create a custom exception
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
	
	public FeatureLayer getRouteLayer(File file, String layerTitle) throws IOException, ParserConfigurationException, SAXException, FactoryException, MismatchedDimensionException, TransformException {
		counter = 0;
		computationCRS = CRS.decode("EPSG:4326");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document routeDoc = dBuilder.parse(file);
		
		KML2Line k2l = new KML2LineImpl();
		k2l.setDOM(routeDoc);
		k2l.setCRS(displayCRS);
		DefaultFeatureCollection lineCollection = k2l.getLine();
		numberOfSegments = k2l.getSegmentCount();
        FeatureLayer layer = new FeatureLayer(lineCollection, createLineStyle(Color.DARK_GRAY));
        layer.setTitle(layerTitle);
        return layer;
	}
	
	public void resetRouteStyle(FeatureLayer routeLayer, RouteReport report, RiskAppetite appetite) {
		Color color;
		if(report.getAccidentCount() > appetite.getMaxAccidentCount() || report.getPollutionPercentage() > appetite.getMaxPollutionPercentage()) {
			color = Color.RED;
		} else {
			color = Color.GREEN;
		}
		routeLayer.setStyle(createLineStyle(color));
	}
	
	//Create a Style to draw the line features of the user route  
    private Style createLineStyle(Color color) {
        Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(color),
                filterFactory.literal(3));

        LineSymbolizer sym = styleFactory.createLineSymbolizer(stroke, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }
    
    public int getNumIntersects(FeatureLayer userRouteLayer, FeatureLayer riskLayer) throws IOException {
    	int val = 0;
		FeatureCollection<?, ?> lineCollection = userRouteLayer.getFeatureSource().getFeatures();
		FeatureCollection<?, ?> polyCollection = riskLayer.getFeatureSource().getFeatures();

		Set<String> polyIds = new HashSet<String>();
		
	    SimpleFeatureIterator lineIterator = (SimpleFeatureIterator) lineCollection.features();
	    
	    try {
	        while( lineIterator.hasNext() ){        	
	        	SimpleFeature lineFeature = lineIterator.next();
	            MultiLineString lines = (MultiLineString) lineFeature.getDefaultGeometry();
	            SimpleFeatureIterator polyIterator = (SimpleFeatureIterator) polyCollection.features();
	            try {
	            	while (polyIterator.hasNext()) {
	            		SimpleFeature polyFeature = polyIterator.next();
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
	    		counter++;
	    		setProgress(counter/(numberOfSegments/20));
	        }
	    } finally {
	        lineIterator.close();
	    }
    	val = polyIds.size();
    	return val;
    }
    
    public int getPollutedPercentage(FeatureLayer userRouteLayer, FeatureLayer riskLayer) throws NoSuchAuthorityCodeException, FactoryException, IOException, MismatchedDimensionException, TransformException {
    	int val = 0;
		FeatureCollection<?, ?> lineCollection = userRouteLayer.getFeatureSource().getFeatures();
		FeatureCollection<?, ?> polyCollection = riskLayer.getFeatureSource().getFeatures();
	    SimpleFeatureIterator lineIterator = (SimpleFeatureIterator) lineCollection.features();    
	    double polDist = 0.0;
	    
	    try {
	        while( lineIterator.hasNext() ){
	        	SimpleFeatureIterator polyIterator = (SimpleFeatureIterator) polyCollection.features();
	        	SimpleFeature lineFeature = lineIterator.next();
	            MultiLineString lines = (MultiLineString) lineFeature.getDefaultGeometry();

	            try {
	            	while (polyIterator.hasNext()) {
	            		SimpleFeature polyFeature = polyIterator.next();
	            		MultiPolygon polys = (MultiPolygon) polyFeature.getDefaultGeometry();
	            		
	            		int np = polys.getNumGeometries();
	            		Polygon polyArray[] = new Polygon[np];	            		
	            		for ( int j = 0; j < np; j++ ) {
	            			polyArray[j] = (Polygon) polys.getGeometryN(j);
	            			if (lines.intersects(polyArray[j])) {
	            				Geometry pollutedPart = lines.intersection(polyArray[j]);
	            				int nl = pollutedPart.getNumGeometries();
	            				LineString lineArray[] = new LineString[nl];
	            				for (int k=0; k<nl; k++) {
	            					lineArray[k] = (LineString) pollutedPart.getGeometryN(k);
	            					polDist = polDist + getLineStringLength(lineArray[k]);
	            				}
	            			}
	            		}
	            		
	            	}	            	
	            } finally {
	            	polyIterator.close();
	            }
	    		counter++;
	    		setProgress(counter/(numberOfSegments/20));
	        }
	    } finally {
	        lineIterator.close();
	    }

        double totDist = getRouteLen(userRouteLayer);
        val = (int) ((polDist/totDist) *100);
    	return val;
    }

	public double getRouteLen(FeatureLayer userRouteLayer) throws NoSuchAuthorityCodeException, FactoryException, IOException, MismatchedDimensionException, TransformException {
		counter = 0;
		double val = 0.0;
		FeatureCollection<?, ?> lineCollection = userRouteLayer.getFeatureSource().getFeatures();
		
        MathTransform transform = CRS.findMathTransform(displayCRS, computationCRS, true);        
        GeodeticCalculator gc = new GeodeticCalculator(computationCRS);

	    SimpleFeatureIterator iterator = (SimpleFeatureIterator) lineCollection.features();
	    
	    try {
	        while(iterator.hasNext()){
	            SimpleFeature feature = iterator.next();
	            
	            
	            MultiLineString geom = (MultiLineString) feature.getDefaultGeometry();
	            
	            int n = geom.getNumGeometries();
	            LineString lines[] = new LineString[n];
	            for ( int i = 0; i < n; i++ ) {
	            	lines[ i ] = (LineString) geom.getGeometryN( i );
	            	
	            	Point startP = lines[i].getStartPoint();
		            Point endP = lines[i].getEndPoint();
		            
		            Geometry startPTransformed = JTS.transform(startP, transform);
		            Geometry endPTransformed = JTS.transform(endP, transform);
		        	Coordinate start = startPTransformed.getCoordinate();
		        	Coordinate end = endPTransformed.getCoordinate();
		        	gc.setStartingPosition( JTS.toDirectPosition(start, computationCRS));
		        	gc.setDestinationPosition( JTS.toDirectPosition(end, computationCRS));
		                	    
	        	    double distance = gc.getOrthodromicDistance();
	        	    val = val + distance;	        	    
	            }
	    		counter++;
	    		setProgress(counter/(numberOfSegments/20));
	        }
	    } finally {
	        iterator.close();
	    }
		return val;
	}
	
	public double getSlope(FeatureLayer userRouteLayer, GridCoverage2D dem) throws IOException, MismatchedDimensionException, NoSuchAuthorityCodeException, FactoryException, TransformException {
		MathTransform transform = CRS.findMathTransform(displayCRS, computationCRS, true);
		FeatureCollection<?, ?> lineCollection = userRouteLayer.getFeatureSource().getFeatures();
		SimpleFeatureIterator lineIterator = (SimpleFeatureIterator) lineCollection.features();
		SimpleFeature lineFeatureStart = lineIterator.next();
		MultiLineString mLineS = (MultiLineString) lineFeatureStart.getDefaultGeometry();
		LineString firstLine = (LineString) mLineS.getGeometryN(0);
		
		LineString lastLine = null;
		SimpleFeature lineFeatureEnd = null;
		try {
	        while(lineIterator.hasNext()) {
	        	lineFeatureEnd = lineIterator.next();

	        }
	    } finally {
	    	lineIterator.close();
	    }
		
    	MultiLineString mLineE = (MultiLineString) lineFeatureEnd.getDefaultGeometry();
        lastLine = (LineString) mLineE.getGeometryN(0);
		
	    Point startP = firstLine.getStartPoint();    		
	    Point endP = lastLine.getStartPoint();
	    Geometry startPTransformed = JTS.transform(startP, transform);
	    Geometry endPTransformed = JTS.transform(endP, transform);
	    Coordinate startC = startPTransformed.getCoordinate();
	    Coordinate endC = endPTransformed.getCoordinate();
	    
	    float[] startHeight = (float[]) (dem.evaluate(JTS.toDirectPosition(startC, computationCRS)));
	    float[] endHeight = (float[]) (dem.evaluate(JTS.toDirectPosition(endC, computationCRS)));
	    
    	double val = ((startHeight[0]-endHeight[0])/getRouteLen(userRouteLayer)) * 100;        
    	return val;
		
	}
	
	private double getLineStringLength(LineString line) throws FactoryException, MismatchedDimensionException, TransformException {
        MathTransform transform = CRS.findMathTransform(displayCRS, computationCRS, true);        
        GeodeticCalculator gc = new GeodeticCalculator(computationCRS);
        Point startP = line.getStartPoint();
        Point endP = line.getEndPoint();       
        Geometry startPTransformed = JTS.transform(startP, transform);
        Geometry endPTransformed = JTS.transform(endP, transform);
    	Coordinate start = startPTransformed.getCoordinate();
    	Coordinate end = endPTransformed.getCoordinate();
    	gc.setStartingPosition( JTS.toDirectPosition(start, computationCRS));
    	gc.setDestinationPosition( JTS.toDirectPosition(end, computationCRS));        
		return gc.getOrthodromicDistance();
	}
}
