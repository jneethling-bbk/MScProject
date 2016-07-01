package server.appserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AppServiceImpl extends UnicastRemoteObject implements AppServiceInterface {

	protected AppServiceImpl() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = -7555554854290536269L;

	public String getURLString() throws RemoteException {
		return "http://ows.terrestris.de/osm/service?Service=WMS&Version=1.1.1&Request=GetCapabilities";
	}

	
}
