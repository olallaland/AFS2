package alpha.adapter;

import alpha.Block;
import alpha.IRemoteBM;
import alpha.Id;

import java.rmi.RemoteException;

public class RemoteBMImpl implements IRemoteBM {

	@Override
	public Block getBlock(Id indexId) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("I am a adapter!");
		return null;
	}

	@Override
	public Block newBlock(byte[] b) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("I am a adapter!");
		return null;
	}

	@Override
	public String getbmId() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
