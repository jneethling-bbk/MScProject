package server.appserver;

import java.awt.FlowLayout;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class AppServerStart extends JFrame {

	private static final long serialVersionUID = -8678802316026376814L;
	
	public AppServerStart() {
		super("Route Analysis Application Server");
		setSize(400, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		AppServerStart ass = new AppServerStart();
		ass.launch(ass);
	}
	private void launch(AppServerStart ass) {
	// 1. If there is no security manager, start one
	if (System.getSecurityManager() == null) {
		System.setSecurityManager(new SecurityManager());
	}
	try {
		// 2. Create the registry if there is not one
		LocateRegistry.createRegistry(1099);
		// 3. Create the server object
		AppServiceImpl server = new AppServiceImpl();
		// 4. Register (bind) the server object on the registy.
		// The registry may be on a different machine
		String registryHost = "//localhost/";
		String serviceName = "route_analysis";
		Naming.rebind(registryHost + serviceName, server);
		
		JLabel servermsg = new JLabel("The server is up");
		FlowLayout flo = new FlowLayout();
		ass.setLayout(flo);
		ass.add(servermsg);
		ass.setVisible(true);
		
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}

}
