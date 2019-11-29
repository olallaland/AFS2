package server.fmlayer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import alpha.constant.FileConstant;
import alpha.exception.ErrorCode;
import alpha.File;
import alpha.IFileMeta;
import alpha.Id;
import alpha.id.StringId;
import alpha.IRemoteFM;
import alpha.util.FileUtil;
import alpha.util.SerializeUtil;
import server.Server;

public class FMServer extends Server implements IRemoteFM {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Id fmId;
	HashMap<String, FileImpl> fileSet = new HashMap<String, FileImpl>();

	protected FMServer() throws RemoteException {
		super();

	}

	/**
	 * 在启动FM Server时就把其所有管理的file列出来
	 */
	public FMServer(Id fmId) throws RemoteException {
		this.fmId = fmId;
		List<String> fileNameSet = new LinkedList<String>();
		String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR;
		path += getStringFMId() + FileConstant.PATH_SEPARATOR + "fileList.txt";

		fileNameSet = FileUtil.readByLine(path);
		for(int i = 0; i < fileNameSet.size(); i++) {
			fileSet.put(fileNameSet.get(i), null);
			System.out.println("file in " + getStringFMId() + ": " + fileNameSet.get(i));
		}
		
	}

	@Override
	public File getFile(Id fileId) {
		String id = "";
		IFileMeta fileMeta = null;
		if(fileId instanceof StringId) {
			StringId sid = (StringId) fileId;
			id = sid.getId();

		} else {
			throw new ErrorCode(13);
		}

		if(fileSet.containsKey(id) && fileSet.get(id) != null) {
            System.out.println(fileSet.get(id).getFileMeta());
			return fileSet.get(id);
		} else if (!fileSet.containsKey(id)){
			throw new ErrorCode(4);
		} else {
			try {
				fileMeta = getFileMeta(id);
				System.out.println("lalala method in fmlayer server, my filemeta is " + fileMeta);
			} catch (RuntimeException e) {
				throw new ErrorCode(12);
			}

			FileImpl file = new FileImpl(fileMeta);
			fileSet.put(id, file);
			return file;
		}
	}

	@Override
	public File newFile(Id fileId) throws Exception {
		String id = "";
		FileImpl file = null;
		if(fileId instanceof StringId) {
			StringId sid = (StringId) fileId;
			id = sid.getId();	
		} else {
			throw new ErrorCode(13);
		}
		
		if(fileSet.containsKey(id)) {
			throw new ErrorCode(3);
		} else {
			
			try {
				FileMeta fileMeta = new FileMeta(fileId, fmId);
				System.out.println("create file in server, and filemeta is: " + fileMeta.toString());
				file = new FileImpl(fileMeta);
				fileSet.put(id, file);
				addFileToList(id);
			} catch(ErrorCode e) {
				throw new ErrorCode(e.getErrorCode());
			}
		}

		return file;
	}
	
	IFileMeta getFileMeta(String filename) {

		byte[] serializedContent;
		String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR + getStringFMId()
				+ FileConstant.PATH_SEPARATOR + filename + FileConstant.META_SUFFIX;
		System.out.println("path~~ " + path);
		if(FileUtil.exists(path)) {
			//获得filemeta文件内容
			serializedContent = FileUtil.reads(path);
			IFileMeta fileMeta = SerializeUtil.deserialize(IFileMeta.class, serializedContent);
			System.out.println(fileMeta.toString());
			return fileMeta;
		} else {
			System.out.println("filemeta not exists!");
			throw new ErrorCode(4);
		}
	}
	
	public String getStringFMId() {
		if(fmId instanceof StringId) {
			StringId sid = (StringId) fmId;
			String id = sid.getId();
			return id;
		} else {
			return "";
		}
	}

	public void addFileToList(String filename) {
		//1. 如果对应fm的目录下没有fileList文件，则创建一个
		//2. 将filename按行写入fileList
		String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR + getStringFMId()
				+ FileConstant.PATH_SEPARATOR + "fileList.txt";
		try {
			FileUtil.writeByLine(path, filename);
		} catch(ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		}
	}
	
	@Override
	public String getfmId() {
		// TODO Auto-generated method stub
		return getStringFMId();
	}

	@Override
	public void updateFile(IFileMeta fileMeta) throws Exception {
		System.out.println(fileMeta.getClass());
		System.out.println("lalala update filemeta in server, and file meta: " + fileMeta.toString());
		String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR + getStringFMId() +
				FileConstant.PATH_SEPARATOR + fileMeta.getStringFileId() + FileConstant.META_SUFFIX;
		
		System.out.println("lalala update filemeta in server: " + path);
		byte[] bytes;
		try {
			bytes = SerializeUtil.toBytes(fileMeta);
		} catch (Exception e) {
			throw new ErrorCode(14);
		}
		try {
			FileUtil.writes(bytes, path);
		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		}

		fileSet.put(fileMeta.getStringFileId(), new FileImpl(fileMeta));
		//System.out.println(SerializeUtil.toBytes(this, path));
	}

	@Override
	public String deleteFile(Id fileId) throws Exception {
		System.out.println("the filename to delete:" + fileId.getId());
		String result = "";
		String filename = (String)fileId.getId();
		System.out.println("the filename to delete:" + filename);
		try {
			fileSet.remove(filename);
			deleteFromList(filename);
			deleteFileMeta(filename);

			result = "success in deleting file: " + filename;
		} catch(ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		}
		return result;
	}

	/**
	 * delete record in fileList.txt
	 * @param filename
	 */
	private void deleteFromList(String filename) {
		String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR + getStringFMId()
				+ FileConstant.PATH_SEPARATOR + "fileList.txt";
		try {
			FileUtil.deleteLine(path, filename);
		} catch (ErrorCode e) {
			throw new ErrorCode(4);
		}
	}

	/**
	 * delete fileMeta file
	 * @param filename
	 */
	private void deleteFileMeta(String filename) {

		String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR + getStringFMId()
				+ FileConstant.PATH_SEPARATOR + filename + FileConstant.META_SUFFIX;
		try {
			FileUtil.deleteFile(path);
		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		}
	}

}
