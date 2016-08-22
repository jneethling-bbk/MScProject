package csmscproject.riskmodeller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BuildController {
	private BuildView view;
	private BuildModel model;
	
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
			view.displayMessage("Not yet implemented", "Warning", 0);
		}
			
	}
	
	class generatePollutionModelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}

	}
	
	class connectTrafficNetworkListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			view.displayMessage("Not yet implemented", "Warning", 0);
		}

	}
	
	class generateTrafficModelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

		}

	}
}
