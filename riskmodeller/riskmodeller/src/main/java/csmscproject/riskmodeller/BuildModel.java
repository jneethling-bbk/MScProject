package csmscproject.riskmodeller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Function;
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
import org.apache.cxf.helpers.DOMUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

public class BuildModel {

	public static final String PROGRESS = "progress";
	private int progress = 0;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
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
	   
	public FeatureLayer getReferenceLayer(File file, String layerTitle) throws IOException {
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
		if (store == null) {
			throw new IOException();
		}
        SimpleFeatureSource featureSource = store.getFeatureSource();
		// Feature type of pollution reference grid must be polygons!       
        if (layerTitle.equals("PollutionGrid") && !isPolygon(featureSource)) {
        	throw new IOException();
        }
		// Feature type of road reference network must be lines!       
        if (layerTitle.equals("RoadNetwork") && !isLine(featureSource)) {
        	throw new IOException();
        }
        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        FeatureLayer layer = new FeatureLayer(featureSource, style);
        return layer;
	}
	
	private boolean isPolygon(SimpleFeatureSource featureSource) {
        Class<?> geomBinding = featureSource.getSchema().getGeometryDescriptor().getType().getBinding();
        boolean isPolygon = geomBinding != null 
                && (Polygon.class.isAssignableFrom(geomBinding) ||
                    MultiPolygon.class.isAssignableFrom(geomBinding));
        return isPolygon;
	}
	
	private boolean isLine(SimpleFeatureSource featureSource) {
        Class<?> geomBinding = featureSource.getSchema().getGeometryDescriptor().getType().getBinding();
        boolean isLine = geomBinding != null 
                && (LineString.class.isAssignableFrom(geomBinding) ||
                    MultiLineString.class.isAssignableFrom(geomBinding));
        return isLine;
	}
	
	public void buildPollutionModel(FeatureLayer pollutionReferenceGrid, File newFile) throws NoSuchAuthorityCodeException, FactoryException, IOException, SAXException, ParserConfigurationException, MismatchedDimensionException, TransformException {
		
		//Set up some resources
		int counter = 0;		
		CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        GeodeticCalculator gc = new GeodeticCalculator(crs);
		CoordinateReferenceSystem wmsCRS = CRS.decode("EPSG:3857");
		MathTransform mercatorToGeographic = CRS.findMathTransform(wmsCRS, crs, true);
		
		//Create the feature builder for points representing pollution measurement locations
		Map<String, Class<?>> pollutionAttributes = new HashMap<>();
		pollutionAttributes.put("name", String.class);
		pollutionAttributes.put("pollution", Double.class);		
		CustomFeatureTypeBuilder pollutionBuilder = new CustomFeatureTypeBuilderImpl();
		pollutionBuilder.setName("Sites");
		pollutionBuilder.setAttributes(pollutionAttributes);
		pollutionBuilder.setCRS(crs);
		pollutionBuilder.setGeometryType(Point.class);
		
		//Use the builder to get a feature type		
		final SimpleFeatureType SITES = pollutionBuilder.buildFeatureType();
        
		//Use feature type to set up a builder for the individual features 
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(SITES);
        
        //Set up an empty feature collection to hold point features once created
        DefaultFeatureCollection pointCollection = new DefaultFeatureCollection();
		
        //Get the data feed and create a DOM
		InputStream inputStream = null;
		String lAir = "http://api.erg.kcl.ac.uk/AirQuality/Hourly/MonitoringIndex/GroupName=London";
		URL url = new URL(lAir);
		URLConnection conn = url.openConnection();
		conn.connect();
		inputStream = conn.getInputStream();
		Document doc = DOMUtils.readXml(inputStream);
		inputStream.close();
		
		NodeList nList = doc.getElementsByTagName("Site");					
		
		//Iterate through the nodes
		for (int i=0; i<nList.getLength(); i++) {
			Node nNode = nList.item(i);
			
			//Use custom object to turn nodes into point features
			Node2Feature n2f = new Node2FeatureImpl();
			n2f.setNode(nNode);
			n2f.setFeatureBuilder(featureBuilder);
			SimpleFeature feature = n2f.getFeature();
			
			//Add the point to the collection
			pointCollection.add(feature);
    		counter++;
    		setProgress(counter/250);   	
        }
		
		//Set up an empty temporary feature collection to hold new polygon features once created
		DefaultFeatureCollection tempPolyCollection = new DefaultFeatureCollection();
        
		//Get the feature collection of input polygons and make an iterator
		FeatureCollection<?, ?> inputPolyCollection = pollutionReferenceGrid.getFeatureSource().getFeatures();
        SimpleFeatureIterator inputPolyIterator = (SimpleFeatureIterator) inputPolyCollection.features();
        
	    try {
	        while(inputPolyIterator.hasNext()) {        	
	        	
	        	//Prepare the polygon feature
	        	SimpleFeature polyFeature = inputPolyIterator.next();
        		MultiPolygon poly = (MultiPolygon) polyFeature.getDefaultGeometry();
        		Point interpoint = poly.getCentroid();
        		Geometry transformedPoint = JTS.transform(interpoint, mercatorToGeographic);
        		gc.setStartingPosition(JTS.toDirectPosition(transformedPoint.getCoordinate(), crs));

        		//Use custom made object to calculate interpolated values
        		IDWinterpolation idw = new IDWinterpolationImpl();
        		idw.setCalculator(gc);
        		idw.setPointCollection(pointCollection);
        		double interpolation = idw.interpolate();
        		
        		//Write interpolated values back to the feature attribute
        		polyFeature.setAttribute("value", interpolation);
        		tempPolyCollection.add(polyFeature);
        		counter++;
        		setProgress(counter/250);
	        }
	    } finally {
	    	inputPolyIterator.close();
	    }
	    
	    //Calculate cut-off point for inclusion in model
	    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
	    Function sum = ff.function("Collection_Max", ff.property("value"));
	    Object value = sum.evaluate(tempPolyCollection);
	    double max = (double) value;
	    double topSlice = max - (max/3);
	    
	    //Set up an output collection and an iterator
	    DefaultFeatureCollection outputPolyCollection = new DefaultFeatureCollection();
	    SimpleFeatureIterator outputPolyIterator = (SimpleFeatureIterator) tempPolyCollection.features();
	    try {
	        while(outputPolyIterator.hasNext()) {
	        	SimpleFeature newPolyFeature = outputPolyIterator.next();
	        	
	        	//Evaluate features and include if appropriate
	        	if ((Double) newPolyFeature.getAttribute("value") > topSlice) {
	        		outputPolyCollection.add(newPolyFeature);
	        	}
        		counter++;
        		setProgress(counter/250);
	        }
	    } finally {
	    	outputPolyIterator.close();
	    }
        
	    //Copy the schema from the reference grid and save the output
	    SimpleFeatureType TYPE = (SimpleFeatureType) pollutionReferenceGrid.getFeatureSource().getSchema();
	    saveShapefile(newFile, TYPE, outputPolyCollection);
	    
	}
	
	public void buildTrafficModel(FeatureLayer trafficReferenceNetwork, File inputFile, File newFile) throws IOException, SchemaException, NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException {
		
		//Set up some resources
		int counter = 0;
		CoordinateReferenceSystem crs = CRS.decode("EPSG:4326", true);
    	CoordinateReferenceSystem wmsCRS = CRS.decode("EPSG:3857");    	
    	MathTransform transform = CRS.findMathTransform(crs, wmsCRS, true);
    	GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
    	
    	//Create the feature builder for points representing accident locations	
		CustomFeatureTypeBuilder accidentBuilder = new CustomFeatureTypeBuilderImpl();
		accidentBuilder.setName("Accidentpoints");
		accidentBuilder.setCRS(crs);
		accidentBuilder.setGeometryType(Point.class);
		
		//Use the builder to get a feature type	
		final SimpleFeatureType ACCPOINTS = accidentBuilder.buildFeatureType();      
        
		//Use feature type to set up a builder for the individual features
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(ACCPOINTS);
    	
        //Set up an empty feature collection to hold point features once created
        DefaultFeatureCollection pointCollection = new DefaultFeatureCollection();
        
        Coordinate[] tempPoints = new Coordinate[1000000];
        int pointsInitCount = 0;
        
        // Get the point data
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        try {
            // First line of the data file is the header
            String line = reader.readLine();

            int i = 0;
            for (line = reader.readLine(); line != null; line = reader.readLine()) {
        		counter++;
        		setProgress(counter/3600);
        		// skip blank lines
        		if (line.trim().length() > 0) {
        				
        			//Use custom made object to turn line of CSV text into a coordinate
        			CSV2Coordinate csv2c = new CSV2CoordinateImpl();
        			csv2c.setLine(line);
        			Coordinate c = csv2c.getCoordinate();
        			if (c != null) {
        				tempPoints[i] = c;
                        pointsInitCount++;
                        i++;
        			}
                }
            }
        } finally {
            reader.close();
        }
        
        //Make a final array of coordinates for the valid accidents
        Coordinate[] points = clean(tempPoints, pointsInitCount);
        final int NUM_POINTS = points.length;
        
        //Get the line features of the roads and set up an iterator
        FeatureSource<?, ?> source = trafficReferenceNetwork.getFeatureSource();
        FeatureCollection<?, ?> features = source.getFeatures();
        SimpleFeatureIterator lineIterator = (SimpleFeatureIterator) features.features();
        
        //Place the lines in a spatial index
        final SpatialIndex index = new STRtree();                
        try {
        	while (lineIterator.hasNext()) { 
        		SimpleFeature lineFeature = lineIterator.next();
        		Geometry geom = (MultiLineString) lineFeature.getDefaultGeometry();
        		if (geom != null) {
        			Envelope env = geom.getEnvelopeInternal();
        			if (!env.isNull()) {
        				index.insert(env, new LocationIndexedLine(geom));
        			}
        		}
        		counter++;
        		// Depends on number of features in network data set
        		setProgress(counter/3600);	
        	}
        } finally {
            	lineIterator.close();
        }
        
        //The maximum distance that a line can be from a point to be a candidate for snapping
        //is defined as 1% of the total width of the feature bounds
        ReferencedEnvelope bounds = features.getBounds();     
        final double MAX_SEARCH_DISTANCE = bounds.getSpan(0) / 100.0;

        int pointsProcessed = 0;
        while (pointsProcessed < NUM_POINTS) {
            
        	//Get point and create search envelope
            Coordinate pt = points[pointsProcessed++];
            Envelope search = new Envelope(pt);
            search.expandBy(MAX_SEARCH_DISTANCE);

            //Query the spatial index for objects within the search envelope.
            List<LocationIndexedLine> lines = index.query(search);

            //Initialise the minimum distance found to the maximum acceptable distance plus a little bit
            double minDist = MAX_SEARCH_DISTANCE + 1.0e-6;
            Coordinate minDistPoint = null;

            for (LocationIndexedLine line : lines) {
            	
            	//Use custom made objects to snap the point to a line
                Snap firstSnap = new SnapImpl();
                firstSnap.setMinDist(minDist);
                firstSnap.setMinDistPoint(minDistPoint);
            	Snap2Line s2l = new Snap2LineImpl();
            	s2l.setLine(line);
            	s2l.setPoint(pt);
            	s2l.setPrevious(firstSnap);
            	Snap returnedSnap = s2l.snap();
            	minDist = returnedSnap.getMinDist();
            	minDistPoint = returnedSnap.getMinDistPoint();
            }
            
            if (minDistPoint != null) {
                Point point = geometryFactory.createPoint(minDistPoint);
                featureBuilder.add(point);
                SimpleFeature feature = featureBuilder.buildFeature(null);
                pointCollection.add(feature);
            }
    		counter++;
    		setProgress(counter/3600);
        }
        
        //Create the feature builder for polygons representing accident buffers
		CustomFeatureTypeBuilder bufferTypeBuilder = new CustomFeatureTypeBuilderImpl();
		bufferTypeBuilder.setName("Accidentbuffers");
		bufferTypeBuilder.setCRS(wmsCRS);
		bufferTypeBuilder.setGeometryType(MultiPolygon.class);
		
		//Use the builder to get a feature type	
		final SimpleFeatureType ACCBUFFERS = bufferTypeBuilder.buildFeatureType();
        
		//Use feature type to set up a builder for the individual features 
        SimpleFeatureBuilder bufferBuilder = new SimpleFeatureBuilder(ACCBUFFERS);
        
        //Set up an empty feature collection to hold buffer features once created
        DefaultFeatureCollection bufferCollection = new DefaultFeatureCollection();
        
        SimpleFeatureIterator pointIterator = pointCollection.features();
        try {
        	while (pointIterator.hasNext()) {        		
        		SimpleFeature pointFeature = pointIterator.next();
        		Point inputPoint = (Point) pointFeature.getDefaultGeometry();
        		Geometry transformedPoint = JTS.transform(inputPoint, transform);
        		Geometry buffer = transformedPoint.buffer(75.0);
        		bufferBuilder.add(buffer);
        		SimpleFeature feature = bufferBuilder.buildFeature(null);
        		bufferCollection.add(feature);
        		counter++;
        		setProgress(counter/3600);
        	}
        } finally {
        	pointIterator.close();
        }
        
        saveShapefile(newFile, ACCBUFFERS, bufferCollection);
        
	}
	
    private Coordinate[] clean(final Coordinate[] v, int pointsInitCount) {
    	Coordinate[] myArray = new Coordinate[pointsInitCount];
    	for (int i=0; i < pointsInitCount; i++) {
    		myArray[i] = v[i];
    	}
    	return myArray;
    }
    
    private void saveShapefile(File newFile, SimpleFeatureType type, DefaultFeatureCollection features) throws IOException {
        
    	ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", newFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);

        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);

        //TYPE is used as a template to describe the file contents
        newDataStore.createSchema(type);
        
        //Write the features to the shapefile
        Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

            featureStore.setTransaction(transaction);
            try {
                featureStore.addFeatures(features);
                transaction.commit();
            } catch (Exception problem) {
                problem.printStackTrace();
                transaction.rollback();
            } finally {
                transaction.close();
            }
        } else {
        	transaction.close();
        	throw new IOException();
        }
    }
}
