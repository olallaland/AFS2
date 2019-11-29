package client.command;

import alpha.File;
import alpha.FileManager;
import alpha.IFileMeta;
import alpha.exception.ErrorCode;
import alpha.id.StringId;
import client.fmlayer.FMClient;
import client.fmlayer.FileImpl;

import java.io.UnsupportedEncodingException;

public class ReadFileCmd extends Command {
	public ReadFileCmd(String[] cmds) {
		//1. 检查用户指令输入的的正确性（参数个数以及文件是否存在）
		if(cmds.length != 6) {
			throw new ErrorCode(7);
		} else {
			try {
				readFileContent(cmds);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}	
	}

	private void readFileContent(String[] cmds) {
		int where = 0;
		int offset = 0;
		int length = 0;
		String sfmId = cmds[1];
		String filename = cmds[2];
		byte[] byteContent;
		String fmStringId = "";
		FileManager fm = null;
		File file = null;
		String fileData = "";
		IFileMeta fileMeta = null;
		
		try {
			//获得读取的起始位置
			where = Integer.valueOf(cmds[3]);
			//获得指针移动的偏移量
			offset = Integer.valueOf(cmds[4]);
			//获得要读取的长度
			length = Integer.valueOf(cmds[5]);
		} catch(Exception e ) {
			throw new ErrorCode(8);
		}

		try {
			fm = new FMClient(new StringId(sfmId));
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
			System.out.println("the filemeta: " + fileMeta.toString());

			localFile.move(offset, where);
			byteContent = localFile.read(length);

		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		} catch (Exception e) {
			throw new ErrorCode(1000);
		}

		try {
			fileData = new String(byteContent, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new ErrorCode(6);
		}

		System.out.println(fileData);
	}

}
