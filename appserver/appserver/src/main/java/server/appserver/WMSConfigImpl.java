package server.appserver;

import common.appcommon.WMSConfigInterface;

public class WMSConfigImpl implements WMSConfigInterface {

	private String url;
	private String layerName;
	
	public WMSConfigImpl(String url, String layerName) {
		this.url = url;
		this.layerName = layerName;
	}
	
	public void setUrl(String url) {
		this.url = url;	
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;	
	}

	public String getUrl() {
		return url;
	}

	public String getLayerName() {
		return layerName;
	}

}
