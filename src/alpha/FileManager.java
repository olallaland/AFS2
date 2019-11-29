package alpha;

public interface FileManager {
	File getFile(Id fileId) throws Exception;
	File newFile(Id fileId) throws Exception;
	String deleteFile(Id fileId) throws Exception;
}
