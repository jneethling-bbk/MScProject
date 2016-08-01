package csmscproject.routemapper;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.WMSLayer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class MapView {

	MapContent mapcontent;
	JMapFrame myFrame;
	JMapPane myPane;
	private WMSLayer backdrop;
	JButton btn1;
	JButton btn2;
	JButton btn3;
	JButton btn4;
	
	public MapView() {
		mapcontent = new MapContent();
		mapcontent.setTitle("Cycle route analysis client application");
		myFrame = new JMapFrame(mapcontent);
		myPane = myFrame.getMapPane();
		myFrame.enableStatusBar(true);
		myFrame.enableToolBar(true);
		//myFrame.enableLayerTable(true);
        JToolBar toolBar = myFrame.getToolBar();
        btn1 = new JButton("Connect accident data");
        toolBar.addSeparator();
        toolBar.add(btn1);
        btn2 = new JButton("Toggle accident data");
        btn2.setEnabled(false);
        toolBar.addSeparator();
        toolBar.add(btn2);
        btn3 = new JButton("Connect pollution data");
        toolBar.addSeparator();
        toolBar.add(btn3);
        btn4 = new JButton("Toggle pollution data");
        btn4.setEnabled(false);
        toolBar.addSeparator();
        toolBar.add(btn4);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
	}
	public List<Layer> getLayerList() {
		return mapcontent.layers();
	}
	
	public void setBackdrop(WMSLayer backdrop) {
		this.backdrop = backdrop;
	}
		
	public void addRiskLayer(FeatureLayer riskLayer) {
		mapcontent.addLayer(riskLayer);
		myPane.repaint();	
	}
	
	public void removeRiskLayer(FeatureLayer riskLayer) {
		mapcontent.removeLayer(riskLayer);
		myPane.repaint();	
	}
	
	public void showRiskLayer(FeatureLayer riskLayer) {
		riskLayer.setVisible(true);
	}
	
	public void hideRiskLayer(FeatureLayer riskLayer) {
		riskLayer.setVisible(false);
	}
	
	public void displayMap() {
		CoordinateReferenceSystem myCRS = backdrop.getCoordinateReferenceSystem();    
		mapcontent.addLayer(backdrop);
		ReferencedEnvelope outbounds = new ReferencedEnvelope(-77278, 45656, 6689116, 6734899, myCRS);
		myPane.setDisplayArea(outbounds);
		myFrame.setVisible(true);
	}
	
	public void enableAccidentToggler() {
		btn2.setEnabled(true);
	}
	
	public void enablePollutionToggler() {
		btn4.setEnabled(true);
	}
	
	File chooseFile() {
        File file = JFileDataStoreChooser.showOpenFile("shp", null);
        if (file == null) {
            return null;
        }
        return file;
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
}
