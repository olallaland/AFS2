package alpha.constant;

public class FileConstant {
	public static final String CWD = "data/";
	public static final String FM_CWD = "data/fm";
	public static final String BM_CWD = "data/bm";
	public static final String META_SUFFIX = ".meta";
	public static final String DATA_SUFFIX = ".data";
	public static final String PATH_SEPARATOR = "/";
	public static final int BLOCK_SIZE = 8;
	public static final String ID_COUNT_PATH = "data/bm/id.count";
	public static final int BM_COUNT = 10;
	public static final int FM_COUNT = 8;

	/**
	 * the block duplication count
	 */
	public static final int DUPLICATION_COUNT = 3;

	/**
	 * the time that client waits for server to response (MILLISECONDS)
	 */
	public static final int WAIT_TIME = 1000;
}
