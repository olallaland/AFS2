package server.bmlayer;

import java.io.Serializable;

import alpha.constant.FileConstant;
import alpha.exception.ErrorCode;
import alpha.util.FileUtil;
import alpha.util.SerializeUtil;

public class BlockMeta implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1936056658480287561L;
	int blockSize = FileConstant.BLOCK_SIZE;
	long checkSum;
	
	BlockMeta(long checkSum) {
		this.checkSum = checkSum;
	}
	
	public long getCheckSum() {
		return this.checkSum;
	}
	
	int write(String path) throws Exception {
		byte[] bytes = SerializeUtil.toBytes(this);
		try {
			FileUtil.writes(bytes, path);
		} catch(RuntimeException e) {
			throw new ErrorCode(1);
		}

		return 0;
	}
}
