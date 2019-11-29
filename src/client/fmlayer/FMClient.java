package client.fmlayer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.*;

import alpha.*;
import alpha.exception.ErrorCode;
import alpha.id.StringId;

public class FMClient implements FileManager {
	public IRemoteFM remoteFM;
	Id fmId;
	
	public FMClient() {
		
	}
	
	public FMClient(Id fmId) {
		this.fmId = fmId;
		String sid = getStringFMId();
		Registry registry = null;

		try {
			registry = LocateRegistry.getRegistry("localhost");
		} catch (RemoteException e) {
			throw new ErrorCode(19);
		}
		//System.out.println("this is my fmlayer id: " + sid);
//		String[] list = registry.list();
//		for(int i = 0; i < list.length; i++) {
//			System.out.println(list[i]);
//		}
		
		IRemoteFM client = null;
		
		try {
			client = (IRemoteFM) registry.lookup(sid);
		} catch(NotBoundException e) {
			throw new ErrorCode(18);
		} catch(RemoteException e) {
			throw new ErrorCode(19);
		} catch(NullPointerException e) {
			throw new ErrorCode(20);
		}
		
		//System.out.println(client.getfmId());
		this.remoteFM = client;
	}
	
	@Override
	public File getFile(Id fileId) throws Exception {
		File file = null;
		// 超时检查
		ExecutorService executor = Executors.newSingleThreadExecutor();
		FutureTask<File> future = new FutureTask<File>(new Callable<File>() {
			@Override
			public File call() throws Exception {
				return remoteFM.getFile(fileId);
			}
		});

		executor.execute(future);
		try {
			file = future.get(1000, TimeUnit.MILLISECONDS);
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

		return file;
	}

	@Override
	public File newFile(Id fileId) throws Exception {
		File file = null;
		// 超时检查
		ExecutorService executor = Executors.newSingleThreadExecutor();
		FutureTask<File> future = new FutureTask<File>(new Callable<File>() {
			@Override
			public File call() throws Exception {
				return remoteFM.newFile(fileId);
			}
		});

		executor.execute(future);
		try {
			file = future.get(1000, TimeUnit.MILLISECONDS);
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

		return file;
	}
	
	public String getStringFMId() {
		if(fmId instanceof StringId) {
			StringId sid = (StringId) fmId;
			String id = sid.getId();
			return id;
		} else {
			return "";
		}
	}

	public void updateFile(IFileMeta fileMeta) {
		try {
			remoteFM.updateFile(fileMeta);
		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		} catch (Exception e) {
			throw new ErrorCode(1000);
		}
	}

	@Override
	public String deleteFile(Id fileId) throws Exception {
		String result = "";
		System.out.println("delete file: " + (String)fileId.getId());
		try {
			result = remoteFM.deleteFile(fileId);
		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		} catch (Exception e) {
			throw new ErrorCode(1000);
		}
		return result;
	}
}
