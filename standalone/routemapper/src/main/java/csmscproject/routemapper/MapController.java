package csmscproject.routemapper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.WMSLayer;
import org.geotools.ows.ServiceException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.xml.sax.SAXException;

public class MapController {

	private MapModel model;
	private MapView view;
	private FeatureLayer accidentLayer;
	private FeatureLayer pollutionLayer;
	private FeatureLayer userRouteLayer;
	private String userRouteFileName;
	private RouteReport report;
	private RiskAppetite appetite;
	
	private final String FILE_ERROR_MSG = "Bad file: please try another...";
	private final String DATA_ERROR_MSG = "Data Error, please try loading a different route file...";
	
	public MapController(MapModel model, MapView view) {
		
		this.model = model;
		this.view = view;
		
		this.view.addConnectAccidentListener(new AccidentConnectionListener());
		this.view.addToggleAccidentListener(new AccidentToggleListener());
		this.view.addConnectPollutionListener(new PollutionConnectionListener());
		this.view.addTogglePollutionListener(new PollutionToggleListener());
		this.view.addRiskAppetiteListener(new RiskAppetiteListener());
		this.view.addUserRouteListener(new UserRouteListener());
		this.view.addEvaluateListener(new EvaluateRouteListener());
		this.view.addReportListener(new ViewReportListener());
		this.view.addZoomSAreaListener(new ZoomAreaListener());
		
		report = new RouteReportImpl();
		appetite = new RiskAppetiteImpl();
		
		accidentLayer = null;
		pollutionLayer = null;
		userRouteLayer = null;
		userRouteFileName = null;
	}
	
	public void configureMapView() throws ServiceException, IOException, NullPointerException {
		
		WMSLayer backdrop = model.getBackdrop();
		view.displayMap(backdrop);
	}
	
	class AccidentConnectionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File file = view.chooseFile();
			if (file == null) {return;}
			try {
				accidentLayer = model.getRiskLayer(file, "Accidents");
			} catch (IOException e1) {
				view.displayErrorMessage(FILE_ERROR_MSG);
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
			view.enableZoomBtn();
		}
	}
	
	class PollutionConnectionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File file = view.chooseFile();
			if (file == null) {return;}
			try {
				pollutionLayer = model.getRiskLayer(file, "Pollution");
			} catch (IOException e1) {
				view.displayErrorMessage(FILE_ERROR_MSG);
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
		}
	}
	
	class AccidentToggleListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (accidentLayer.isVisible()) {
				view.hideRiskLayer(accidentLayer);
			} else {
				view.showRiskLayer(accidentLayer);
			}
		}
	}
	
	class PollutionToggleListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (pollutionLayer.isVisible()) {
				view.hideRiskLayer(pollutionLayer);
			} else {
				view.showRiskLayer(pollutionLayer);
			}
		}
	}
	
	class RiskAppetiteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			view.setRiskAppetite();
		}
	}
	
	class UserRouteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File file = view.chooseFile();
			if (file == null) {return;}
			userRouteFileName = file.getName();
			try {
				userRouteLayer = model.getRouteLayer(file, "Route");
				view.zoomToLayer(userRouteLayer);
			} catch (IOException e1) {
				view.displayErrorMessage(FILE_ERROR_MSG);
				return;
			} catch (MismatchedDimensionException e1) {
				view.displayErrorMessage(DATA_ERROR_MSG);
				return;
			} catch (ParserConfigurationException e1) {
				// can only be caused by a programming error
				e1.printStackTrace();
			} catch (SAXException e1) {
				view.displayErrorMessage(FILE_ERROR_MSG);
				return;
			} catch (FactoryException e1) {
				// can only be caused by a programming error
				e1.printStackTrace();
			} catch (TransformException e1) {
				view.displayErrorMessage(DATA_ERROR_MSG);
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
		}
	}
	
	class EvaluateRouteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			report.setRouteFileName(userRouteFileName);
			try {
				report.setRouteLength((long) model.getRouteLen(userRouteLayer));
				report.setSlope(model.getSlope(userRouteLayer));
				report.setAccidentCount((long) model.getNumIntersects(userRouteLayer, accidentLayer));
				report.setPollutionPercentage((long) model.getPollutedPercentage(userRouteLayer, pollutionLayer));
			} catch (MismatchedDimensionException e1) {
				view.displayErrorMessage(DATA_ERROR_MSG);
				return;
			} catch (NoSuchAuthorityCodeException e1) {
				// can only be caused by a programming error
				e1.printStackTrace();
			} catch (FactoryException e1) {
				// can only be caused by a programming error
				e1.printStackTrace();
			} catch (IOException e1) {
				view.displayErrorMessage(FILE_ERROR_MSG);
				return;
			} catch (TransformException e1) {
				view.displayErrorMessage(DATA_ERROR_MSG);
				return;
			}
			appetite.setMaxAccidentCount((long) view.getAllowedIntersects());
			appetite.setMaxPollutionPercentage((long) view.getAllowedPercentage());
			model.resetRouteStyle(userRouteLayer, report, appetite);
			view.enableReportBtn();
		}
	}
	
	class ViewReportListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			view.displayReport(report);
		}
	}
	
	class ZoomAreaListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			view.zoomToLayer(accidentLayer);
		}
	}
}
