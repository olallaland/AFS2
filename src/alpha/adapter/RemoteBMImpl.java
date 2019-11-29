package alpha.adapter;

import alpha.Block;
import alpha.IRemoteBM;
import alpha.Id;

public class RemoteBMImpl implements IRemoteBM {

	@Override
	public Block getBlock(Id indexId) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("I am a adapter!");
		return null;
	}

	@Override
	public Block newBlock(byte[] b) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("I am a adapter!");
		return null;
	}

	@Override
	public String getbmId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
