package client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.concurrent.*;

import alpha.Block;
import alpha.IRemoteBM;
import alpha.exception.ErrorCode;
import alpha.id.IntegerId;
import alpha.id.StringId;
import alpha.util.FileUtil;
import client.bmlayer.BMClient;

public class TestClient {
	public static void main(String args[]) throws RemoteException {

//		try {
//			//test();
//		} catch(RuntimeException e) {
//			System.out.println(e.getMessage());
//		}
//
//		while(true) {
//			System.out.println("input: ");
//			Scanner scanner = new Scanner(System.in);
//			String input = scanner.nextLine();
//			String regexFM = "^fm-([1-8])";
//			System.out.println(input.matches(regexFM));
//
//		}

		Registry registry = LocateRegistry.getRegistry("localhost");
		IRemoteBM remote;
		try {
			String[] list = registry.list();
			for(int i = 0; i < list.length; i++) {
				System.out.println(list[i]);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


	}

	public static void test() {
			BMClient bmc = null;
			try {
				Registry registry = LocateRegistry.getRegistry("localhost");
				String[] list = registry.list();
				for (int i = 0; i < list.length; i++) {
					System.out.println(list[i]);
				}
//
				bmc = new BMClient(new StringId("bm-10"));
				//Block blockRead = bmc.getBlock(new IntegerId(160));
				//byte[] tempContent = blockRead.getBlockData();
				//System.out.println(new String(blockRead.getBlockData()));


			} catch (Exception e) {
				e.printStackTrace();
			}

			BMClient bmcTemp = bmc;

			ExecutorService executor = Executors.newSingleThreadExecutor();
			FutureTask<Block> future = new FutureTask<Block>(new Callable<Block>() {

				@Override
				public Block call() throws Exception {
					Block result;
					try {
						result = bmcTemp.getBlock(new IntegerId(160));
					} catch (ErrorCode e) {
						throw new ErrorCode(e.getErrorCode());
					}
					return result;
				}
			});
			Block blockRead;
			executor.execute(future);
			try {
				blockRead = future.get(4000, TimeUnit.MILLISECONDS);
				System.out.println(new String(blockRead.getBlockData(), "utf-8"));
			} catch (ErrorCode e) {
				throw new ErrorCode(e.getErrorCode());
			} catch (InterruptedException | ExecutionException e) {
				ErrorCode e1 = (ErrorCode) e.getCause();
				throw new ErrorCode(e1.getErrorCode());
			} catch (TimeoutException e) {
				throw new ErrorCode(22);
			} catch (UnsupportedEncodingException e) {
				throw new ErrorCode(6);
			} finally {
				future.cancel(true);
				executor.shutdown();
			}

			System.out.println("ggggg");
		}

}
