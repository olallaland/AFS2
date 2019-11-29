package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class Server extends UnicastRemoteObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6336636414997870348L;

	protected Server() throws RemoteException {
		super();
	}
	
}
