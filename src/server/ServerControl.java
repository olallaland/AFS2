package server;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Scanner;

import alpha.exception.ErrorCode;
import alpha.id.StringId;
import server.bmlayer.BMServer;
import server.fmlayer.FMServer;

public class ServerControl {
	public static void main(String args[]) throws RemoteException {
		//fmlayer server���ߵĳ������룺����severControl�� �û��ڿ���̨������Ҫ������fm��Ȼ��server control
		//����Ӧ��fm�Ƿ���ڣ�������ڣ��򴴽���Ӧfm���󲢰����ߣ����û�����׳��쳣
		//�������ά��һ��HashMap����¼�Ѿ�������BM��FM
		
		HashMap<String, Integer> launchedManagers = new HashMap<String, Integer>();
		String[] input;
		LocateRegistry.createRegistry(1099);
		//System.setProperty("java.rmi.server.hostname","127.0.0.1");
		
		Registry registry = LocateRegistry.getRegistry();
		
		while(true) {
			System.out.println("launch/termiante [server name](if more than one, split them with spaces...): ");
			Scanner reader = new Scanner(System.in);
			input = reader.nextLine().trim().split(" ");
			
			if(input[0].equals("exit")) {
				// ֻ���˳����Ƴ��򣬷�������δ����
				break;
			//��������	
			} else if(input[0].equals("launch")) {
				try {
					launchServer(input, registry);
				} catch(Exception e) {
					System.out.println(e.getMessage());
				}
				
			//��ֹ����
			} else if(input[0].equals("terminate")) {
				try {
					terminateServer(input, registry);
				} catch(Exception e) {
					System.out.println(e.getMessage());
				}
			
			//��ʾ�����������ķ���
			} else if(input[0].equals("list")) {
				listServer(registry);
			} else {
				continue;
			}
		}
	}

	private static void listServer(Registry registry) throws AccessException, RemoteException {
		String[] list = registry.list();
		for(int i = 0;  i < list.length; i++) {
			System.out.println(list[i]);
		}
	}

	private static void terminateServer(String[] input, Registry registry) {
		for(int i = 1; i < input.length; i++) {
		
			try {
				registry.unbind(input[i]);
				//Naming.unbind("rmi://localhost:8888/" + input[i]);
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				System.out.println("terminate server: " + input[i] + " unsuccessfully!");
				continue;
			} 
			
			System.out.println("terminate server: " + input[i] + " successfully!");
		}
		
	}

	private static void launchServer(String[] input, Registry registry) {
		String regexBM = "^bm-([1-9]|10)";
		String regexFM = "^fm-([1-8])";
		
		for(int i = 1; i < input.length; i++) {
			String current = input[i];
			Server server = null;
			if(current.matches(regexBM)) {
				try {
					server = new BMServer(new StringId(current));
				} catch(ErrorCode | RemoteException e) {
					throw new RuntimeException(e.getMessage());
				}
			} else if(current.matches(regexFM)) {
				try {
					server = new FMServer(new StringId(current));
				} catch(ErrorCode | RemoteException e) {
					throw new RuntimeException(e.getMessage());
				}
			} else {
				System.out.println("server: " + current + " not exists!");
				continue;
			}
			
			try {
				registry.bind(current, server);
				//Naming.bind("rmi://localhost:8888/" + current, server);
			} catch (AlreadyBoundException | RemoteException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e.getMessage());
			}
			
			System.out.println("launch server: " + input[i] + " successfully!");
		}
		
	}
}
