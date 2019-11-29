package alpha;

import java.rmi.Remote;

public interface IRemoteFM extends Remote {
	File getFile(Id fileId) throws Exception;
	File newFile(Id fileId) throws Exception;
	String getfmId() throws Exception;
	void updateFile(IFileMeta fileMeta) throws Exception;
	String deleteFile(Id fileId) throws Exception;
}
