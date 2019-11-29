package alpha;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import client.bmlayer.BlockImpl;

public interface IFileMeta {

	String getStringFileId();

	String getStringFmId();

	Id getFileId();

	Id getFmId();

	HashMap<Integer, LinkedHashMap<Id, Integer>> getLogicBlocks();

	long getFileSize();

	void read();

	void write();

	void setBlockCount(int i);

	void setFileSize(long totalFileSize);

	void addLogicBlocks(int i, LinkedHashMap<Id, Integer> duplicates);

	int getBlockCount();

	void setLogicBlocks(HashMap<Integer, LinkedHashMap<Id, Integer>> logicBlocks);

}
