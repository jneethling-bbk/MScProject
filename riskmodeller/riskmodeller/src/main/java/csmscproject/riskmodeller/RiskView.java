package csmscproject.riskmodeller;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class RiskView {

	private JFrame mainFrame;
	private JPanel controlPanel;
	private JPanel statusPanel;
	private JButton connectPollutionGridBtn;
	private JButton generatePollutionModelBtn;
	private JButton connectTrafficNetworkBtn;
	private JButton generateTrafficModelBtn;
	
	public RiskView() {
		
		connectPollutionGridBtn = new JButton("Connect interpolation grid");
		generatePollutionModelBtn = new JButton("Generate air-pollution model");
		connectTrafficNetworkBtn = new JButton("Connect reference network");
		generateTrafficModelBtn = new JButton("Generate traffic accident model");
		
		GridLayout grid = new GridLayout(2, 2);
		controlPanel = new JPanel();
		controlPanel.setLayout(grid);
		controlPanel.add(connectPollutionGridBtn);
		
		controlPanel.add(connectTrafficNetworkBtn);
		controlPanel.add(generatePollutionModelBtn);
		controlPanel.add(generateTrafficModelBtn);
		
		mainFrame = new JFrame("Risk Model Builder for Cycling Route Analysis");
		mainFrame.add(controlPanel);
		mainFrame.setSize(500, 100);
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	public void displayGUI() {
		mainFrame.setVisible(true);
	}
}
