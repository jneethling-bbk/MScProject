package server.appserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import common.appcommon.AppServiceInterface;
import common.appcommon.WMSConfigInterface;

public class AppServiceImpl extends UnicastRemoteObject implements AppServiceInterface {

	private String wmsUrlString = "http://ows.terrestris.de/osm/service?Service=WMS&Version=1.1.1&Request=GetCapabilities";
	private String wmsLayerName = "OSM-WMS";
	private WMSConfigInterface config = new WMSConfigImpl(wmsUrlString, wmsLayerName);
	
	protected AppServiceImpl() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = -7555554854290536269L;

	public String getWMSURLString() throws RemoteException {
		return config.getUrl();
	}

	public String getWMSLayerName() throws RemoteException {
		return config.getLayerName();
	}
	

}
