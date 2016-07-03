package common.appcommon;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AppServiceInterface extends Remote {

	String getURLString() throws RemoteException;
}
