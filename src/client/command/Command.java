package client.command;

import client.fmlayer.FileImpl;

import java.util.HashMap;

public abstract class Command {

	public static HashMap<String, FileImpl> openedFileSet = new HashMap<String, FileImpl>();

	Command(String cmdName, String filename) {
		
	}
	
	Command(String filename) {
		
	}
	Command() {
		
	}

}
