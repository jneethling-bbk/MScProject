package csmscproject.riskmodeller;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.geotools.swing.data.JFileDataStoreChooser;

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
	
	public void enableGeneratePollutionModelBtn() {
		generatePollutionModelBtn.setEnabled(true);
	}
	
	public void enableGenerateTrafficModelBtn() {
		generateTrafficModelBtn.setEnabled(true);
	}
	
	void displayMessage(String message, String heading, int messageType){
		JOptionPane.showMessageDialog(mainFrame, message, heading, messageType);
	}
	
	public File chooseShapeFile() {
        File file = JFileDataStoreChooser.showOpenFile("shp", null);
        if (file == null) {
            return null;
        }
        return file;
	}
	
	public File chooseGenericFile() {
        final JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(null);
        File inputFile = fc.getSelectedFile();
        return inputFile;
	}
	
    public File setShapeFile(String model) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        String[] firstArray = strDate.split(" ");
        String[] secondArray = firstArray[1].split(":");
        strDate = firstArray[0] + "-" + secondArray[0] + "H" + secondArray[0];

        JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
        chooser.setDialogTitle("Save " + model);
        chooser.setSelectedFile(new File(model + "-" + strDate + ".shp"));

        int returnVal = chooser.showSaveDialog(null);
        File newFile = chooser.getSelectedFile();
        
        if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) {
            // the user cancelled the dialog
        	newFile = null;
        }
        return newFile;
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
