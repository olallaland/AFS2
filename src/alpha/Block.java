package alpha;

public interface Block {
	Id getIndexId();
	BlockManager getBlockManager();
	byte[] read();
	int blockSize();
	byte[] getBlockData();
	Integer getIntegerBlockId();
}
