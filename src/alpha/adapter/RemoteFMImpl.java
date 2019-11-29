package alpha.adapter;

import alpha.IFileMeta;
import alpha.IRemoteFM;
import alpha.File;
import alpha.Id;

public class RemoteFMImpl implements IRemoteFM {

	@Override
	public File getFile(Id fileId) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("I am a adapter!");
		return null;
	}

	@Override
	public File newFile(Id fileId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getfmId() throws Exception {
		System.out.println("I am an adapter!");
		return null;
	}

	@Override
	public void updateFile(IFileMeta fileMeta) {
		System.out.println("I am an adapter!");
	}

	@Override
	public String deleteFile(Id fileId) {
		System.out.println("I am an adapter!");
		return null;
	}

}
