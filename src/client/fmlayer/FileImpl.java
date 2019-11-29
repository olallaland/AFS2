package client.fmlayer;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

import alpha.*;
import alpha.id.IntegerId;
import client.bmlayer.BlockImpl;
import client.bmlayer.BMClient;
import alpha.constant.FileConstant;
import alpha.exception.ErrorCode;
import alpha.id.StringId;

public class FileImpl implements File, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -924457163328607900L;
	String fileData;
	IFileMeta fileMeta;
	long pointer = 0;
	Id fileId;
	
	public FileImpl(FileMeta fileMeta) {
		this.fileMeta = fileMeta;
		this.fileId = fileMeta.fileId;
	}
	
	public FileImpl(Id fileId) {
		this.fileId = fileId;
	}
	
	public FileImpl(File file) {
		this.fileMeta = file.getFileMeta();
		this.fileId = file.getFileId();
	}

	@Override
	public Id getFileId() {
		return fileMeta.getFileId();
	}

	@Override
	public IFileMeta getFileMeta() { return this.fileMeta; }

	public String getStringFileId() {
		if(fileId instanceof StringId) {
			StringId sid = (StringId) fileId;
			String id = sid.getId();
			return id;
		} else {
			//TODO
			return "";
		}
	}

	@Override
	public FileManager getFileManager() {
		return new FMClient(fileMeta.getFmId());
	}
	
	@Override
	public long move(long offset, int where) {
		switch(where) {
			case MOVE_HEAD:
				pointer = offset;
				break;
			case MOVE_CURR:
				pointer += offset;
				break;
			case MOVE_TAIL:
				pointer = size() + offset;
				break;
			default:
				throw new ErrorCode(11);
		}
		if(pointer < 0) {
			throw new ErrorCode(11);
		}
		return pointer;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public long size() {
		return fileMeta.getFileSize();
	}

	@Override
	public void setSize(long newSize) {
		byte[] oldContent = read((int) fileMeta.getFileSize());
		byte[] newContent = new byte[(int) newSize];
		long oldSize = oldContent.length;
		int diff = (int) (newSize - oldSize);
		int newBlockCount = (int) ((newSize % FileConstant.BLOCK_SIZE) == 0
				? (newSize / FileConstant.BLOCK_SIZE)
				: (newSize / FileConstant.BLOCK_SIZE) + 1);

		HashMap<Integer, LinkedHashMap<Id, Integer>> logicBlocks = fileMeta.getLogicBlocks();
		int oldBlockCount = fileMeta.getBlockCount();

		//ԭ�ļ�����С�ڻ�����µĳ��ȣ��ں����0x00
		if(diff > 0) {
			System.out.println("new size is more than old size:" + diff);

			byte[] addition;
			int leftOver = (int)oldSize % FileConstant.BLOCK_SIZE;
			addition = new byte[diff + leftOver];
			if(leftOver == 0) {

				for(int i = leftOver; i < diff; i++) {
					addition[i] = 0x00;
				}

				try {
					subWrite(addition, oldBlockCount, newSize);
				} catch (ErrorCode e) {
					throw new ErrorCode(e.getErrorCode());
				}
				System.out.println("old file leftover is 0:" + diff);
			} else {
				System.out.println("old file leftover is: " + leftOver);
				byte[] temp;
				try {
					temp = readRemoteBlock(logicBlocks.get(oldBlockCount - 1));
				} catch (ErrorCode e) {
					throw new ErrorCode(e.getErrorCode());
				}
				System.arraycopy(temp, 0, addition, 0, leftOver);


				for(int i = leftOver; i < diff; i++) {
					addition[i] = 0x00;
				}
				try {
					subWrite(addition, oldBlockCount - 1, newSize);
				} catch (ErrorCode e) {
					throw new ErrorCode(e.getErrorCode());
				}
				//logicBlocks.remove(oldBlockCount - 1);
			}

		} else if(diff < 0) {
			//���newSize������blockSize��
			// ��ô��fileMeta��ɾȥ�����block�����ı�fileSize
			//���newSize��������blockSize��
			// ��ô�������һ��block�����ݣ�������Ӧ���ȵ���block��

			System.out.println("new size is less than old size:" + diff);

//			for(int i = newBlockCount - 1; i < oldBlockCount; i++) {
//				logicBlocks.remove(i);
//			}

			if(newSize % FileConstant.BLOCK_SIZE != 0) {
				System.out.println("read old data:" + diff);
				System.out.println(logicBlocks);
				System.out.println(logicBlocks.get((int)newSize / FileConstant.BLOCK_SIZE));
				byte[] temp;
				try {
					temp = readRemoteBlock(logicBlocks.get((int)newSize / FileConstant.BLOCK_SIZE));
				} catch (ErrorCode e) {
					throw new ErrorCode(e.getErrorCode());
				}

				for(int i = newBlockCount; i < oldBlockCount; i++) {
					logicBlocks.remove(i);
				}

				byte[] left = new byte[(int)newSize % FileConstant.BLOCK_SIZE];
				System.arraycopy(temp, 0, left, 0, (int)newSize % FileConstant.BLOCK_SIZE);

				try {
					subWrite(left, newBlockCount - 1, newSize);
				} catch (ErrorCode e) {
					throw new ErrorCode(e.getErrorCode());
				}

			} else {
				for(int i = newBlockCount; i < oldBlockCount; i++) {
					logicBlocks.remove(i);
				}
				System.out.println("do not need old data");
				fileMeta.setLogicBlocks(logicBlocks);
				fileMeta.setBlockCount(newBlockCount);
				fileMeta.setFileSize(newSize);
				try {

					FMClient fmc = new FMClient(fileMeta.getFmId());
					fmc.updateFile(fileMeta);

				} catch (ErrorCode e) {
					throw new ErrorCode(e.getErrorCode());
				}
			}
		} else {
			System.out.println("old size and new size are the same");
			// do nothing
		}
	}


	public void setFileMeta(FileMeta fileMeta) {
		this.fileMeta = fileMeta;	
	}

	@Override
	public byte[] read(int length) {
		HashMap<Integer, LinkedHashMap<Id, Integer>>logicBlocks = fileMeta.getLogicBlocks();
		byte[] total = new byte[(int) fileMeta.getFileSize()];
		byte[] result = new byte[length];

		try {
			Registry registry = LocateRegistry.getRegistry("localhost");
		} catch (RemoteException e) {
			throw new ErrorCode(19);
		}

		for(Integer i : logicBlocks.keySet()) {
            LinkedHashMap<Id, Integer> duplicates = logicBlocks.get(i);
			byte[] tempContent = null;
            Iterator it = duplicates.entrySet().iterator();

            try {
                tempContent = readRemoteBlock(duplicates);
            } catch (ErrorCode e) {
                throw new ErrorCode(e.getErrorCode());
            }
			
			System.arraycopy(tempContent, 0, total, i * FileConstant.BLOCK_SIZE, tempContent.length);

		}

		System.arraycopy(total, (int) pointer, result, 0, length);
		try {
			move(length, 0);
		} catch(RuntimeException e) {
			System.out.println(e.getMessage());
		}

		return result;
	}

	@Override
	public void write(byte[] bytes) {
		long fileSize = fileMeta.getFileSize();
		HashMap<Integer, LinkedHashMap<Id, Integer>> logicBlocks = fileMeta.getLogicBlocks();
		int blockCount = fileMeta.getBlockCount();
		
		/**
		 * pointer��file��β����β��֮��
		 */
		if(pointer >= fileMeta.getFileSize()) {
			
			long fileHoleCount = pointer - fileSize;

			//System.out.println("pointer��file��β����β��֮��");
			//���һ�� ֮ǰ�ļ�λռ��block
			//��Ҫ��ȡ֮ǰblock������
			if(fileSize % FileConstant.BLOCK_SIZE != 0) {
				//System.out.println("��Ҫ��ȡ֮ǰblock�����ݣ�");
                byte[] temp = null;
                try {
                    temp = readRemoteBlock(logicBlocks.get(blockCount - 1));
                } catch(ErrorCode e) {
                    throw new ErrorCode(e.getErrorCode());
                }

				//System.out.println("the last block size is(should be less than 8): " + temp.length);
				
				byte[] large = new byte[(int) (temp.length + fileHoleCount + bytes.length)];
				//�����һ��block�����ݸ��Ƶ�������Ŀ�ͷ
				System.arraycopy(temp, 0, large, 0, temp.length);
				//����ļ��ն�������
				for(int i = temp.length; i < fileHoleCount + temp.length; i++) {
					large[i] = 0x00;
				}
				//����Ҫд������ݵ�����������һ����
				System.arraycopy(bytes, 0, large, (int) (temp.length + fileHoleCount), bytes.length);
				
				//large��������������ļ�block index��blockCount - 1��ʼ������
				subWrite(large, blockCount - 1, fileSize + large.length - fileSize % FileConstant.BLOCK_SIZE);
				
			//���һ��block������������ֱ��д
			} else if(fileSize % FileConstant.BLOCK_SIZE == 0){ 
				//System.out.println("���һ��block������������ֱ��д��");
				byte[] large = new byte[(int) (fileHoleCount + bytes.length)];
				//����ļ��ն�������
				for(int i = 0; i < fileHoleCount; i++) {
					large[i] = 0x00;
				}
				//����Ҫд������ݵ�����������һ����
				System.arraycopy(bytes, 0, large, (int)fileHoleCount, bytes.length);
				subWrite(large, blockCount, fileSize + large.length);
			}
		
		/**
		 * pointer��file�в�
		 */
		} else {
			//System.out.println("pointer��file�в�");
			int preBlockCount = (int) (pointer / FileConstant.BLOCK_SIZE);
			long newSize = 0;
			byte[] large;
			byte[] larger;
			
			//��Ҫ��ȡ֮ǰblock������
			if(pointer % FileConstant.BLOCK_SIZE != 0) {
			    byte[] temp = null;
				//System.out.println("��Ҫ��ȡ֮ǰblock������");
				int leftInBlock = (int) (pointer % FileConstant.BLOCK_SIZE);

				try {
				    temp = readRemoteBlock(logicBlocks.get(preBlockCount));
                } catch (ErrorCode e) {
				    throw new ErrorCode(e.getErrorCode());
                }
				
				newSize = leftInBlock + bytes.length;
				large = new byte[(int) newSize];
				System.arraycopy(temp, 0, large, 0, leftInBlock);
				System.arraycopy(bytes, 0, large, leftInBlock, bytes.length);
				
			//����ֱ��д
			} else {
				//System.out.println("����ֱ��д");
				newSize = bytes.length;
				large = new byte[bytes.length];
				System.arraycopy(bytes, 0, large, 0, bytes.length);
			}
			
			if((bytes.length < fileSize - pointer) && newSize % FileConstant.BLOCK_SIZE != 0) {
				//System.out.println("δ����fileSizeĩβ��Ҫ����");
                byte[] blockInTailContent = null;
                try {
                    blockInTailContent = readRemoteBlock(logicBlocks.get(preBlockCount + (int)(newSize / FileConstant.BLOCK_SIZE)));
                } catch (ErrorCode e) {
                    throw new ErrorCode(e.getErrorCode());
                }

				int addToTail = (int) (blockInTailContent.length - newSize % FileConstant.BLOCK_SIZE);
				larger = new byte[large.length + addToTail];
				System.arraycopy(large, 0, larger, 0, large.length);
				System.arraycopy(blockInTailContent, (int) (newSize % FileConstant.BLOCK_SIZE), larger, large.length, addToTail);
				
				subWrite(larger, preBlockCount, fileSize);
			} else if(bytes.length >= fileSize - pointer){
				
				//System.out.println("����fileSize");
				subWrite(large, preBlockCount, bytes.length + pointer);
			} else {
				//System.out.println("δ����fileSizeĩβ��������Ҫ����");
				subWrite(large, preBlockCount, fileSize);
			}
		}
		try {
			move(bytes.length, 0);
		} catch(RuntimeException e) {
			System.out.println(e.getMessage());
		}
	}
	
	void subWrite(byte[] bytes, int beginBlock, long totalFileSize) {
		BMClient bmc;
		
		int newBlockCount = (bytes.length % FileConstant.BLOCK_SIZE) == 0
				? (bytes.length / FileConstant.BLOCK_SIZE)
				: (bytes.length / FileConstant.BLOCK_SIZE) + 1;
		
		for(int i = 0; i < newBlockCount; i++) {
			LinkedHashMap<Id, Integer> duplicates = new LinkedHashMap<Id, Integer>();
		
			int begin = i * FileConstant.BLOCK_SIZE;
			int end = (i + 1) * FileConstant.BLOCK_SIZE;
			//System.out.println("copy begins at: " + begin + "\n ends at: " + end);
			
			//���������ͬ�ĸ���
			for(int j = 0; j < FileConstant.DUPLICATION_COUNT; j++) {
				byte[] temp;
				Block block = null;
				if(end > bytes.length) {
					temp = Arrays.copyOfRange(bytes, begin, bytes.length);			
				} else {
					temp = Arrays.copyOfRange(bytes, begin, end);
				}		
				//System.out.println("д����ֽ�: " + new String(temp));
				//���ѡ��һ��bm
                try {
                    bmc = new BMClient();
                    block = bmc.newBlock(temp);
                } catch (ErrorCode e) {
				    throw new ErrorCode(e.getErrorCode());
                }
				System.out.println(bmc.getStringBMId() + ", " + block.getIntegerBlockId());
				duplicates.put(new StringId(bmc.getStringBMId()), block.getIntegerBlockId());
			}

			fileMeta.addLogicBlocks(i + beginBlock, duplicates);
		}

		fileMeta.setFileSize(totalFileSize);
		fileMeta.setBlockCount((int) ((totalFileSize % FileConstant.BLOCK_SIZE) == 0
				? (totalFileSize / FileConstant.BLOCK_SIZE)
				: (totalFileSize / FileConstant.BLOCK_SIZE) + 1));
		System.out.println("before update fileMeta: " + fileMeta.toString());
		//4. д����ɺ󣬽�fileMeta������д�ض�Ӧ�ļ�
		try {
			
			FMClient fmc = new FMClient(fileMeta.getFmId());
			fmc.updateFile(fileMeta);
			
		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		}
	}

	private byte[] readRemoteBlock(LinkedHashMap<Id, Integer> duplicates) {
		byte[] blockData = null;
        Iterator it = duplicates.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry entity = (Map.Entry) it.next();
            try {

                BMClient bmc = new BMClient((StringId)entity.getKey());
                Block blockRead = bmc.getBlock(new IntegerId((Integer)entity.getValue()));
                blockData = blockRead.getBlockData();
                System.out.println(new String(blockData, "utf-8"));
                break;
            } catch (ErrorCode e) {
                System.out.println(e.getMessage());
                duplicates.remove(entity.getKey());
            } catch (Exception e) {
                throw new ErrorCode(1000);
            }
        }

        if(duplicates.size() == 0) {
            blockData = new byte[FileConstant.BLOCK_SIZE];
            try {
                throw new ErrorCode(16);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }

		return blockData;
	}
	

	public void copy(File destFile) {
		IFileMeta destFileMeta = destFile.getFileMeta();

		destFileMeta.setBlockCount(fileMeta.getBlockCount());
		destFileMeta.setFileSize(fileMeta.getFileSize());
		destFileMeta.setLogicBlocks(fileMeta.getLogicBlocks());

		try {
			FMClient fmc = new FMClient(destFileMeta.getFmId());
			fmc.updateFile(destFileMeta);

		} catch (ErrorCode e) {
			throw new ErrorCode(e.getErrorCode());
		}
	}

}
