package client.command;

import alpha.File;
import alpha.FileManager;
import alpha.IFileMeta;
import alpha.exception.ErrorCode;
import alpha.id.StringId;
import client.fmlayer.FMClient;
import client.fmlayer.FileImpl;
import client.fmlayer.FileMeta;

public class CopyFileCmd extends Command {
	public CopyFileCmd(String[] cmds) {
		//1. 检查用户指令输入的的正确性（参数个数以及文件是否存在）
		if(cmds.length != 5) {
			throw new ErrorCode(7); 
		} else {
			try {
				copyFile(cmds);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}	
	}

	private void copyFile(String[] cmds) {
		String srcFmStringId = "";
		String destFmStringId = "";
		String srcFMId = cmds[1];
		String srcFilename = cmds[2];
		String destFMId = cmds[3];
		String destFilename = cmds[4];
		IFileMeta srcFileMeta;
		IFileMeta destFileMeta = new FileMeta();
		FileManager srcFm = null;
		FileManager destFm = null;
		File srcFile = null;
		File destFile = null;
		FileImpl srcLocalFile = null;

		try {
			srcFm = new FMClient(new StringId(srcFMId));
			//System.out.println(fmlayer.getStringFmId());
			srcFile = srcFm.getFile(new StringId(srcFilename));

			// file is already opened
			if(openedFileSet.containsKey(srcFilename)) {
				srcLocalFile = openedFileSet.get(srcFilename);
			} else {
				srcLocalFile = new FileImpl(srcFile);
			}
			srcFileMeta = srcFile.getFileMeta();
		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		} catch (Exception e) {
			throw new ErrorCode(1000);
		}

		try {
			destFm = new FMClient(new StringId(destFMId));
			destFile = destFm.newFile(new StringId(destFilename));
			srcLocalFile.copy(destFile);
		} catch(ErrorCode e) {
			//System.out.println(e.getMessage());
			throw new ErrorCode(e.getErrorCode());
		} catch (Exception e) {
			throw new ErrorCode(1000);
		}

	}
}
