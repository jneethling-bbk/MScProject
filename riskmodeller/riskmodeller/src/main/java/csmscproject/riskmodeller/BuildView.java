package csmscproject.riskmodeller;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.geotools.swing.data.JFileDataStoreChooser;

public class BuildView {
	private JFrame mainFrame;
	private JPanel controlPanel;
	private JButton connectPollutionGridBtn;
	private JButton generatePollutionModelBtn;
	private JButton connectTrafficNetworkBtn;
	private JButton generateTrafficModelBtn;
	private JProgressBar progressBar;
	private JLabel statusLabel;
	
	public BuildView() {
		
		connectPollutionGridBtn = new JButton("Connect interpolation grid");
		generatePollutionModelBtn = new JButton("Generate air-pollution model");
		generatePollutionModelBtn.setEnabled(false);
		connectTrafficNetworkBtn = new JButton("Connect reference network");
		generateTrafficModelBtn = new JButton("Generate traffic accident model");
		generateTrafficModelBtn.setEnabled(false);
		progressBar = new JProgressBar();
		statusLabel = new JLabel("STATUS: waiting for input");
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		GridLayout grid = new GridLayout(3, 2);
		controlPanel = new JPanel();
		controlPanel.setLayout(grid);
		controlPanel.add(connectPollutionGridBtn);	
		controlPanel.add(connectTrafficNetworkBtn);
		controlPanel.add(generatePollutionModelBtn);
		controlPanel.add(generateTrafficModelBtn);
		controlPanel.add(statusLabel);
		controlPanel.add(progressBar);
				
		mainFrame = new JFrame("Risk Model Builder for Cycling Route Analysis");
		mainFrame.add(controlPanel);
		mainFrame.setSize(500, 150);
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}
	
	public void setProgress(int progress) {
		progressBar.setValue(progress);
	}
	
	public void setStatus(String status) {
		statusLabel.setText(status);
	}
	
	public void done() {
		setProgress(100);
	}
	
	public void displayGUI() {
		mainFrame.setVisible(true);
	}
	
	public void enableBtns(boolean gridConnected, boolean networkConnected) {
		connectPollutionGridBtn.setEnabled(true);
		connectTrafficNetworkBtn.setEnabled(true);
		if (gridConnected) {
			generatePollutionModelBtn.setEnabled(true);
		}
		if (networkConnected) {
			generateTrafficModelBtn.setEnabled(true);
		}
	}
	
	public void disableBtns() {
		connectPollutionGridBtn.setEnabled(false);
		generatePollutionModelBtn.setEnabled(false);
		connectTrafficNetworkBtn.setEnabled(false);
		generateTrafficModelBtn.setEnabled(false);
		
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
        Date now = Calendar.getInstance().getTime();
        
        String strDate = sdfDate.format(now);
        String[] firstArray = strDate.split(" ");
        String[] secondArray = firstArray[1].split(":");
        strDate = firstArray[0] + "-" + secondArray[0] + "H00";

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
