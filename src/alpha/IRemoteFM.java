package alpha;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteFM extends Remote {
	File getFile(Id fileId) throws RemoteException;
	File newFile(Id fileId) throws RemoteException;
	String getfmId() throws RemoteException;
	void updateFile(IFileMeta fileMeta) throws RemoteException;
	String deleteFile(Id fileId) throws RemoteException;
}
