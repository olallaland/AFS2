package client.command;

import alpha.File;
import alpha.FileManager;
import alpha.IFileMeta;
import alpha.exception.ErrorCode;
import client.fmlayer.FMClient;
import client.fmlayer.FileImpl;
import client.fmlayer.FileManagerImpl;
import alpha.id.StringId;

public class SetFileSizeCmd extends Command {
	public SetFileSizeCmd(String[] cmds) throws Exception {
		if(cmds.length != 4) {
			throw new ErrorCode(7);
		} else {
			try {
				setFileSize(cmds);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private void setFileSize(String[] cmds) {
		String filename = cmds[2];
		String sfmId = cmds[1];
		long length = 0;
		String fmStringId = "";
		FileManager fm;
		File file;
		IFileMeta fileMeta;
		long oldPointer;

		try {
			length = Long.valueOf(cmds[3]);
		} catch (Exception e) {
			throw new ErrorCode(8);
		}
		if(length < 0) {
			throw new ErrorCode(17);
		}

		System.out.println("new length:" + length);
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
			oldPointer = localFile.pos();
			localFile.move(0, 1);
			localFile.setSize(length);
			localFile.move(oldPointer, 1);

		} catch(ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		} catch (Exception e) {
			throw new ErrorCode(1000);
		}

	}
}
