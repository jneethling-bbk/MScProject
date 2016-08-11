package csmscproject.routemapper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.WMSLayer;
import org.geotools.ows.ServiceException;

public class MapController {

	private MapModel model;
	private MapView view;
	private FeatureLayer accidentLayer = null;
	private FeatureLayer pollutionLayer = null;
	private FeatureLayer userRouteLayer = null;
	private String userRouteFileName = null;
	int routeLength;
	double slope;
	int accidentCount;
	int pollutionPercentage;
	
	public MapController(MapModel model, MapView view) {
		this.model = model;
		this.view = view;
		this.view.addConnectAccidentListener(new BtnListener());
		this.view.addToggleAccidentListener(new BtnListener());
		this.view.addConnectPollutionListener(new BtnListener());
		this.view.addTogglePollutionListener(new BtnListener());
		this.view.addRiskAppetiteListener(new BtnListener());
		this.view.addUserRouteListener(new BtnListener());
		this.view.addEvaluateListener(new BtnListener());
		this.view.addZoomSAreaListener(new BtnListener());
	}
	
	public void configureMapView() throws ServiceException, IOException, NullPointerException {
		WMSLayer backdrop = model.getBackdrop();
		view.displayMap(backdrop);
	}
	
	class BtnListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {			
			String source = e.getActionCommand();
			if (source.equals("Connect traffic risk")) {
				File file = view.chooseFile();
				if (file == null) {return;}
				try {
					accidentLayer = model.getRiskLayer(file, "Accidents");
				} catch (IOException e1) {
					view.displayErrorMessage("Bad file: please try another...");
					return;
				}
				List<Layer> layerList = view.getLayerList();
				for (Layer l : layerList) {
					if (l.getTitle().equals("Accidents")) {
						view.removeLayer((FeatureLayer) l);
					}
				}
				view.addLayer(accidentLayer);
				view.zoomToLayer(accidentLayer);
				view.enableAccidentToggler();
				view.enableZoomBtn();
			} else if (source.equals("Connect pollution risk")) {
				File file = view.chooseFile();
				if (file == null) {return;}
				try {
					pollutionLayer = model.getRiskLayer(file, "Pollution");
				} catch (IOException e1) {
					view.displayErrorMessage("Bad file: please try another...");
					return;
				}
				List<Layer> layerList = view.getLayerList();
				for (Layer l : layerList) {
					if (l.getTitle().equals("Pollution")) {
						view.removeLayer((FeatureLayer) l);
					}
				}
				view.addLayer(pollutionLayer);
				view.enablePollutionToggler();
			} else if (source.equals("Toggle traffic risk")) {
				if (accidentLayer.isVisible()) {
					view.hideRiskLayer(accidentLayer);
				} else {
					view.showRiskLayer(accidentLayer);
				}
			} else if (source.equals("Toggle pollution risk")) {
				if (pollutionLayer.isVisible()) {
					view.hideRiskLayer(pollutionLayer);
				} else {
					view.showRiskLayer(pollutionLayer);
				}
			} else if (source.equals("Set risk appetite")) {
				view.setRiskAppetite();
			} else if (source.equals("Add user route")) {
				File file = view.chooseFile();
				if (file == null) {return;}
				userRouteFileName = file.getName();
				//routeFile = file;
				try {
					userRouteLayer = model.getRouteLayer(file, "Route");
				} catch (IOException e1) {
					view.displayErrorMessage("Bad file: please try another...");
					return;
				}
				List<Layer> layerList = view.getLayerList();
				for (Layer l : layerList) {
					if (l.getTitle().equals("Route")) {
						view.removeLayer((FeatureLayer) l);
					}
				}
				view.addLayer(userRouteLayer);
				view.enableEvaluateBtn();
			} else if (source.equals("Evaluate user route")) {				
				routeLength = (int) model.getRouteLen(userRouteLayer);
				slope = model.getSlope(userRouteLayer);
				accidentCount = model.getNumIntersects(userRouteLayer, accidentLayer);
				pollutionPercentage = model.getPollutedPercentage(userRouteLayer, pollutionLayer);
				//double routeLen = model.getRouteLen(userRouteLayer);
				//int roundedLength = (int)routeLen;
				//int v = model.getNumIntersects(userRouteLayer, accidentLayer);
				//int p = model.getPollutedPercentage(userRouteLayer, pollutionLayer);
				//double s = model.getSlope(userRouteLayer);
				view.zoomToLayer(userRouteLayer);
				model.resetRouteStyle(userRouteLayer);
				view.refreshMap();
				//view.enableReportBtn();
				//view.displayReport(userRouteFileName, roundedLength, v, p, s);
			} else if(source.equals("View report")) {
				view.displayReport(userRouteFileName, routeLength, accidentCount, pollutionPercentage, slope);
			} else if (source.equals("Zoom to study area")) {
				view.zoomToLayer(accidentLayer);
			}
		}
	}
}
