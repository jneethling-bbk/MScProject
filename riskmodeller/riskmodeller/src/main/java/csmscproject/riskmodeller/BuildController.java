package csmscproject.riskmodeller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingWorker;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.feature.SchemaException;
import org.geotools.map.FeatureLayer;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.xml.sax.SAXException;

public class BuildController {
	private BuildView view;
	private BuildModel model;
	private FeatureLayer pollutionReferenceGrid;
	private boolean gridConnected;
	private FeatureLayer trafficReferenceNetwork;
	private boolean networkConnected;
	
	private final String FILE_ERROR_MSG = "Bad file: please try another...";
	private final String DATA_ERROR_MSG = "Data Error, please try loading a different route file...";
	private final String SUCCESS_MSG = "Model construction completed successfully";
	private final String MESSAGE_HEADING_FAIL = "Procedure failed";
	private final String MESSAGE_HEADING_OK = "Procedure completed";
	
	public BuildController(final BuildView view, BuildModel model) {
		this.model = model;
		this.view = view;
		
		this.view.addConnectPollutionGridBtnListener(new connectPollutionGridListener());
		this.view.addGeneratePollutionModelBtnListener(new generatePollutionModelListener());
		this.view.addConnectTrafficNetworkBtnListener(new connectTrafficNetworkListener());
		this.view.addGenerateTrafficModelBtnListener(new generateTrafficModelListener());
	    
		model.addPropertyChangeListener(new PropertyChangeListener() {
		         public void propertyChange(PropertyChangeEvent pce) {
		            if (BuildModel.PROGRESS.equals(pce.getPropertyName())) {
		               view.setProgress((Integer)pce.getNewValue());
		            }
		         }
		      });
	
		gridConnected = false;
		networkConnected = false;
	}
	
	public void configureGUI() {
		view.displayGUI();
	}
	
	class connectPollutionGridListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			File file = view.chooseShapeFile();
			if (file == null) {return;}
			try {
				pollutionReferenceGrid = model.getReferenceLayer(file, "PollutionGrid");
			} catch (IOException e1) {
				view.displayMessage(FILE_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
			}
			gridConnected = true;
			view.enableBtns(gridConnected, networkConnected);
		}			
	}
	
	class generatePollutionModelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			final File outputFile = view.setShapeFile("AirPollutionRiskModel");
			if (outputFile == null) {return;}
			
		      SwingWorker<Void, Void> swingworker = new SwingWorker<Void, Void>() {
			         @Override
			         protected Void doInBackground() {
			        	 model.reset();
			        	 view.disableBtns();
			        	 view.setStatus("STATUS: constructing risk model");
							try {
								model.buildPollutionModel(pollutionReferenceGrid, outputFile);
							} catch (MismatchedDimensionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NoSuchAuthorityCodeException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FactoryException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SAXException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ParserConfigurationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (TransformException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							view.displayMessage(SUCCESS_MSG, MESSAGE_HEADING_OK, 1);
							view.enableBtns(gridConnected, networkConnected);
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
	
	class connectTrafficNetworkListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File file = view.chooseShapeFile();
			if (file == null) {return;}
			try {
				trafficReferenceNetwork = model.getReferenceLayer(file, "RoadNetwork");
			} catch (IOException e1) {
				view.displayMessage(FILE_ERROR_MSG, MESSAGE_HEADING_FAIL, 0);
			}
			networkConnected = true;
			view.enableBtns(gridConnected, networkConnected);
		}
	}
	
	class generateTrafficModelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			final File inputFile = view.chooseGenericFile();
			if (inputFile == null) {return;}
			final File outputFile = view.setShapeFile("TrafficRiskModel");
			if (outputFile == null) {return;}
			
		      SwingWorker<Void, Void> swingworker = new SwingWorker<Void, Void>() {
			         @Override
			         protected Void doInBackground() {
			        	 model.reset();
			        	 view.disableBtns();
			        	 view.setStatus("STATUS: constructing risk model");
			        	 try {
							model.buildTrafficModel(trafficReferenceNetwork, inputFile, outputFile);
						} catch (MismatchedDimensionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchAuthorityCodeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SchemaException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (FactoryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (TransformException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							view.displayMessage(SUCCESS_MSG, MESSAGE_HEADING_OK, 1);
							view.enableBtns(gridConnected, networkConnected);
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
}
