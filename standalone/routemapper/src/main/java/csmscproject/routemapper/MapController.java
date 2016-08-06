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
	FeatureLayer accidentLayer = null;
	FeatureLayer pollutionLayer = null;
	FeatureLayer userRouteLayer = null;
	
	public MapController(MapModel model, MapView view) {
		this.model = model;
		this.view = view;
		this.view.addConnectAccidentListener(new BtnListener());
		this.view.addToggleAccidentListener(new BtnListener());
		this.view.addConnectPollutionListener(new BtnListener());
		this.view.addTogglePollutionListener(new BtnListener());
		this.view.addUserRouteListener(new BtnListener());
		this.view.addEvaluateListener(new BtnListener());
	}
	
	public void configureMapView() throws ServiceException, IOException, NullPointerException {
		WMSLayer backdrop = model.getBackdrop();
		view.displayMap(backdrop);
	}
	
	class BtnListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {			
			String source = e.getActionCommand();
			if (source.equals("Connect accident data")) {
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
				view.enableAccidentToggler();
			} else if (source.equals("Connect pollution data")) {
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
			} else if (source.equals("Toggle accident data")) {
				if (accidentLayer.isVisible()) {
					view.hideRiskLayer(accidentLayer);
				} else {
					view.showRiskLayer(accidentLayer);
				}
			} else if (source.equals("Toggle pollution data")) {
				if (pollutionLayer.isVisible()) {
					view.hideRiskLayer(pollutionLayer);
				} else {
					view.showRiskLayer(pollutionLayer);
				}
			} else if (source.equals("Add user route")) {
				File file = view.chooseFile();
				if (file == null) {return;}
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
				view.zoomToLayer(userRouteLayer);
				double routeLen = model.getRouteLen(userRouteLayer);
				int roundedAnswer = (int)routeLen;
				int v = model.getNumIntersects(userRouteLayer, accidentLayer);
				view.displayErrorMessage("Length of route: " + roundedAnswer + " meters; Number of accident sites: " + v);
				
			}
		}
	}
}
