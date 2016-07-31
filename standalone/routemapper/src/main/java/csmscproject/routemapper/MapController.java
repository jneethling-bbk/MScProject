package csmscproject.routemapper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import org.geotools.map.FeatureLayer;
import org.geotools.ows.ServiceException;

public class MapController {

	private MapModel model;
	private MapView view;
	FeatureLayer accidentLayer = null;
	
	public MapController(MapModel model, MapView view) {
		this.model = model;
		this.view = view;
		this.view.addConnectAccidentListener(new LoadBtnListener());
		this.view.addToggleAccidentListener(new ToggleLayerListener());
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
	
	class LoadBtnListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File file = view.chooseFile();
			try {
				accidentLayer = model.getRiskLayer(file);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			view.addRiskLayer(accidentLayer);
			view.enableAccidentToggler();
		}
	}
	
	class ToggleLayerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (accidentLayer.isVisible()) {
				view.hideRiskLayer(accidentLayer);
			} else {
				view.showRiskLayer(accidentLayer);
			}
		}
	}
}
