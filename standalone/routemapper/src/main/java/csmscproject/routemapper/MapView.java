package csmscproject.routemapper;

import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.WMSLayer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public class MapView {
	
	JMapFrame myFrame;
	JMapPane myPane;
	MapContent mapcontent;
	CoordinateReferenceSystem myCRS;
	WMSLayer backdrop;
	ReferencedEnvelope outbounds;
	JButton btn1;
	JButton btn2;
	JButton btn3;
	JButton btn4;
	JButton btn5;
	JButton btn6;
	JButton btn7;
	JButton btn8;
	JButton btn9;
	JFrame appetiteFrame;
	JSlider accIntersectsAllowed;
	JSlider polPercentageAllowed;
	
	public MapView() {
		mapcontent = new MapContent();
		mapcontent.setTitle("Cycle route analysis client application");
		myFrame = new JMapFrame(mapcontent);
		myPane = myFrame.getMapPane();
		myFrame.enableStatusBar(true);
		myFrame.enableToolBar(true);
		//myFrame.enableLayerTable(true);
        JToolBar toolBar = myFrame.getToolBar();
        btn1 = new JButton("Connect traffic risk");
        toolBar.addSeparator();
        toolBar.add(btn1);
        btn2 = new JButton("Toggle traffic risk");
        btn2.setEnabled(false);
        toolBar.addSeparator();
        toolBar.add(btn2);
        btn3 = new JButton("Connect pollution risk");
        toolBar.addSeparator();
        toolBar.add(btn3);
        btn4 = new JButton("Toggle pollution risk");
        btn4.setEnabled(false);
        toolBar.addSeparator();
        toolBar.add(btn4);
        btn5 = new JButton("Set risk appetite");
        toolBar.addSeparator();
        toolBar.add(btn5);
        btn6 = new JButton("Add user route");
        toolBar.addSeparator();
        toolBar.add(btn6);
        btn7 = new JButton("Evaluate user route");
        btn7.setEnabled(false);
        toolBar.addSeparator();
        toolBar.add(btn7);
        btn8 = new JButton("View report");
        btn8.setEnabled(false);
        toolBar.addSeparator();
        toolBar.add(btn8);
        btn9 = new JButton("Zoom to study area");
        btn9.setEnabled(false);
        toolBar.addSeparator();
        toolBar.add(btn9);       
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		final int INTER_MIN = 0;
		final int INTER_MAX = 20;
		final int INTER_INIT = 10;
		final int PER_MIN = 0;
		final int PER_MAX = 100;
		final int PER_INIT = 50;
		
		appetiteFrame = new JFrame("Set risk appetite parameters");
		appetiteFrame.setSize(400, 200);
		JLabel accLabel = new JLabel("Maximum allowed accident hotspots on route:");
		accIntersectsAllowed = new JSlider(JSlider.HORIZONTAL, INTER_MIN, INTER_MAX, INTER_INIT);
		accIntersectsAllowed.setMajorTickSpacing(5);
		accIntersectsAllowed.setMinorTickSpacing(1);
		accIntersectsAllowed.setSnapToTicks(true);
		accIntersectsAllowed.setPaintTicks(true);
		accIntersectsAllowed.setPaintLabels(true);
		
		JLabel polLabel = new JLabel("Maximum allowed percentage of route polluted:");
		polPercentageAllowed = new JSlider(JSlider.HORIZONTAL, PER_MIN, PER_MAX, PER_INIT);
		polPercentageAllowed.setMajorTickSpacing(10);
		polPercentageAllowed.setMinorTickSpacing(5);
		polPercentageAllowed.setSnapToTicks(true);
		polPercentageAllowed.setPaintTicks(true);
		polPercentageAllowed.setPaintLabels(true);
		
		JPanel appetitePanel = new JPanel();
		BoxLayout box1 = new BoxLayout(appetitePanel, BoxLayout.Y_AXIS);
		appetitePanel.setLayout(box1);
		appetitePanel.add(accLabel);
		appetitePanel.add(accIntersectsAllowed);
		appetitePanel.add(polLabel);
		appetitePanel.add(polPercentageAllowed);
		appetiteFrame.add(appetitePanel);
		appetiteFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
	}
	public List<Layer> getLayerList() {
		return mapcontent.layers();
	}
		
	public void addLayer(FeatureLayer layer) {
		mapcontent.addLayer(layer);
		//myPane.repaint();	
	}
	
	public void removeLayer(FeatureLayer layer) {
		mapcontent.removeLayer(layer);	
	}
	
	public void showRiskLayer(FeatureLayer riskLayer) {
		riskLayer.setVisible(true);
	}
	
	public void hideRiskLayer(FeatureLayer riskLayer) {
		riskLayer.setVisible(false);
	}
	
	public void displayMap(WMSLayer backdrop) {
		this.backdrop = backdrop;
		myCRS = backdrop.getCoordinateReferenceSystem();    
		mapcontent.addLayer(backdrop);
		//ReferencedEnvelope tempbounds = backdrop.getBounds();
		//outbounds = new ReferencedEnvelope(tempbounds.getMinX()/24, tempbounds.getMaxX()/24, tempbounds.getMinY()/26, tempbounds.getMaxY()/12, myCRS);
		//myPane.setDisplayArea(outbounds);
		myFrame.setVisible(true);
	}
	
	public int getAccSlider() {
		return accIntersectsAllowed.getValue();
	}
	
	public int getPolSlider() {
		return polPercentageAllowed.getValue();
	}
	
	public void refreshMap() {
		myPane.repaint();
	}
	public void enableAccidentToggler() {
		btn2.setEnabled(true);
	}
	public void enablePollutionToggler() {
		btn4.setEnabled(true);
	}	
	public void enableEvaluateBtn() {
		btn7.setEnabled(true);
	}
	public void enableReportBtn() {
		btn8.setEnabled(true);
	}
	public void enableZoomBtn() {
		btn9.setEnabled(true);
	}
	
	void displayErrorMessage(String errorMessage){
		JOptionPane.showMessageDialog(myFrame, errorMessage);
	}
	void setRiskAppetite() {
		appetiteFrame.setVisible(true);	
	}
	public int getAllowedIntersects() {
		return accIntersectsAllowed.getValue();
	}
	public int getAllowedPercentage() {
		return polPercentageAllowed.getValue();
	}
	
	void displayReport(RouteReport report) {
		//FlowLayout flo = new FlowLayout();
		DecimalFormat df2 = new DecimalFormat(".#");
		
		JFrame reportFrame = new JFrame("User route evaluation report");
		reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //reportFrame.setLayout(flo);
        reportFrame.setSize(350, 150);
		
		//String[] bits = s.split(";");
        String l1S = "Route file evaluated: " + report.getRouteFileName();
        
        int n = l1S.length();
        char[] chars = new char[n];
        Arrays.fill(chars, '=');
        String divString = new String(chars);
        
        String l2S = "Length of route: " + report.getRouteLength() + " meters";
        String l3S = "Average gradient of route: " + df2.format(report.getSlope()) + "%";
        String l4S = "Number of accident hotspots on route: " + report.getAccidentCount();
        String l5S = "Percentage of route severely polluted: " + report.getPollutionPercentage() + "%";
		
        JLabel l1 = new JLabel(l1S);
        JLabel div = new JLabel(divString);
        JLabel l2 = new JLabel(l2S);
        JLabel l3 = new JLabel(l3S);
		JLabel l4 = new JLabel(l4S);
		JLabel l5 = new JLabel(l5S);
		
		//JTextArea report = new JTextArea(4, 20);
		//report.append(bits[0] + "\n");
		//report.append(bits[1] + "\n");
		//report.append(bits[2] + "\n");
		JPanel reportPanel = new JPanel();
		BoxLayout box2 = new BoxLayout(reportPanel, BoxLayout.Y_AXIS);
		reportPanel.setLayout(box2);
		
		//reportPanel.add(report);
		
		reportPanel.add(l1);
		reportPanel.add(div);
		reportPanel.add(l2);
		reportPanel.add(l3);
		reportPanel.add(l4);
		reportPanel.add(l5);
		reportFrame.add(reportPanel);
		reportFrame.setVisible(true);

	}
	
	File chooseFile() {
        File file = JFileDataStoreChooser.showOpenFile("shp", null);
        if (file == null) {
            return null;
        }
        return file;
	}
	
	File chooseXMLFile() {
        final JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(null);
        File inputFile = fc.getSelectedFile();
        return inputFile;
	}
		
	void addConnectAccidentListener(ActionListener listenForAccidentCon) {
		btn1.addActionListener(listenForAccidentCon);
	}
	void addToggleAccidentListener(ActionListener listenForAccidentToggle) {
		btn2.addActionListener(listenForAccidentToggle);
	}
	void addConnectPollutionListener(ActionListener listenForPollutionCon) {
		btn3.addActionListener(listenForPollutionCon);
	}
	void addTogglePollutionListener(ActionListener listenForPollutionToggle) {
		btn4.addActionListener(listenForPollutionToggle);
	}
	void addRiskAppetiteListener(ActionListener listenForRiskAppetite) {
		btn5.addActionListener(listenForRiskAppetite);
	}
	void addUserRouteListener(ActionListener listenForAddUserRoute) {
		btn6.addActionListener(listenForAddUserRoute);
	}
	void addEvaluateListener(ActionListener listenForRouteEval) {
		btn7.addActionListener(listenForRouteEval);
	}
	void addReportListener(ActionListener listenForViewReport) {
		btn8.addActionListener(listenForViewReport);
	}
	void addZoomSAreaListener(ActionListener listenForZoomToSArea) {
		btn9.addActionListener(listenForZoomToSArea);
	}
	
	public void zoomToLayer(FeatureLayer layer) {
		BoundingBox inbounds = layer.getBounds();
		((Envelope) inbounds).expandBy(500);
		myPane.setDisplayArea(inbounds);
		//myPane.repaint();
	}
}
