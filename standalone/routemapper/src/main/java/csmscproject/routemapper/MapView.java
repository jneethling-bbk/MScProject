package csmscproject.routemapper;

import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;

import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.WMSLayer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.geometry.BoundingBox;
import com.vividsolutions.jts.geom.Envelope;

public class MapView {
	
	private JMapFrame mapFrame;
	private JMapPane mapPane;
	private MapContent mapcontent;
	private JButton connectDEM;
	private JButton connectTrafficBtn;
	private JButton toggleTrafficBtn;
	private JButton connectPollutionBtn;
	private JButton togglePollutionBtn;
	private JButton setRiskAppetiteBtn;
	private JButton addRouteBtn;
	private JButton evaluateRouteBtn;
	private JButton viewReportBtn;
	private JButton zoomToAreaBtn;
	private JFrame appetiteFrame;
	private JSlider accIntersectsAllowed;
	private JSlider polPercentageAllowed;
	
	public MapView() {

		final int INTER_MIN = 0;
		final int INTER_MAX = 20;
		final int INTER_INIT = 10;
		final int PER_MIN = 0;
		final int PER_MAX = 100;
		final int PER_INIT = 50;
		
		mapcontent = new MapContent();
		mapcontent.setTitle("Cycle route analysis client application");
		mapFrame = new JMapFrame(mapcontent);
		mapPane = mapFrame.getMapPane();
		mapFrame.enableToolBar(true);
        JToolBar toolBar = mapFrame.getToolBar();
        connectDEM = new JButton();
        connectDEM.setIcon(new ImageIcon(getClass().getResource("/dem.jpg")));
        connectDEM.setToolTipText("Connect digital elevation model");
        toolBar.addSeparator();
        toolBar.add(connectDEM);
        connectTrafficBtn = new JButton();
        connectTrafficBtn.setIcon(new ImageIcon(getClass().getResource("/connect_traffic_risk.jpg")));
        connectTrafficBtn.setToolTipText("Connect traffic risk model");
        toolBar.addSeparator();
        toolBar.add(connectTrafficBtn);
        toggleTrafficBtn = new JButton();
        toggleTrafficBtn.setIcon(new ImageIcon(getClass().getResource("/toggle_traffic_risk.jpg")));
        toggleTrafficBtn.setToolTipText("Toggle traffic risk model");
        toggleTrafficBtn.setEnabled(false);
        toolBar.addSeparator();
        toolBar.add(toggleTrafficBtn);
        connectPollutionBtn = new JButton();
        connectPollutionBtn.setIcon(new ImageIcon(getClass().getResource("/connect_pollution_risk.jpg")));
        connectPollutionBtn.setToolTipText("Connect pollution risk model");
        toolBar.addSeparator();
        toolBar.add(connectPollutionBtn);
        togglePollutionBtn = new JButton();
        togglePollutionBtn.setIcon(new ImageIcon(getClass().getResource("/toggle_pollution_risk.jpg")));
        togglePollutionBtn.setToolTipText("Toggle pollution risk model");
        togglePollutionBtn.setEnabled(false);
        toolBar.addSeparator();
        toolBar.add(togglePollutionBtn);
        setRiskAppetiteBtn = new JButton();
        setRiskAppetiteBtn.setIcon(new ImageIcon(getClass().getResource("/risk_appetite.jpg")));
        setRiskAppetiteBtn.setToolTipText("Set risk appetite");
        toolBar.addSeparator();
        toolBar.add(setRiskAppetiteBtn);
        addRouteBtn = new JButton();
        addRouteBtn.setIcon(new ImageIcon(getClass().getResource("/add_route.jpg")));
        addRouteBtn.setToolTipText("Add route");
        toolBar.addSeparator();
        toolBar.add(addRouteBtn);
        evaluateRouteBtn = new JButton();
        evaluateRouteBtn.setIcon(new ImageIcon(getClass().getResource("/evaluate_route.jpg")));
        evaluateRouteBtn.setToolTipText("Evaluate route");
        evaluateRouteBtn.setEnabled(false);
        toolBar.addSeparator();
        toolBar.add(evaluateRouteBtn);
        viewReportBtn = new JButton();
        viewReportBtn.setIcon(new ImageIcon(getClass().getResource("/show_report.jpg")));
        viewReportBtn.setToolTipText("View report");
        viewReportBtn.setEnabled(false);
        toolBar.addSeparator();
        toolBar.add(viewReportBtn);
        zoomToAreaBtn = new JButton();
        zoomToAreaBtn.setIcon(new ImageIcon(getClass().getResource("/zoom_area.jpg")));
        zoomToAreaBtn.setToolTipText("Zoom to study area");
        zoomToAreaBtn.setEnabled(false);
        toolBar.addSeparator();
        toolBar.add(zoomToAreaBtn);       
        mapFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mapFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
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
	
	public void displayMap(WMSLayer backdrop) {  
		mapcontent.addLayer(backdrop);
		mapFrame.setVisible(true);
	}
	
	public List<Layer> getLayerList() {
		return mapcontent.layers();
	}
		
	public void addLayer(FeatureLayer layer) {
		mapcontent.addLayer(layer);	
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
	
	public void enableAccidentToggler() {
		toggleTrafficBtn.setEnabled(true);
	}
	
	public void enablePollutionToggler() {
		togglePollutionBtn.setEnabled(true);
	}
	
	public void enableEvaluateBtn() {
		evaluateRouteBtn.setEnabled(true);
	}
	
	public void enableReportBtn() {
		viewReportBtn.setEnabled(true);
	}
	
	public void enableZoomBtn() {
		zoomToAreaBtn.setEnabled(true);
	}
	
	void displayErrorMessage(String errorMessage){
		JOptionPane.showMessageDialog(mapFrame, errorMessage);
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
	
	public void displayReport(RouteReport report) {
		DecimalFormat df2 = new DecimalFormat(".##");
		
		JFrame reportFrame = new JFrame("User route evaluation report");
		reportFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reportFrame.setSize(350, 150);
		
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
		
		JPanel reportPanel = new JPanel();
		BoxLayout box2 = new BoxLayout(reportPanel, BoxLayout.Y_AXIS);
		reportPanel.setLayout(box2);

		reportPanel.add(l1);
		reportPanel.add(div);
		reportPanel.add(l2);
		reportPanel.add(l3);
		reportPanel.add(l4);
		reportPanel.add(l5);
		reportFrame.add(reportPanel);
		reportFrame.setVisible(true);
	}
		
	public void zoomToLayer(FeatureLayer layer) {
		BoundingBox inbounds = layer.getBounds();
		((Envelope) inbounds).expandBy(500);
		mapPane.setDisplayArea(inbounds);
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
	
	public void addConnectDEMListener(ActionListener listenForDEMCon) {
		connectDEM.addActionListener(listenForDEMCon);
	}
		
	public void addConnectAccidentListener(ActionListener listenForAccidentCon) {
		connectTrafficBtn.addActionListener(listenForAccidentCon);
	}
	
	public void addToggleAccidentListener(ActionListener listenForAccidentToggle) {
		toggleTrafficBtn.addActionListener(listenForAccidentToggle);
	}
	
	public void addConnectPollutionListener(ActionListener listenForPollutionCon) {
		connectPollutionBtn.addActionListener(listenForPollutionCon);
	}
	
	public void addTogglePollutionListener(ActionListener listenForPollutionToggle) {
		togglePollutionBtn.addActionListener(listenForPollutionToggle);
	}
	
	public void addRiskAppetiteListener(ActionListener listenForRiskAppetite) {
		setRiskAppetiteBtn.addActionListener(listenForRiskAppetite);
	}
	
	public void addUserRouteListener(ActionListener listenForAddUserRoute) {
		addRouteBtn.addActionListener(listenForAddUserRoute);
	}
	
	public void addEvaluateListener(ActionListener listenForRouteEval) {
		evaluateRouteBtn.addActionListener(listenForRouteEval);
	}
	
	public void addReportListener(ActionListener listenForViewReport) {
		viewReportBtn.addActionListener(listenForViewReport);
	}
	
	public void addZoomSAreaListener(ActionListener listenForZoomToSArea) {
		zoomToAreaBtn.addActionListener(listenForZoomToSArea);
	}
}
