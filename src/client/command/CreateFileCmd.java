package client.command;

import alpha.File;
import alpha.IFileMeta;
import alpha.exception.ErrorCode;
import alpha.id.StringId;
import client.fmlayer.FMClient;

public class CreateFileCmd extends Command {
	public CreateFileCmd(String[] cmds) throws Exception {
		if(cmds.length != 3) {
			throw new ErrorCode(7);
		} else {
			try {
				createFile(cmds[1], cmds[2]);
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	private static void createFile(String stringFmId, String filename) throws Exception {
		// 1. 创建对应的FMClient
		// 2. 调用newFile()方法
		// 3. 返回创建结果

		FMClient fmc = new FMClient(new StringId(stringFmId));
		File file = null;
		
		try {
			file = fmc.newFile(new StringId(filename));
		} catch(ErrorCode e) {
			//System.out.println(e.getMessage());
			throw new ErrorCode(e.getErrorCode());
		}
		
		IFileMeta fileMeta = file.getFileMeta();
		
		System.out.println("create file " + fileMeta.getStringFileId() + " in manager " + fileMeta.getStringFmId());

	}
	
}
