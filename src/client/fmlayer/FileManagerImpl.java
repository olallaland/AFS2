package client.fmlayer;

import java.io.Serializable;
import java.util.HashMap;

import alpha.File;
import alpha.FileManager;
import alpha.constant.FileConstant;
import alpha.exception.ErrorCode;
import alpha.Id;
import alpha.id.StringId;
import alpha.util.FileUtil;
import alpha.util.SerializeUtil;

public class FileManagerImpl implements FileManager, Serializable {
	Id fmId;
	HashMap<String, FileImpl> fileSet;
	
	public FileManagerImpl(Id fmId) {
		this.fmId = fmId;
		fileSet = new HashMap<String, FileImpl>();
	}
	
	public Id getFmId() {
		return this.fmId;
	}
	
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
     * 序列化ID
     */
    private static final long serialVersionUID = -5809782578272943999L;
	@Override
	public File getFile(Id fileId) {
		String id = "";
		FileMeta fileMeta = null;
		if(fileId instanceof StringId) {
			StringId sid = (StringId) fileId;
			id = sid.getId();

		} else {
			throw new ErrorCode(13);
		}

		if(fileSet.containsKey(id)) {
            System.out.println(fileSet.get(id).fileMeta);
			return fileSet.get(id);
		} else {
			try {
				fileMeta = getFileMeta(id);
				System.out.println(fileMeta);
			} catch (RuntimeException e) {
				throw new ErrorCode(12);
			}

			FileImpl file = new FileImpl(fileMeta);
			fileSet.put(id, file);
			return file;
		}
	}

	@Override
	public File newFile(Id fileId){
		String id = "";
		if(fileId instanceof StringId) {
			StringId sid = (StringId) fileId;
			id = sid.getId();	
		} else {
			
		}
		FileMeta fileMeta = new FileMeta(fileId, fmId);
		FileImpl file = new FileImpl(fileMeta);//在这里维护一个hashmap 保存所有已经创建对象的File
		fileSet.put(id, file);
		
		//fmlayer.write(destFilename);
		//fileId即为输入要创建的file的名字 由于没有目录结构，所以不允许同名 fileId是唯一的
		// 1. 检查是否有名为fileId的文件创建了 用FileUtil查询（上层检查？）
		// 2. 根据fmId在对应目录下创建对应的meta文件
		return file;
	}

	@Override
	public String deleteFile(Id fileId) throws Exception {
		return null;
	}

	public String deleteFile(FileImpl file) {
		//1. 从fileSet中移除file
		//2. 删除fileList的索引
		//3. 删除对应的fileMeta文件

		String result = "";
		String filename = file.getStringFileId();
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

	FileMeta getFileMeta(String filename) {

		byte[] serializedContent;
		String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR + getStringFmId()
				+ FileConstant.PATH_SEPARATOR + filename + FileConstant.META_SUFFIX;

		if(FileUtil.exists(path)) {
			//获得filemeta文件内容
			serializedContent = FileUtil.reads(path);
			FileMeta fileMeta = SerializeUtil.deserialize(FileMeta.class, serializedContent);
			return fileMeta;
		} else {
			throw new ErrorCode(4);
		}
	}

	public void addFileToList(String filename) {
		//1. 如果对应fm的目录下没有fileList文件，则创建一个
		//2. 将filename按行写入fileList
		String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR + getStringFmId()
				+ FileConstant.PATH_SEPARATOR + "fileList.txt";
		try {
			FileUtil.writeByLine(path, filename);
		} catch(ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		}
	}

	private void deleteFromList(String filename) {
		String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR + getStringFmId()
				+ FileConstant.PATH_SEPARATOR + "fileList.txt";
		try {
			FileUtil.deleteLine(path, filename);
		} catch (ErrorCode e) {
			throw new ErrorCode(4);
		}
	}

	private void deleteFileMeta(String filename) {

		String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR + getStringFmId()
				+ FileConstant.PATH_SEPARATOR + filename + FileConstant.META_SUFFIX;
		try {
			FileUtil.deleteFile(path);
		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		}

	}

}
