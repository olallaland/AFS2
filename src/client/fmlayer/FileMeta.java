package client.fmlayer;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import client.bmlayer.BlockImpl;
import alpha.constant.FileConstant;
import alpha.exception.ErrorCode;
import alpha.IFileMeta;
import alpha.Id;
import alpha.id.StringId;
import alpha.util.FileUtil;
import alpha.util.SerializeUtil;

public class FileMeta implements Serializable, IFileMeta {
	long fileSize;
	int blockSize = FileConstant.BLOCK_SIZE;
	int blockCount = 0;
	Id fmId;
	String path;
	Id fileId;
	HashMap<Integer, LinkedHashMap<Id, Integer>> logicBlocks = new HashMap<Integer, LinkedHashMap<Id, Integer>>();
	private static final long serialVersionUID = -5248069984631225347L;

	public FileMeta() {
	}

	FileMeta(Id fileId, Id fmId) {
		this.fileId = fileId;
		this.fmId = fmId;
		this.fileSize = 0;
		this.blockCount = (int) ((fileSize % blockSize) == 0
				? (fileSize / blockSize) : (fileSize / blockSize + 1));
		this.path = setPath(fmId, fileId);
		FileUtil.createFile(path);
		try {
			write();
		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		}
		read();
	}

	@Override
	public void setFileSize(long totalFileSize) {
		this.fileSize = totalFileSize;
	}

	@Override
	public void setBlockCount(int blockCount) {
		this.blockCount = blockCount;
	}

	public String getPath() {
		return this.path;
	}

	@Override
	public long getFileSize() {
		return this.fileSize;
	}

	@Override
	public int getBlockCount() {
		return this.blockCount;
	}

	@Override
	public void setLogicBlocks(HashMap<Integer, LinkedHashMap<Id, Integer>> logicBlocks) {
		this.logicBlocks = logicBlocks;
	}


    public String setPath(Id fmId, Id fileId) {
		String newPath = "";

		newPath = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR + getStringFmId() +
				FileConstant.PATH_SEPARATOR + getStringFileId() + FileConstant.META_SUFFIX;

		return newPath;
	}

	@Override
	public HashMap<Integer, LinkedHashMap<Id, Integer>> getLogicBlocks() {
		return this.logicBlocks;
	}

	/**
	 * @return the fmId
	 */
	@Override
	public Id getFmId() {
		return this.fmId;
	}

	/**
	 * get the string fmId
	 * @return
	 */
	@Override
	public String getStringFmId() {
		if(fmId instanceof StringId) {
			StringId sid = (StringId) fmId;
			String id = sid.getId();
			return id;
		} else {
			//TODO
			return "";
		}
	}

	/**
	 * @param fmId the fmId to set
	 */
	public void setFmId(Id fmId) {
		this.fmId = fmId;
	}

	@Override
	public Id getFileId() {
		return this.fileId;
	}

	/**
	 * get the string fileId
	 * @return
	 */
	@Override
	public String getStringFileId() {
		if(fileId instanceof StringId) {
			StringId sid = (StringId) fileId;
			String id = sid.getId();
			return id;
		} else {
			//TODO
			return "";
		}
	}

	public void setFileId(Id fileId) {
		this.fileId = fileId;
	}

	/**
	 * 将fileMeta序列化并写入文件
	 * @return
	 */
	@Override
	public void write() {
		this.path = setPath(fmId, fileId);
		byte[] bytes;
		try {
			bytes = SerializeUtil.toBytes(this);
		} catch (Exception e) {
			throw new ErrorCode(14);
		}
		try {
			FileUtil.writes(bytes, path);
		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		}
		//System.out.println(SerializeUtil.toBytes(this, path));
		//FileUtil.write();
	}

	/**
	 * 反序列化并生成FileMeta对象
	 */
	@Override
	public void read() {
		FileMeta deserialize = SerializeUtil.deserialize(FileMeta.class, FileUtil.reads(path));
		System.out.println(deserialize);
	}

	@Override
	public String toString() {
		String str = "FileMeta{" +
				"fileId = " + getStringFileId() +
				", filesize = " + fileSize +
				", blockSize = " + blockSize +
				", blockCount = " + blockCount +
				", path = " + path +
				'}';
		for(Integer i : logicBlocks.keySet()) {
			HashMap<Id, Integer> duplicates = logicBlocks.get(i);
			str += "\n" + i;
			for(Id bm : duplicates.keySet()) {
				str += ": [" + bm.toString() + ", " + duplicates.get(bm) + "]" ;
			}
		}

		return str;
	}

	@Override
	public void addLogicBlocks(int index, LinkedHashMap<Id, Integer> list) {

		logicBlocks.put(index, list);

	}

}
