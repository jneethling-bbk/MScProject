package csmscproject.routemapper;

import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.map.WMSLayer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class MapView extends JFrame {

	private static final long serialVersionUID = -5894832586538467097L;
	MapContent mapcontent;
	JMapPane myPane;
	JMapFrame myFrame;
	private WMSLayer backdrop;
	JButton btn1;
	
	public MapView() {
		mapcontent = new MapContent();
		mapcontent.setTitle("Cycle route analysis client application");
		myFrame = new JMapFrame(mapcontent);
		myPane = myFrame.getMapPane();
		myFrame.enableStatusBar(true);
		myFrame.enableToolBar(true);
		//myFrame.enableLayerTable(true);
        JToolBar toolBar = myFrame.getToolBar();
        btn1 = new JButton("Get risk data");
        toolBar.addSeparator();
        toolBar.add(btn1);
        myFrame.setExtendedState(myFrame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		
	}
	
	public void setBackdrop(WMSLayer backdrop) {
		this.backdrop = backdrop;
	}
	
	public void addRiskLayer(FeatureLayer riskLayer) {
		mapcontent.addLayer(riskLayer);
		myPane.repaint();
	}
	
	public void displayMap() {
		CoordinateReferenceSystem myCRS = backdrop.getCoordinateReferenceSystem();    
		//MapContent mapcontent = new MapContent();
		//mapcontent.setTitle("Cycle route analysis client application"); 
		mapcontent.addLayer(backdrop);
		//JMapFrame myFrame = new JMapFrame(mapcontent);
		//JMapPane myPane = myFrame.getMapPane();
		ReferencedEnvelope outbounds = new ReferencedEnvelope(-77278, 45656, 6689116, 6734899, myCRS);
		myPane.setDisplayArea(outbounds);
		//myFrame.enableStatusBar(true);
		//myFrame.enableToolBar(true);
        //JToolBar toolBar = myFrame.getToolBar();
        //btn1 = new JButton("Get accident risk");
        //toolBar.addSeparator();
        //toolBar.add(btn1);
        //JButton btn2 = new JButton("Get pollution risk");
        //toolBar.addSeparator();
        //toolBar.add(btn2);
        
		//myFrame.setExtendedState(myFrame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		myFrame.setVisible(true);
	}
	File chooseFile() {
        File file = JFileDataStoreChooser.showOpenFile("shp", null);
        if (file == null) {
            return null;
        }
        return file;
	}
		
	void addLoadBtnListener(ActionListener listenForLoadBtn) {
		btn1.addActionListener(listenForLoadBtn);

	}
}
