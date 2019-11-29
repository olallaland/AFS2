package server.bmlayer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;

import alpha.Block;
import alpha.constant.FileConstant;
import alpha.exception.ErrorCode;
import alpha.Id;
import alpha.id.IntegerId;
import alpha.id.StringId;
import alpha.IRemoteBM;
import alpha.util.FileUtil;
import alpha.util.SerializeUtil;
import server.Server;

public class BMServer extends Server implements IRemoteBM {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Id bmId;
	HashMap<Id, BlockImpl> blockSet = new HashMap<Id, BlockImpl>();
	
	protected BMServer() throws RemoteException {
		super();
	}
	
	
	public BMServer(Id bmId) throws RemoteException {
		this.bmId = bmId;
		
	}

	@Override
	public Block getBlock(Id indexId) throws RemoteException {
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			System.out.println(e.getMessage());
//		}
		int id;
		BlockMeta blockMeta = null;
		BlockImpl block;
		byte[] blockData;

		if(indexId instanceof IntegerId) {
			IntegerId iid = (IntegerId) indexId;
			id = iid.getId();

		} else {
			throw new ErrorCode(13);
		}
		try {
			blockData = getBlockData(id);
			blockMeta = getBlockMeta(id);
		} catch (RuntimeException e) {
			throw new ErrorCode(15);
		}
		block = new BlockImpl(indexId, bmId, blockData, blockMeta);
		
		if(block.isValid()) {
			return block;
		} else {
			throw new ErrorCode(2);
		}
		
	}

	@Override
	public Block newBlock(byte[] b) throws RemoteException {
		//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			System.out.println(e.getMessage());
//		}
		int count = 0;
		BlockImpl block = new BlockImpl();
		try {
			count = FileUtil.readIdCount();
			block = new BlockImpl(new IntegerId(count), bmId, b);
			block.write();
			blockSet.put(new IntegerId(count), block);
			addBlockToList(count);
			
			count++;
			FileUtil.updateIdCount(count);			
		} catch (IOException e1) {
			throw new ErrorCode(1);
		}
		return block;
	}
	
	private void addBlockToList(int blockId) {
		//1. 如果对应fm的目录下没有fileList文件，则创建一个
		//2. 将filename按行写入fileList
		String path = FileConstant.BM_CWD + FileConstant.PATH_SEPARATOR + getStringBMId()
				+ FileConstant.PATH_SEPARATOR + "blockList.txt";
		try {
			FileUtil.writeByLine(path, blockId + "");
		} catch(RuntimeException e) {
			throw new ErrorCode(1);
		}
	}
	
	private byte[] getBlockData(int id) {
		byte[] blockData;

		String path = FileConstant.BM_CWD + FileConstant.PATH_SEPARATOR + getStringBMId()
				+ FileConstant.PATH_SEPARATOR + id + FileConstant.DATA_SUFFIX;

		if(FileUtil.exists(path)) {
			//获得blockData文件内容
			blockData = FileUtil.reads(path);
			return blockData;
		} else {
			throw new ErrorCode(4);
		}
	}

	private BlockMeta getBlockMeta(int id) {
		byte[] bytes;
		BlockMeta blockMeta;

		String path = FileConstant.BM_CWD + FileConstant.PATH_SEPARATOR + getStringBMId()
				+ FileConstant.PATH_SEPARATOR + id + FileConstant.META_SUFFIX;

		if(FileUtil.exists(path)) {
			//获得blockmeta文件内容
			bytes = FileUtil.reads(path);
			blockMeta = SerializeUtil.deserialize(BlockMeta.class, bytes);
			return blockMeta;
		} else {
			throw new ErrorCode(4);
		}
	}
	
	public String getStringBMId() {
		if(bmId instanceof StringId) {
			StringId sid = (StringId) bmId;
			String id = sid.getId();
			return id;
		} else {
			//TODO
			return "";
		}
	}


	@Override
	public String getbmId() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
