package csmscproject.riskmodeller;

import java.io.File;
import java.io.IOException;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class BuildModel {

	public FeatureLayer getReferenceLayer(File file, String layerTitle) throws IOException {
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
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
	
	public void buildPollutionModel(FeatureLayer pollutionReferenceGrid, File outputFile) {
		
	}
	
}
