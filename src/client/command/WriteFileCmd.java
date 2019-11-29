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
		//1. 检查用户指令输入的的正确性
		if(cmds.length < 5) {
			throw new ErrorCode(7);
		} else {
			//拼接被空格号隔开的内容
			writeFile(cmds);
		}
	}
	
	public static int writeFile(String[] cmds) throws UnsupportedEncodingException {
		//1. 创建对应fm
		//2. 调用getFile()方法获取fileMeta信息，然后将原file的blockdata内容读取出来
		//3. 先不考虑指针的问题，根据fileMeta里的blockSize将输入内容分成一个个block
		//4. 对每个block分配一个已上线的BM，然后创建对应BM Client对象
		//5. 调用newBlock()方法
		//6. 根据返回的block信息修改fileMeta
		//7. 更新fileMeta到服务器端
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
			//获得指针移动的初始位置
			where = Integer.valueOf(cmds[3]);
			//获得指针移动的偏移量
			offset = Long.valueOf(cmds[4]);
		} catch(Exception e ) {
			throw new ErrorCode(8);
		}

		
		//拼接用空格隔开的content
		for(int i = 5; i < cmds.length; i++) {
			if(i == cmds.length - 1) {
				content += cmds[i];
			} else {
				content += cmds[i] + " ";
			}
		}
		//去掉首尾的双引号
		content = content.substring(1, content.length() - 1);
		System.out.println("要写入的内容：" + content);
	
		//2. 若是用户指令输入无误，根据用户输入的filename查找到对应的fileMeta文件
		//并将其内容反序列化，生成对应的fileMeta对象
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
