package alpha;

import java.rmi.Remote;
import alpha.Block;
import alpha.Id;

public interface IRemoteBM extends Remote {
	Block getBlock(Id indexId) throws Exception;
	Block newBlock(byte[] b) throws Exception;
	String getbmId() throws Exception;
}
