package csmscproject.riskmodeller;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class BuildView {
	private JFrame mainFrame;
	private JPanel controlPanel;
	private JButton connectPollutionGridBtn;
	private JButton generatePollutionModelBtn;
	private JButton connectTrafficNetworkBtn;
	private JButton generateTrafficModelBtn;
	
	public BuildView() {
		
		connectPollutionGridBtn = new JButton("Connect interpolation grid");
		generatePollutionModelBtn = new JButton("Generate air-pollution model");
		generatePollutionModelBtn.setEnabled(false);
		connectTrafficNetworkBtn = new JButton("Connect reference network");
		generateTrafficModelBtn = new JButton("Generate traffic accident model");
		generateTrafficModelBtn.setEnabled(false);
		
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
	
	void displayMessage(String message, String heading, int messageType){
		JOptionPane.showMessageDialog(mainFrame, message, heading, messageType);
	}
	
	public void addConnectPollutionGridBtnListener(ActionListener listenForConnectPollutionGridBtn) {
		connectPollutionGridBtn.addActionListener(listenForConnectPollutionGridBtn);
	}
	
	public void addGeneratePollutionModelBtnListener(ActionListener listenForGeneratePollutionModelBtn) {
		generatePollutionModelBtn.addActionListener(listenForGeneratePollutionModelBtn);
	}
	
	public void addConnectTrafficNetworkBtnListener(ActionListener listenForConnectTrafficNetworkBtn) {
		connectTrafficNetworkBtn.addActionListener(listenForConnectTrafficNetworkBtn);
	}
	
	public void addGenerateTrafficModelBtnListener(ActionListener listenForGenerateTrafficModelBtn) {
		generateTrafficModelBtn.addActionListener(listenForGenerateTrafficModelBtn);
	}
}
