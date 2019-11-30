package alpha;

import java.rmi.Remote;
import java.rmi.RemoteException;

import alpha.Block;
import alpha.Id;

public interface IRemoteBM extends Remote {
	Block getBlock(Id indexId) throws RemoteException;
	Block newBlock(byte[] b) throws RemoteException;
	String getbmId() throws RemoteException;
}
