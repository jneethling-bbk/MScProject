package csmscproject.routemapper;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
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
import org.geotools.styling.SLDParser;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.dialog.JExceptionReporter;
import org.geotools.swing.styling.JSimpleStyleDialog;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class MapModel {
	
    private static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
    private static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();
    
    private Document routeDoc;
    private CoordinateReferenceSystem displayCRS;
    private CoordinateReferenceSystem computationCRS;
    
    public WMSLayer getBackdrop() throws ServiceException, IOException, NullPointerException {
		String wmsUrlString = "http://ows.terrestris.de/osm/service?Service=WMS&Version=1.1.1&Request=GetCapabilities";
		String wmsLayerName = "OSM-WMS";
		//String wmsUrlString = "http://129.206.228.72/cached/osm?Service=WMS&Version=1.1.1&Request=GetCapabilities";
		//String wmsLayerName = "osm_auto:all";
				
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
        displayCRS = displayLayer.getCoordinateReferenceSystem();
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

		computationCRS = CRS.decode("EPSG:4326");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        routeDoc = dBuilder.parse(file);
        routeDoc.getDocumentElement().normalize();
        NodeList nList = routeDoc.getElementsByTagName("gx:coord");
		if (nList.getLength() < 2) {
			// Maybe create a custom exception
			throw new IOException();
		}
        Coordinate[] coords = new Coordinate[nList.getLength()];
        for (int i=0; i<nList.getLength(); i++) {
        	Node nNode = nList.item(i);
        	String raw = nNode.getTextContent();
        	String[] arr = raw.split(" ");
        	Coordinate c = new Coordinate(Double.parseDouble(arr[1]), Double.parseDouble(arr[0]));
        	coords[i] = c;
        }
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        LineString line = geometryFactory.createLineString(coords);
        MathTransform transform = CRS.findMathTransform(computationCRS, displayCRS, true);
        Geometry transformedLine = JTS.transform(line, transform);
        
        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
        //set the name first
        b.setName("Route");
        //then add the geometry properties
        b.setCRS(displayCRS); // set crs first
        b.add("the_geom", MultiLineString.class); // then add geometry
        //then build the type
        final SimpleFeatureType ROUTE = b.buildFeatureType();
        
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(ROUTE);
        featureBuilder.add(transformedLine);
        SimpleFeature feature = featureBuilder.buildFeature(null);
        DefaultFeatureCollection lineCollection = new DefaultFeatureCollection();
        lineCollection.add(feature);
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
		//Geometry pollutedPart = null;

	    SimpleFeatureIterator lineIterator = (SimpleFeatureIterator) lineCollection.features();
	    
	    double polDist = 0.0;
        MathTransform transform = CRS.findMathTransform(displayCRS, computationCRS, true);        
        GeodeticCalculator gc = new GeodeticCalculator(computationCRS);
	    
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
	            				polDist = polDist + getLineStringLength((LineString) pollutedPart);
	            			}
	            		}
	            		
	            		//Geometry pollutedPart = lines.intersection(polys);
	            	}	            	
	            } finally {
	            	polyIterator.close();
	            }
	        }
	    } finally {
	        lineIterator.close();
	    }
//        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
//        //set the name
//        b.setName("Route");
//        //add a geometry property
//        b.setCRS(displayCRS); // set crs first
//        b.add("the_geom", MultiLineString.class); // then add geometry
//        //build the type
//        final SimpleFeatureType ROUTE = b.buildFeatureType();       
//        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(ROUTE);
//        featureBuilder.add(pollutedPart);
//        SimpleFeature feature = featureBuilder.buildFeature(null);
//        DefaultFeatureCollection lineCollection1 = new DefaultFeatureCollection();
//        lineCollection1.add(feature);
//        
//        FeatureLayer nl = new FeatureLayer(lineCollection1, createLineStyle(Color.GRAY));
        //double polDist = getRouteLen(nl);
        double totDist = getRouteLen(userRouteLayer);
        val = (int) ((polDist/totDist) *100);
    	return val;
    }

	public double getRouteLen(FeatureLayer userRouteLayer) throws NoSuchAuthorityCodeException, FactoryException, IOException, MismatchedDimensionException, TransformException {
		double val = 0.0;
		FeatureCollection<?, ?> lineCollection = userRouteLayer.getFeatureSource().getFeatures();
		
        MathTransform transform = CRS.findMathTransform(displayCRS, computationCRS, true);        
        GeodeticCalculator gc = new GeodeticCalculator(computationCRS);

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
		            
		            Geometry startPTransformed = JTS.transform(startP, transform);
		            Geometry endPTransformed = JTS.transform(endP, transform);
		        	Coordinate start = startPTransformed.getCoordinate();
		        	Coordinate end = endPTransformed.getCoordinate();
		        	gc.setStartingPosition( JTS.toDirectPosition(start, computationCRS));
		        	gc.setDestinationPosition( JTS.toDirectPosition(end, computationCRS));
		                	    
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
	
	public double getSlope(FeatureLayer userRouteLayer) throws IOException, MismatchedDimensionException, NoSuchAuthorityCodeException, FactoryException, TransformException {
		NodeList nList = routeDoc.getElementsByTagName("gx:coord");
		if (nList.getLength() < 2) {
			// Maybe create a custom exception
			throw new IOException();
		}
        
		Node first = nList.item(0);
        String firstRaw = first.getTextContent();
    	String[] firstArr = firstRaw.split(" ");
    	double startHeight = Double.parseDouble(firstArr[2]);
        
        Node last = nList.item(nList.getLength()-1);
        String lastRaw = last.getTextContent();
    	String[] lastArr = lastRaw.split(" ");
    	double endHeight = Double.parseDouble(lastArr[2]);
        
    	double val = ((endHeight-startHeight)/getRouteLen(userRouteLayer)) * 100;        
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
	
    /**
     * Create a Style to display the features. If an SLD file is in the same
     * directory as the shapefile then we will create the Style by processing
     * this. Otherwise we display a JSimpleStyleDialog to prompt the user for
     * preferences.
     */
    
	//private Style createStyle(File file, FeatureSource featureSource) {
    private Style createStyle(File file) {
        File sld = toSLDFile(file);
        //if (sld != null) {
            return createFromSLD(sld);
        //}

        //SimpleFeatureType schema = (SimpleFeatureType)featureSource.getSchema();
        //return JSimpleStyleDialog.showDialog(null, schema);
    }
    
    /**
     * Figure out if a valid SLD file is available.
     */
    public File toSLDFile(File file)  {
        String path = file.getAbsolutePath();
        String base = path.substring(0,path.length()-4);
        String newPath = base + ".sld";
        File sld = new File( newPath );
        if( sld.exists() ){
            return sld;
        }
        newPath = base + ".SLD";
        sld = new File( newPath );
        if( sld.exists() ){
            return sld;
        }
        return null;
    }

    /**
     * Create a Style object from a definition in a SLD document
     */
    private Style createFromSLD(File sld) {
        try {
            SLDParser stylereader = new SLDParser(styleFactory, sld.toURI().toURL());
            Style[] style = stylereader.readXML();
            return style[0];
            
        } catch (Exception e) {
            JExceptionReporter.showDialog(e, "Problem creating style");
        }
        return null;
    }
}
