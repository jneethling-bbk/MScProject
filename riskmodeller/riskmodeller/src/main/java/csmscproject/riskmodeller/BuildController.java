package csmscproject.riskmodeller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

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
	private FeatureLayer trafficReferenceNetwork;
	
	private final String FILE_ERROR_MSG = "Bad file: please try another...";
	private final String DATA_ERROR_MSG = "Data Error, please try loading a different route file...";
	private final String MESSAGE_HEADING = "Procedure failed";
	
	public BuildController(BuildView view, BuildModel model) {
		this.model = model;
		this.view = view;
		this.view.addConnectPollutionGridBtnListener(new connectPollutionGridListener());
		this.view.addGeneratePollutionModelBtnListener(new generatePollutionModelListener());
		this.view.addConnectTrafficNetworkBtnListener(new connectTrafficNetworkListener());
		this.view.addGenerateTrafficModelBtnListener(new generateTrafficModelListener());
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
				view.displayMessage(FILE_ERROR_MSG, MESSAGE_HEADING, 0);
			}
			view.enableGeneratePollutionModelBtn();
		}			
	}
	
	class generatePollutionModelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File outputFile = view.setShapeFile("AirPollutionModel");
			if (outputFile == null) {return;}
			try {
				model.buildPollutionModel(pollutionReferenceGrid, outputFile);
			} catch (MismatchedDimensionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchAuthorityCodeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (FactoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	class connectTrafficNetworkListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			File file = view.chooseShapeFile();
			if (file == null) {return;}
			try {
				trafficReferenceNetwork = model.getReferenceLayer(file, "RoadNetwork");
			} catch (IOException e1) {
				view.displayMessage(FILE_ERROR_MSG, MESSAGE_HEADING, 0);
			}
			view.enableGenerateTrafficModelBtn();
		}
	}
	
	class generateTrafficModelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

		}
	}
}
