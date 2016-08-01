package csmscproject.routemapper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;

import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
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
		try {
			view.setBackdrop(model.getBackdrop());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		view.displayMap();
	}
	
	class BtnListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {			
			String source = e.getActionCommand();
			if (source.equals("Connect accident data")) {
				File file = view.chooseFile();
				try {
					accidentLayer = model.getRiskLayer(file, "Accidents");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
				try {
					pollutionLayer = model.getRiskLayer(file, "Pollution");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
