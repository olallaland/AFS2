package client.command;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import alpha.Block;
import client.bmlayer.BlockImpl;
import client.bmlayer.BlockManagerImpl;
import client.bmlayer.BMClient;
import alpha.exception.ErrorCode;
import alpha.id.IntegerId;
import alpha.id.StringId;

public class ReadBlockInHexCmd extends Command {
	public ReadBlockInHexCmd(String[] cmds) {
		//1. 检查用户指令输入的的正确性（参数个数以及文件是否存在）
		if(cmds.length != 3) {
			throw new ErrorCode(7);
		} else {
			try {
				readBlockInHex(cmds);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}	
	}

	private void readBlockInHex(String[] input) throws RemoteException {
		byte[] blockData = null;
		String bmStringId = "";
		BlockManagerImpl bm;
		BlockImpl block;
		int id;
		Registry registry = LocateRegistry.getRegistry("localhost");

		bmStringId = input[1];
		try {
			id = Integer.valueOf(input[2]);
		} catch (Exception e ) {
			throw new ErrorCode(8);
		}
		
		try {
			BMClient bmc = new BMClient(new StringId(bmStringId), registry);
			Block blockRead = bmc.getBlock(new IntegerId(id));
			blockData = blockRead.getBlockData();
			
		} catch (NotBoundException e) {
			throw new ErrorCode(18);
		} catch (Exception e) {
			throw new ErrorCode(1000);
		}
		for(int i = 0; i < blockData.length; i++) {
			System.out.print("0x" + Integer.toHexString(blockData[i] & 0xFF) + " ");
			if((i + 1) % 16 == 0) {
				System.out.println();
			}
		}
		System.out.println();
//
//		try {
//			bmStringId = findBm(blockId);
//			bm = new BlockManagerImpl(new StringId(bmStringId));
//			block = (BlockImpl)bm.getBlock(new IntegerId(id));
//		} catch (ErrorCode e) {
//			throw new ErrorCode(e.getErrorCode());
//		}

		
		
		
//		if(blockRead.isValid()) {
//			blockData = block.read();
//			for(int i = 0; i < blockData.length; i++) {
//				System.out.print("0x" + Integer.toHexString(blockData[i] & 0xFF) + " ");
//				if((i + 1) % 16 == 0) {
//					System.out.println();
//				}
//			}
//			System.out.println();
//		} else {
//			throw new ErrorCode(2);
//		}
	}

}
 