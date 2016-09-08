package csmscproject.routemapper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.SwingWorker;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.wms.WebMapServer;
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
	private GridCoverage2D dem;
	private FeatureLayer accidentLayer;
	private FeatureLayer pollutionLayer;
	private FeatureLayer userRouteLayer;
	private String userRouteFileName;
	private RouteReport report;
	private RiskAppetite appetite;
	
	private final String FILE_ERROR_MSG = "Bad file or web resource: please check the connection or try another...";
	private final String DATA_ERROR_MSG = "Data Error, please try using a different file...";
	private final String MESSAGE_HEADING_FAIL = "Procedure failed";
	
	boolean demConnected;
	boolean accidentsConnected;
	boolean pollutionConnected;
	boolean routeLoaded;
	boolean routeEvaluated;
	
	public MapController(MapModel model, final MapView view) {
		
		this.model = model;
		this.view = view;
		
		this.view.addConnectDEMListener(new DemConnectionListener());
		this.view.addConnectAccidentListener(new AccidentConnectionListener());
		this.view.addToggleAccidentListener(new AccidentToggleListener());
		this.view.addConnectPollutionListener(new PollutionConnectionListener());
		this.view.addTogglePollutionListener(new PollutionToggleListener());
		this.view.addRiskAppetiteListener(new RiskAppetiteListener());
		this.view.addUserRouteListener(new UserRouteListener());
		this.view.addEvaluateListener(new EvaluateRouteListener());
		this.view.addReportListener(new ViewReportListener());
		this.view.addZoomSAreaListener(new ZoomAreaListener());
		
		model.addPropertyChangeListener(new PropertyChangeListener() {
	         public void propertyChange(PropertyChangeEvent pce) {
	            if (MapModel.PROGRESS.equals(pce.getPropertyName())) {
	               view.setProgress((Integer)pce.getNewValue());
	            }
	         }
	      });
		
		report = new RouteReportImpl();
		appetite = new RiskAppetiteImpl();
		
		dem = null;
		accidentLayer = null;
		pollutionLayer = null;
		userRouteLayer = null;
		userRouteFileName = null;
		
		demConnected = false;
		accidentsConnected = false;
		pollutionConnected = false;
		routeLoaded = false;
	}
	
	public boolean configureMapView() {

		List<String> servers = model.getServers();
		URL capabilitiesURL = view.getURL(servers);
		WebMapServer wms = null;
		try {
			wms = model.getWMS(capabilitiesURL);
		} catch (ServiceException e) {
			view.displayMessage(FILE_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
			return false;
		} catch (IOException e) {
			view.displayMessage(FILE_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
			return false;
		} catch (NullPointerException e) {
			view.displayMessage("You must select a URL for WMS visualisation", "Cancelled", 0);
			return false;
		}
		
		List<org.geotools.data.ows.Layer> wmsLayers = view.getWMSLayer(wms);
		if (wmsLayers.isEmpty()) {
			view.displayMessage("You must select a layer for WMS visualisation", "Cancelled", 0);
			return false;
		}
		WMSLayer displayLayer = model.getBackdrop(wms, wmsLayers.get(0));
		view.displayMap(displayLayer);
		return true;
	}
	
	class DemConnectionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File file = view.chooseGenericFile();
			if (file == null) {return;}
			try {
				dem = model.getDEM(file);
			} catch (NoSuchAuthorityCodeException e1) {
				// can only be caused by a programming error
				e1.printStackTrace();
			} catch (IOException e1) {
				view.displayMessage(FILE_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
				return;
			} catch (FactoryException e1) {
				// can only be caused by a programming error
				e1.printStackTrace();
			}
			demConnected = true;
			view.enableBtns(demConnected, accidentsConnected, pollutionConnected, routeLoaded);
		}
	}
	
	class AccidentConnectionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File file = view.chooseShapeFile();
			if (file == null) {return;}
			try {
				accidentLayer = model.getRiskLayer(file, "Accidents");
			} catch (IOException e1) {
				view.displayMessage(FILE_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
				return;
			}
			List<Layer> layerList = view.getLayerList();
			for (Layer l : layerList) {
				if (l.getTitle().equals("Accidents")) {
					view.removeLayer((FeatureLayer) l);
				}
			}
			view.addLayer(accidentLayer);
			accidentsConnected = true;
			view.enableBtns(demConnected, accidentsConnected, pollutionConnected, routeLoaded);
		}
	}
	
	class PollutionConnectionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File file = view.chooseShapeFile();
			if (file == null) {return;}
			try {
				pollutionLayer = model.getRiskLayer(file, "Pollution");
			} catch (IOException e1) {
				view.displayMessage(FILE_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
				return;
			}
			List<Layer> layerList = view.getLayerList();
			for (Layer l : layerList) {
				if (l.getTitle().equals("Pollution")) {
					view.removeLayer((FeatureLayer) l);
				}
			}
			view.addLayer(pollutionLayer);
			pollutionConnected = true;
			view.enableBtns(demConnected, accidentsConnected, pollutionConnected, routeLoaded);
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
			File file = view.chooseGenericFile();
			if (file == null) {return;}
			userRouteFileName = file.getName();
			try {
				userRouteLayer = model.getRouteLayer(file, "Route");
				view.zoomToLayer(userRouteLayer);
			} catch (IOException e1) {
				view.displayMessage(FILE_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
				return;
			} catch (MismatchedDimensionException e1) {
				view.displayMessage(DATA_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
				return;
			} catch (ParserConfigurationException e1) {
				// can only be caused by a programming error
				e1.printStackTrace();
			} catch (SAXException e1) {
				view.displayMessage(FILE_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
				return;
			} catch (FactoryException e1) {
				// can only be caused by a programming error
				e1.printStackTrace();
			} catch (TransformException e1) {
				view.displayMessage(DATA_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
				return;
			}
			List<Layer> layerList = view.getLayerList();
			for (Layer l : layerList) {
				if (l.getTitle().equals("Route")) {
					view.removeLayer((FeatureLayer) l);
				}
			}
			view.addLayer(userRouteLayer);
			routeLoaded = true;
			view.enableBtns(demConnected, accidentsConnected, pollutionConnected, routeLoaded);
		}
	}
	
	class EvaluateRouteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			report.setRouteFileName(userRouteFileName);
		      SwingWorker<Void, Void> swingworker = new SwingWorker<Void, Void>() {
			         @Override
			         protected Void doInBackground() {
			        	 double routeLength = 0.0;
			        	 int accidentCount = 0;
			        	 int pollutionPercentage = 0;
			        	 double slope = 0.0;
			        	 model.reset();
			        	 view.disableBtns();
			        	 view.setStatus("STATUS: evaluating route");
			        	 try {
			        		 routeLength = model.getRouteLen(userRouteLayer);
			        		 accidentCount = model.getNumIntersects(userRouteLayer, accidentLayer);
			        		 pollutionPercentage = model.getPollutedPercentage(userRouteLayer, pollutionLayer);
			        		 slope = model.getSlope(userRouteLayer, dem);
			        	 } catch (MismatchedDimensionException e1) {
			        		 view.displayMessage(DATA_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
			        		 return null;
			        	 } catch (NoSuchAuthorityCodeException e1) {
			        		 // can only be caused by a programming error
			        		 e1.printStackTrace();
			        	 } catch (FactoryException e1) {
			        		 // can only be caused by a programming error
			        		 e1.printStackTrace();
			        	 } catch (IOException e1) {
			        		 view.displayMessage(FILE_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
			        		 return null;
			        	 } catch (TransformException e1) {
			        		 view.displayMessage(DATA_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
			        		 return null;
			        	 }
			        	 report.setRouteLength((long) routeLength);
			        	 report.setAccidentCount((long) accidentCount);
			        	 report.setPollutionPercentage((long) pollutionPercentage);
			        	 report.setSlope(slope);
			        	 
			        	 appetite.setMaxAccidentCount((long) view.getAllowedIntersects());
			        	 appetite.setMaxPollutionPercentage((long) view.getAllowedPercentage());
			        	 model.resetRouteStyle(userRouteLayer, report, appetite);
			        	 view.enableBtns(demConnected, accidentsConnected, pollutionConnected, routeLoaded);
			        	 view.enableReportBtn();
			        	 view.setStatus("STATUS: waiting for input");
			        	 return null;
			         }
			         
			         @Override
			         protected void done() {
			            view.done();
			         }
			      };
			      swingworker.execute();
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
