package client.command;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import client.fmlayer.FMClient;
import alpha.exception.ErrorCode;
import alpha.File;
import alpha.IFileMeta;
import alpha.id.StringId;

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
		// 1. ������Ӧ��FMClient
		// 2. ����newFile()����
		// 3. ���ش������

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
