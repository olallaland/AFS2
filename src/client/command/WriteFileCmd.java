package client.command;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import alpha.File;
import client.fmlayer.FMClient;
import alpha.exception.ErrorCode;
import client.fmlayer.FileImpl;
import client.fmlayer.FileManagerImpl;
import alpha.IFileMeta;
import alpha.id.StringId;

public class WriteFileCmd extends Command {
	
	public WriteFileCmd(String[] cmds) throws UnsupportedEncodingException {
		//1. ����û�ָ������ĵ���ȷ��
		if(cmds.length < 5) {
			throw new ErrorCode(7);
		} else {
			//ƴ�ӱ��ո�Ÿ���������
			writeFile(cmds);
		}
	}
	
	public static int writeFile(String[] cmds) throws UnsupportedEncodingException {
		//1. ������Ӧfm
		//2. ����getFile()������ȡfileMeta��Ϣ��Ȼ��ԭfile��blockdata���ݶ�ȡ����
		//3. �Ȳ�����ָ������⣬����fileMeta���blockSize���������ݷֳ�һ����block
		//4. ��ÿ��block����һ�������ߵ�BM��Ȼ�󴴽���ӦBM Client����
		//5. ����newBlock()����
		//6. ���ݷ��ص�block��Ϣ�޸�fileMeta
		//7. ����fileMeta����������
		int where = 0;
		long offset = 0;
		String stringFMId = cmds[1];
		String filename = cmds[2];
		String content = "";
		IFileMeta fileMeta = null;
		String fmStringId = "";
		FileManagerImpl fm = null;
		File remoteFile;
	
		try {
			//���ָ���ƶ��ĳ�ʼλ��
			where = Integer.valueOf(cmds[3]);
			//���ָ���ƶ���ƫ����
			offset = Long.valueOf(cmds[4]);
		} catch(Exception e ) {
			throw new ErrorCode(8);
		}

		
		//ƴ���ÿո������content
		for(int i = 5; i < cmds.length; i++) {
			if(i == cmds.length - 1) {
				content += cmds[i];
			} else {
				content += cmds[i] + " ";
			}
		}
		//ȥ����β��˫����
		content = content.substring(1, content.length() - 1);
		System.out.println("Ҫд������ݣ�" + content);
	
		//2. �����û�ָ���������󣬸����û������filename���ҵ���Ӧ��fileMeta�ļ�
		//���������ݷ����л������ɶ�Ӧ��fileMeta����
		try {
			FMClient fmc = new FMClient(new StringId(stringFMId));
			remoteFile =  fmc.getFile(new StringId(filename));

			FileImpl localFile;
			// file is already opened
			if(openedFileSet.containsKey(filename)) {
				localFile = openedFileSet.get(filename);
			} else {
				localFile = new FileImpl(remoteFile);
				openedFileSet.put(filename, localFile);
			}

			localFile.move(offset, where);
			localFile.write(content.getBytes("utf-8"));
			
			//file.write(content.getBytes("utf-8"));
		} catch(ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		} catch (Exception e) {
			throw new ErrorCode(1000);
		}

		return 0;
	}
}
