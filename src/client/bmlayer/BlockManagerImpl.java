package client.bmlayer;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import alpha.Block;
import alpha.BlockManager;
import alpha.constant.FileConstant;
import alpha.exception.ErrorCode;
import alpha.Id;
import alpha.id.IntegerId;
import alpha.id.StringId;
import alpha.util.FileUtil;
import alpha.util.SerializeUtil;

public class BlockManagerImpl implements BlockManager, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6437111830025101381L;
	Id bmId;
	HashMap<Id, BlockImpl> blockSet = new HashMap<Id, BlockImpl>();
	
	public BlockManagerImpl(Id bmId) {
		this.bmId = bmId;	
	}

	public BlockManagerImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Block getBlock(Id indexId) {
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

		return block;
	}
	
	public String getStringBmId() {
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
	public Block newBlock(byte[] b) {
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
	
	public int addBlock(BlockImpl b) {
		blockSet.put(b.getIndexId(), b);
		return 0;
	}

	private void addBlockToList(int blockId) {
		//1. 如果对应fm的目录下没有fileList文件，则创建一个
		//2. 将filename按行写入fileList
		String path = FileConstant.BM_CWD + FileConstant.PATH_SEPARATOR + getStringBmId()
				+ FileConstant.PATH_SEPARATOR + "blockList.txt";
		try {
			FileUtil.writeByLine(path, blockId + "");
		} catch(RuntimeException e) {
			throw new ErrorCode(1);
		}
	}

	private byte[] getBlockData(int id) {
		byte[] blockData;

		String path = FileConstant.BM_CWD + FileConstant.PATH_SEPARATOR + getStringBmId()
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

		String path = FileConstant.BM_CWD + FileConstant.PATH_SEPARATOR + getStringBmId()
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

}
