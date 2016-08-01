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
	
	public MapController(MapModel model, MapView view) {
		this.model = model;
		this.view = view;
		this.view.addConnectAccidentListener(new BtnListener());
		this.view.addToggleAccidentListener(new BtnListener());
		this.view.addConnectPollutionListener(new BtnListener());
		this.view.addTogglePollutionListener(new BtnListener());
	}
	
	public void DisplayMap() {
		WMSLayer backdrop = null;
		try {
			backdrop = model.getBackdrop();
		} catch (ServiceException e) {
			view.displayErrorMessage("Problem with prefered web map server");
			System.exit(0);
		} catch (IOException e) {
			view.displayErrorMessage("Problem with prefered web map server");
			System.exit(0);
		}
		view.setBackdrop(backdrop);
		view.displayMap();
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
						view.removeRiskLayer((FeatureLayer) l);
					}
				}
				view.addRiskLayer(accidentLayer);
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
						view.removeRiskLayer((FeatureLayer) l);
					}
				}
				view.addRiskLayer(pollutionLayer);
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
			}
		}
	}
}
