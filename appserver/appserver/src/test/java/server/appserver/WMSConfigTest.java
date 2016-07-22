package server.appserver;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import common.appcommon.WMSConfigInterface;

public class WMSConfigTest {

	private WMSConfigInterface testConfig;
	private String inputUrl;
	private String inputLayerName;
	
	@Before
	public void setUp() throws Exception {
		inputUrl = "url to test constructor";
		inputLayerName = "layer name to test constructor";
		testConfig = new WMSConfigImpl(inputUrl, inputLayerName);
	}

	@Test
	public final void testGetUrlConstructor() {
		String output = testConfig.getUrl();
		assertEquals("Mismatch between expected and actual URL", inputUrl, output);
	}

	@Test
	public final void testGetLayerNameConstructor() {
		String output = testConfig.getLayerName();
		assertEquals("Mismatch between expected and actual layer name", inputLayerName, output);
	}
	
	@Test
	public final void testGetUrl() {
		testConfig.setUrl("another url test");
		String output = testConfig.getUrl();
		assertEquals("Mismatch between expected and actual URL", "another url test", output);
	}

	@Test
	public final void testGetLayerName() {
		testConfig.setLayerName("another layer name test");
		String output = testConfig.getLayerName();
		assertEquals("Mismatch between expected and actual layer name", "another layer name test", output);
	}

}
