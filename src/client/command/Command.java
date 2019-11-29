package client.command;

import java.util.HashMap;
import java.util.LinkedList;

import alpha.File;
import client.bmlayer.BlockManagerImpl;
import alpha.constant.FileConstant;
import alpha.exception.ErrorCode;
import client.fmlayer.FileImpl;
import client.fmlayer.FileManagerImpl;
import client.fmlayer.FileMeta;
import alpha.Id;
import alpha.id.StringId;
import alpha.util.FileUtil;
import alpha.util.SerializeUtil;

public abstract class Command {
	
	public static HashMap<String, BlockManagerImpl> currBlockManagers = 
			new HashMap<String, BlockManagerImpl>();
	public static HashMap<String, FileManagerImpl> currFileManagers = 
			new HashMap<String, FileManagerImpl>();
	public static HashMap<String, FileImpl> openedFileSet = new HashMap<String, FileImpl>();

	Command(String cmdName, String filename) {
		
	}
	
	Command(String filename) {
		
	}
	Command() {
		
	}
	
	static FileMeta findFile(String filename) {
		StringBuilder path = new StringBuilder();
		byte[] serializedContent;
		if(FileUtil.exists(filename + FileConstant.META_SUFFIX, path)) {
			//获得filemeta文件内容
			serializedContent = FileUtil.reads(path.toString());
			FileMeta fileMeta = SerializeUtil.deserialize(FileMeta.class, serializedContent);
			return fileMeta;
		} else {
			throw new ErrorCode(4);
		}
	}

	/**
	 * 为新建文件随机分配一个file manager
	 * @return
	 */
	static Id allocFm() {
		int fmIndex = (int) (Math.random() * FileConstant.FM_COUNT + 1);
		String sid = "fmlayer-" + fmIndex;
		FileManagerImpl fm;

		// 查询分配的fm对象是否已创建
		if (currFileManagers.containsKey(sid)) {
			fm = currFileManagers.get(sid);
		} else {
			fm = new FileManagerImpl(new StringId(sid));
			currFileManagers.put(sid, fm);
		}
		//System.out.println("choose a fmlayer :" + sid);
		return new StringId(sid);
	}

	/**
	 * 根据fmId获得FileManager对象
	 * @param fmId
	 * @return
	 */
	static FileManagerImpl getFmById(Id fmId) {
		String sfmId = "";
		if (fmId instanceof StringId) {
			StringId sid = (StringId) fmId;
			sfmId = sid.getId();
		} else {
			throw new ErrorCode(13);
		}
		FileManagerImpl fm = null;

		if (currFileManagers.containsKey(sfmId)) {
			fm = currFileManagers.get(sfmId);

		} else {
			fm = new FileManagerImpl(fmId);
			currFileManagers.put(sfmId, fm);
		}

		return fm;
	}

	/**
	 * 为新开的块随机分配BlockManager
	 * @return
	 */
	public static BlockManagerImpl allocBm() {
		int bmIndex = (int) (Math.random() * FileConstant.BM_COUNT + 1);
		String sbmId = "bm-" + bmIndex;
		BlockManagerImpl bm = null ;
		
		//查询分配的bm对象是否已创建
		if(currBlockManagers.containsKey(sbmId)) {
			bm = currBlockManagers.get(sbmId);
		} else {
			bm = new BlockManagerImpl(new StringId(sbmId));
			currBlockManagers.put(sbmId, bm);
		}
		return bm;
	}

	/**
	 * 根据filename从fileList中找到其对应的file manager
	 * @param filename
	 * @return
	 */
	static String findFm(String filename) {
		String fm = "";
		//String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR;
		//String fmName = "fmlayer-";
		LinkedList<String> list = new LinkedList<String>();

		for(int i = 1; i <= FileConstant.FM_COUNT; i++) {
			String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR;
			String fmName = "fmlayer-";
			fmName += i;
			path += fmName + FileConstant.PATH_SEPARATOR + "fileList.txt";
			try {
				list = FileUtil.readByLine(path);
			} catch(RuntimeException e) {
				throw new ErrorCode(1);
			}

			for(int k = 0; k < list.size(); k++) {

				if(list.get(k).equals(filename)) {
					return fmName;
				}
			}
		}

		//文件未创建
		throw new ErrorCode(4);
	}

	/**
	 * 根据blockId从blockList中找到其对应的block manager
	 * @param blockId
	 * @return
	 */
	static String findBm(String blockId) {
		//String path = FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR;
		//String fmName = "fmlayer-";
		LinkedList<String> list = new LinkedList<String>();

		for(int i = 1; i <= FileConstant.BM_COUNT; i++) {
			String path = FileConstant.BM_CWD + FileConstant.PATH_SEPARATOR;
			String bmName = "bm-";
			bmName += i;
			path += bmName + FileConstant.PATH_SEPARATOR + "blockList.txt";
			try {
				list = FileUtil.readByLine(path);
			} catch(RuntimeException e) {
				System.out.println(e.getMessage());
			}

			for(int k = 0; k < list.size(); k++) {

				if(list.get(k).equals(blockId)) {
					return bmName;
				}
			}
		}

		throw new ErrorCode(5);
	}
	
	
}
