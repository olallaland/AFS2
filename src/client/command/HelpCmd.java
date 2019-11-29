package client.command;

import alpha.exception.ErrorCode;

public class HelpCmd extends Command {
	public HelpCmd(String[] cmds) {
		if(cmds.length != 1) {
			throw new ErrorCode(7);
		} else {
			printHelps();
		}
	}

	private void printHelps() {
		System.out.println("alpha-cat [file manager name] [filename]");
		System.out.println("alpha-create [file manager name] [filename]");
		System.out.println("alpha-copy [src fm name] [srcFile] [dest fm name] [desFile]");
		System.out.println("alpha-write [file manager name] [filename] [where] [offset] [content]");
		System.out.println("alpha-read [file manager name] [filename] [where] [length]");
		System.out.println("alpha-sfs [file manager name] [filename] [newSize]");
		System.out.println("alpha-delete [file manager name] [filename]");
		System.out.println("alpha-hex [block manager name] [blockId]");
	}
}
