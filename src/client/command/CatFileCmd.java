package client.command;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.LinkedList;

import client.bmlayer.BlockImpl;
import client.fmlayer.FMClient;
import alpha.exception.ErrorCode;
import alpha.File;
import client.fmlayer.FileImpl;
import alpha.FileManager;
import alpha.IFileMeta;
import alpha.id.StringId;

public class CatFileCmd extends Command {
	public CatFileCmd(String[] cmds) {
		//1. 检查用户指令输入的的正确性（参数个数以及文件是否存在）
		if(cmds.length != 3) {
			throw new ErrorCode(7); 
		} else {
			try {
				catFileContent(cmds[1], cmds[2]);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}	
	}
	
	public static void catFileContent(String sfmId, String filename) throws Exception {
		IFileMeta fileMeta = null;
		File file = null;
		String fileData = "";
		byte[] byteContent = null;
		String fmStringId = "";
		FileManager fm = null;
		long oldPointer;

		HashMap<Integer, LinkedList<BlockImpl>> logicBlocks = new HashMap<Integer, LinkedList<BlockImpl>>();
		System.out.println("the input fmlayer id: " + sfmId);
		System.out.println("the input filename " + filename);

		try{
			fm = new FMClient(new StringId(sfmId));
			//System.out.println(fmlayer.getStringFmId());
			file = fm.getFile(new StringId(filename));

			FileImpl localFile;
			// file is already opened
			if(openedFileSet.containsKey(filename)) {
				localFile = openedFileSet.get(filename);
			} else {
				localFile = new FileImpl(file);
				openedFileSet.put(filename, localFile);
			}

			fileMeta = file.getFileMeta();

			oldPointer = localFile.pos();
			localFile.move(0, 1);
			System.out.println("the file metadata " + fileMeta.toString());
			byteContent = localFile.read((int)fileMeta.getFileSize());
			fileData = new String(byteContent, "utf-8");
			System.out.println("the file content: " + fileData);

			//恢复cat之前的指针位置
			file.move(oldPointer, 1);

		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		}
	}

}
