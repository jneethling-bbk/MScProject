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
	
	public MapController(MapModel model, MapView view) {
		this.model = model;
		this.view = view;
		this.view.addLoadBtnListener(new LoadBtnListener());

	}
	
	class LoadBtnListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			FeatureLayer riskLayer = null;
			File file = view.chooseFile();
			try {
				riskLayer = model.getRiskLayer(file);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			view.addRiskLayer(riskLayer);
		}

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
}
