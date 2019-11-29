package client.bmlayer;
import alpha.Block;
import alpha.BlockManager;
import alpha.IRemoteBM;
import alpha.Id;
import alpha.exception.ErrorCode;
import alpha.id.StringId;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class BMClient implements BlockManager {
	public IRemoteBM remoteBM;
	Id bmId;
	
	public BMClient() {
		Registry registry;
		String[] serverList = null;
		IRemoteBM client = null;
		List<String> bmServerList = new LinkedList<String>();
		
		try {
			registry = LocateRegistry.getRegistry("localhost");
			serverList = registry.list();
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			throw new ErrorCode(19);
		}
		
		for(int i = 0; i < serverList.length; i++) {
			if(serverList[i].startsWith("bm-")) {
				bmServerList.add(serverList[i]);
			}
		}
		
		if(bmServerList.size() == 0) {
			// no bm server available
			throw new ErrorCode(21); 
		} else {
			
			//random choose a bm server
			int index = (int) (Math.random() * bmServerList.size());
			try {
				client = (IRemoteBM) registry.lookup(bmServerList.get(index));
			} catch (AccessException e) {
				throw new ErrorCode(20);
			} catch (RemoteException e) {
				throw new ErrorCode(19);
			} catch (NotBoundException e) {
				throw new ErrorCode(18);
			}
			
			this.remoteBM = client;
			this.bmId = new StringId(bmServerList.get(index));
		}
		
	}
	
	public BMClient(Id bmId) {
		this.bmId = bmId;
		String sid = getStringBMId();
		System.out.println("this is my bm id: " + sid);

		Registry registry = null;

		try {
			registry = LocateRegistry.getRegistry("localhost");
		} catch (RemoteException e) {
			throw new ErrorCode(19);
		}
		//String[] list = registry.list();
//		for(int i = 0; i < list.length; i++) {
//			System.out.println(list[i]);
//		}
		IRemoteBM client = null;
		
		try {
			client = (IRemoteBM) registry.lookup(sid);
		} catch(NotBoundException e) {
			throw new ErrorCode(18);
		} catch(RemoteException e) {
			throw new ErrorCode(19);
		} catch(NullPointerException e) {
			throw new ErrorCode(20);
		}
		this.remoteBM = client;
	}
	
	@Override
	public Block getBlock(Id indexId) throws Exception {

		// 超时检查
		ExecutorService executor = Executors.newSingleThreadExecutor();
		FutureTask<Block> future = new FutureTask<Block>(new Callable<Block>() {
			@Override
			public Block call() throws Exception {
				return remoteBM.getBlock(indexId);
			}
		});

		Block blockRead;
		executor.execute(future);
		try {
			blockRead = future.get(1000, TimeUnit.MILLISECONDS);
		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		} catch (InterruptedException | ExecutionException e) {
			ErrorCode e1 = (ErrorCode) e.getCause();
			throw new ErrorCode(e1.getErrorCode());
		} catch (TimeoutException e) {
			throw new ErrorCode(22);
		} finally {
			future.cancel(true);
			executor.shutdown();
		}
		return blockRead;
	}

	@Override
	public Block newBlock(byte[] b) {
		Block block;
		// 超时检查
		ExecutorService executor = Executors.newSingleThreadExecutor();
		FutureTask<Block> future = new FutureTask<Block>(new Callable<Block>() {
			@Override
			public Block call() throws Exception {
				return remoteBM.newBlock(b);
			}
		});

		executor.execute(future);
		try {
			block = future.get(1000, TimeUnit.MILLISECONDS);
		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		} catch (InterruptedException | ExecutionException e) {
			ErrorCode e1 = (ErrorCode) e.getCause();
			throw new ErrorCode(e1.getErrorCode());
		} catch (TimeoutException e) {
			throw new ErrorCode(22);
		} finally {
			future.cancel(true);
			executor.shutdown();
		}
		return block;
	}
	
	
	public String getStringBMId() {
		if(bmId instanceof StringId) {
			StringId sid = (StringId) bmId;
			String id = sid.getId();
			return id;
		} else {
			return "";
		}
	}
	
}
