package server.fmlayer;

import alpha.File;
import alpha.FileManager;
import alpha.IFileMeta;
import alpha.Id;
import alpha.exception.ErrorCode;
import alpha.id.StringId;

import java.io.Serializable;

public class FileImpl implements File, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -924457163328607900L;
	String fileData;
	IFileMeta fileMeta;
	long pointer = 0;
	Id fileId;
	long fileSize = 0;
	
	public FileImpl(IFileMeta fileMeta) {
		this.fileMeta = fileMeta;
		this.fileId = fileMeta.getFileId();
		this.fileSize = fileMeta.getFileSize();
	}
	
	public FileImpl(Id fileId) {
		this.fileId = fileId;
	}
	
	@Override
	public Id getFileId() {
		return fileMeta.getFileId();
	}

	@Override
	public IFileMeta getFileMeta() { return this.fileMeta; }

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

	@Override
	public FileManager getFileManager() {
		return null;
	}

	@Override
	public byte[] read(int length) {
		System.out.println("this is server read call");
		return null;
	}

	@Override
	public void write(byte[] bytes) {
		System.out.println("this is server read call");
	}

	@Override
	public long move(long offset, int where) {
		switch(where) {
			case MOVE_HEAD:
				pointer = offset;
				break;
			case MOVE_CURR:
				pointer += offset;
				break;
			case MOVE_TAIL:
				pointer = size() + offset;
				break;
			default:
				throw new ErrorCode(11);
		}
		if(pointer < 0) {
			throw new ErrorCode(11);
		}
		return pointer;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	@Override
	public long size() {
		return fileMeta.getFileSize();
	}

	@Override
	public void setSize(long newSize) {
		byte[] oldContent = read((int) fileMeta.getFileSize());
		byte[] newContent = new byte[(int) newSize];
		long oldSize = oldContent.length;
		int diff = (int) (newSize - oldSize);

		//原文件长度小于或等于新的长度，在后面加0x00
		if(diff >= 0) {
			System.arraycopy(oldContent, 0, newContent, 0, (int) oldSize);
			for(int i = 0; i < diff; i++) {
				newContent[(int) (oldSize + i)] = 0x00;
			}
		} else {
			System.arraycopy(oldContent, 0, newContent, 0, (int) newSize);
		}
		fileMeta.getLogicBlocks().clear();
		//subWrite(newContent, 0, newSize);

	}

	public void setFileMeta(FileMeta fileMeta) {
		this.fileMeta = fileMeta;	
	}

}
