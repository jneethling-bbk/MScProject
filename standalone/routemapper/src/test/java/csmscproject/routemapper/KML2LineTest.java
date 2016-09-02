package csmscproject.routemapper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class KML2LineTest {

	private KML2LineImpl testInstance;
	
	@Test(expected = IllegalArgumentException.class)
	public final void setFileNullTest() {
		
		testInstance = new KML2LineImpl();
		testInstance.setDOM(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public final void setCrsNullTest() {
		
		testInstance = new KML2LineImpl();
		testInstance.setCRS(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public final void getLineWithDomNotSetTest() {
		
		testInstance = new KML2LineImpl();
		testInstance.setCRS(null);
		try {
			testInstance.getLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public final void getLineValidTest() {
		
		String xmlString = "<LineString><tessellate>1</tessellate><coordinates>0.07083576259125257,51.50025302339641,0 0.07074415189870065,51.50044077608111,0</coordinates></LineString>";		
		DocumentBuilder db = null;
		Document doc = null;
		
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlString));

		try {
			doc = db.parse(is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CoordinateReferenceSystem testCRS = null;
		try {
			testCRS = CRS.decode("EPSG:3857");
		} catch (NoSuchAuthorityCodeException e1) {
			e1.printStackTrace();
		} catch (FactoryException e1) {
			e1.printStackTrace();
		}
		testInstance = new KML2LineImpl();
		testInstance.setCRS(testCRS);
		testInstance.setDOM(doc);
		DefaultFeatureCollection lines = null;
		try {
			lines = testInstance.getLine();
			assertTrue(lines.getCount() == 1);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
}
