package csmscproject.riskmodeller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
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
		int counter = 0;
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		
		CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
        GeodeticCalculator gc = new GeodeticCalculator(crs);
		CoordinateReferenceSystem wmsCRS = CRS.decode("EPSG:3857");
		CoordinateReferenceSystem bng = CRS.decode("EPSG:27700");
		//MathTransform transform = CRS.findMathTransform(wmsCRS, crs, true);
		MathTransform mercatorToBNG = CRS.findMathTransform(wmsCRS, bng, true);
		MathTransform geographicToBNG = CRS.findMathTransform(crs, bng, true);
		
        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
        //set the name first
        b.setName("Sites");
        //add some properties
        b.add( "name", String.class );
        b.add( "pollution", Double.class );
        //then add the geometry properties
        b.setCRS(crs); // set crs first
        b.add("the_geom", Point.class); // then add geometry
        //then build the type
        final SimpleFeatureType SITES = b.buildFeatureType();
        
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(SITES);
        DefaultFeatureCollection pointCollection = new DefaultFeatureCollection();
		
		InputStream inputStream = null;
		String lAir = "http://api.erg.kcl.ac.uk/AirQuality/Hourly/MonitoringIndex/GroupName=London";
		URL url = new URL(lAir);
		URLConnection conn = url.openConnection();
		conn.connect();
		inputStream = conn.getInputStream();

		Document doc = Utils.readXml(inputStream);
		NodeList nList = doc.getElementsByTagName("Site");		
			
		for (int i=0; i<nList.getLength(); i++) {
			
			Node nNode = nList.item(i);
        	double lat = Double.parseDouble(nNode.getAttributes().getNamedItem("Latitude").getNodeValue());
        	double lon = Double.parseDouble(nNode.getAttributes().getNamedItem("Longitude").getNodeValue());
        	String sitename = nNode.getAttributes().getNamedItem("SiteName").getNodeValue();
        	
        	NodeList sList = nNode.getChildNodes();
        	
        	double total = 0.0;
        	int validMeasures = 0;
        	for (int j=0; j<sList.getLength(); j++) {
        		Node sNode = sList.item(j);
        		double reading = Double.parseDouble(sNode.getAttributes().getNamedItem("AirQualityIndex").getNodeValue());
        		if (reading > 0) {
        			total = total + reading;
        			validMeasures = validMeasures + 1;
        		}
        	}

        	double val = total/validMeasures;
        	Coordinate c = new Coordinate(lat, lon);
        	Point point = geometryFactory.createPoint(c);
        	featureBuilder.add(sitename);
        	featureBuilder.add(val);
        	featureBuilder.add(point);
        	SimpleFeature feature = featureBuilder.buildFeature(null);
        	pointCollection.add(feature);
    	
        }
		
		inputStream.close();
		//Style style = SLD.createSimpleStyle(SITES);
        //FeatureLayer pointlayer = new FeatureLayer(pointCollection, style);
        //pointlayer.setTitle("Sites");
        
		DefaultFeatureCollection tempPolyCollection = new DefaultFeatureCollection();
        FeatureCollection<?, ?> inputPolyCollection = pollutionReferenceGrid.getFeatureSource().getFeatures();
        SimpleFeatureIterator inputPolyIterator = (SimpleFeatureIterator) inputPolyCollection.features();
        
		double max = 0.0;
        
	    try {
	        while(inputPolyIterator.hasNext()) {        	
	        	SimpleFeature polyFeature = inputPolyIterator.next();
        		MultiPolygon poly = (MultiPolygon) polyFeature.getDefaultGeometry();
        		Point interpoint = poly.getCentroid();
        		Geometry transformedPoint = JTS.transform(interpoint, mercatorToBNG);
        		gc.setStartingPosition(JTS.toDirectPosition(transformedPoint.getCoordinate(), bng));
        		double numerator = 0.0;
        		double denominator = 0.0;
        		SimpleFeatureIterator pointIterator = (SimpleFeatureIterator) pointCollection.features();
        		try {
        			while(pointIterator.hasNext()) {
        				SimpleFeature pointFeature = pointIterator.next();
        				double pointval = (Double) pointFeature.getAttribute("pollution");
        				if (pointval > 0) {
        					Point valuepoint = (Point) pointFeature.getDefaultGeometry();
        					Geometry transformedValuePoint = JTS.transform(valuepoint, geographicToBNG);
        					gc.setDestinationPosition(JTS.toDirectPosition(transformedValuePoint.getCoordinate(), bng));
        					double distance = gc.getOrthodromicDistance();
        					if (distance < 5000) {
        						numerator = numerator + (pointval/Math.pow(distance, 2));
        						denominator = denominator + (1/Math.pow(distance, 2));
        					}
        				}
        			}
        		} finally {
        			pointIterator.close();
        		}
        		double interpolation = 0.0;
        		if (denominator > 0) {
        			interpolation = numerator/denominator;
        		}
        		polyFeature.setAttribute("value", interpolation);
        		if (interpolation > max) {
        			max = interpolation;
        		}
        		tempPolyCollection.add(polyFeature);
        		counter++;
        		setProgress(counter/250);
	        }
	    } finally {
	    	inputPolyIterator.close();
	    }
	    
	    double topSlice = max - (max/3);
	    DefaultFeatureCollection outputPolyCollection = new DefaultFeatureCollection();
	    SimpleFeatureIterator outputPolyIterator = (SimpleFeatureIterator) tempPolyCollection.features();
	    try {
	        while(outputPolyIterator.hasNext()) {
	        	SimpleFeature newPolyFeature = outputPolyIterator.next();
	        	if ((Double) newPolyFeature.getAttribute("value") > topSlice) {
	        		outputPolyCollection.add(newPolyFeature);
	        	}
        		counter++;
        		setProgress(counter/250);
	        }
	    } finally {
	    	outputPolyIterator.close();
	    }
        
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", newFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);

        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);

        
        newDataStore.createSchema((SimpleFeatureType) pollutionReferenceGrid.getFeatureSource().getSchema());
        /*
         * Write the features to the shapefile
         */
        Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
        SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();
        /*
         * The Shapefile format has a couple limitations:
         * - "the_geom" is always first, and used for the geometry attribute name
         * - "the_geom" must be of type Point, MultiPoint, MuiltiLineString, MultiPolygon
         * - Attribute names are limited in length 
         * - Not all data types are supported (example Timestamp represented as Date)
         * 
         * Each data store has different limitations so check the resulting SimpleFeatureType.
         */
        System.out.println("");
        System.out.println("SHAPE:"+SHAPE_TYPE);
        

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            /*
             * SimpleFeatureStore has a method to add features from a
             * SimpleFeatureCollection object, so we use the ListFeatureCollection
             * class to wrap our list of features.
             */
            //SimpleFeatureCollection collection = new ListFeatureCollection(ROUTE, features);
            featureStore.setTransaction(transaction);
            try {
                featureStore.addFeatures(outputPolyCollection);
                //featureStore.addFeatures(newPolyCollection);
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
	
	public void buildTrafficModel() {}
}
