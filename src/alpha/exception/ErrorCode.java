package alpha.exception;

import java.util.HashMap;
import java.util.Map;

public class ErrorCode extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5243613610598386486L;
	public static final int IO_EXCEPTION = 1;
	public static final int CHECKSUM_CHECK_FAILED = 2;
	public static final int CREATE_EXISTED_FILE = 3;
	public static final int FILE_NOT_EXIST = 4;
	public static final int BLOCK_NOT_EXIST = 5;
	public static final int UNSUPPORTED_ENCODING = 6;
	public static final int INCORRECT_ARGUMENT_FORMAT = 7;
	public static final int NUMBER_FORMAT_EXCEPTION = 8;
	public static final int READ_OUT_OF_BOUND = 9;
	public static final int COMMAND_NOT_FOUND = 10;
	public static final int MOVE_POINTER_ERROR = 11;
	public static final int FILE_META_LOSS = 12;
	public static final int TYPE_CONVERSION_EXCEPTION = 13;
	public static final int SERIALIZED_ERROR = 14;
	public static final int BLOCK_INFORMATION_LOSS = 15;
	public static final int FILE_HOLE_EXCEPTION = 16;
	public static final int SET_FILE_SIZE_NEGATIVE = 17;
	public static final int SERVER_NOT_BOUND = 18;
	public static final int REMOTE_EXCEPTION = 19;
	public static final int NULL_POINTER_EXCEPTION = 20;
	public static final int NO_AVAILABLE_BM_SERVER = 21;
	public static final int SERVER_RESPONSE_TIMEOUT = 22;
	
	public static final int UNKNOWN = 1000;
	
	private static final Map<Integer, String> ErrorCodeMap = new HashMap<>();
	static {
		ErrorCodeMap.put(IO_EXCEPTION, "IO exception");
		ErrorCodeMap.put(CHECKSUM_CHECK_FAILED, "block checksum check failed");
		ErrorCodeMap.put(CREATE_EXISTED_FILE, "create a existed file exception");
		ErrorCodeMap.put(FILE_NOT_EXIST, "file not exists exception");
		ErrorCodeMap.put(BLOCK_NOT_EXIST, "block not exists exception");
		ErrorCodeMap.put(UNSUPPORTED_ENCODING, "unsupported encoding exception");
		ErrorCodeMap.put(INCORRECT_ARGUMENT_FORMAT, "incorrect arguments format");
		ErrorCodeMap.put(NUMBER_FORMAT_EXCEPTION, "number format exception when type casting");
		ErrorCodeMap.put(READ_OUT_OF_BOUND, "read out of bound");
		ErrorCodeMap.put(COMMAND_NOT_FOUND, "command not found");
		ErrorCodeMap.put(MOVE_POINTER_ERROR, "move pointer error");
		ErrorCodeMap.put(FILE_META_LOSS, "file meta loss");
		ErrorCodeMap.put(TYPE_CONVERSION_EXCEPTION, "id type conversion exception");
		ErrorCodeMap.put(SERIALIZED_ERROR, "serialize error");
		ErrorCodeMap.put(BLOCK_INFORMATION_LOSS, "block data or meta loss");
		ErrorCodeMap.put(FILE_HOLE_EXCEPTION, "file empty hole exception");
		ErrorCodeMap.put(SET_FILE_SIZE_NEGATIVE, "set a negative length of the file");
		ErrorCodeMap.put(SERVER_NOT_BOUND, "server not bound(haven't launched)");
		ErrorCodeMap.put(REMOTE_EXCEPTION, "remote exception");
		ErrorCodeMap.put(NULL_POINTER_EXCEPTION, "null pointer exception");
		ErrorCodeMap.put(NO_AVAILABLE_BM_SERVER, "no available bm server now");
		ErrorCodeMap.put(SERVER_RESPONSE_TIMEOUT, "server response timeout exception");
		
		ErrorCodeMap.put(UNKNOWN, "unknown");
	}
	
	public static String getErrorText(int errorCode) {
		return ErrorCodeMap.getOrDefault(errorCode, "invalid");
	}
	
	private int errorCode;
	
	public ErrorCode(int errorCode) {
		super(String.format("error code '%d' \"%s\"", errorCode, getErrorText(errorCode)));
		this.errorCode = errorCode;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
}
