package alpha.util;

import alpha.constant.FileConstant;
import alpha.exception.ErrorCode;

import java.io.*;
import java.util.LinkedList;

public class FileUtil {
    /**
     * 根据文件的相对路径创建文件
     * @param destFilePath
     * @return
     */
	public static int createFile(String destFilePath) {

        File file = new File(destFilePath);
        if (file.exists()) {
            //file exists
            return -1;
        } else {
            if (destFilePath.endsWith(File.separator)) {
                throw new ErrorCode(1);
            }
            // file exists?
            if (!file.getParentFile().exists()) {
                //if the parent dir is not exist, then create it.
                if (!file.getParentFile().mkdirs()) {
                    throw new ErrorCode(1);
                }
            }
            // create target file.
            try {
                if (file.createNewFile()) {             
                    return 1;
                }
            
            } catch (IOException e) {
                throw new ErrorCode(1);
            }
        }
		return 0;
    }

    /**
     * 根据文件的文件名找到其路径（文件名是唯一的）
     * @param target
     * @param path
     * @return
     */
	public static boolean exists(String target, StringBuilder path) {
		//String target = filename + FileConstant.META_SUFFIX;
		//System.out.println(target);
		File fmFolder = new File(FileConstant.FM_CWD);
		if(!fmFolder.exists()) {
		    new File(FileConstant.FM_CWD + FileConstant.PATH_SEPARATOR).mkdirs();
        }
		File[] files = fmFolder.listFiles();
		for(File f : files) {                //遍历File[]数组
            if (f.isDirectory()) {  
                File[] subFiles = f.listFiles();
                for(File sf : subFiles) {
                	
                	String tempFile = sf.getName();
                	//System.out.println(tempFile);
                	if(tempFile.equals(target)) {
                		path.append(sf + "");
                		return true;
                	}
                }
            }
            if(f.isFile()) {
            	//
            }
        }
		return false;
	}

    /**
     * 根据文件的路径判断是否存在
     * @param path
     * @return
     */
	public static boolean exists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 根据block的block id找到其路径（block id是唯一的）
     * @param target
     * @param path
     * @return
     */
	public static boolean blockExists(String target, StringBuilder path) {
		//String target = filename + FileConstant.META_SUFFIX;
		//System.out.println(target);
		File fmFolder = new File(FileConstant.BM_CWD);
		File[] files = fmFolder.listFiles();
		for(File f : files) {                //遍历File[]数组
            if (f.isDirectory()) {  
                File[] subFiles = f.listFiles();
                for(File sf : subFiles) {
                	
                	String tempFile = sf.getName();
                	//System.out.println(tempFile);
                	if(tempFile.equals(target)) {
                		path.append(sf + "");
                		return true;
                	}
                }
            }
            if(f.isFile()) {
            	//
            }
        }
		return false;
	}

    /**
     * 将数据以字节流写入文件
     * @param bytes
     * @param destFilePath
     * @throws IOException
     */
	public static void oldWrites(byte[] bytes, String destFilePath) throws IOException {
		createFile(destFilePath);
		//创建源
        File dest = new File(destFilePath);

        //选择流
        InputStream is = null;
        OutputStream os = null;
        //操作
        try {
            is = new ByteArrayInputStream(bytes);
            os = new FileOutputStream(dest);

            byte[] flush = new byte[1024*10];
            int len = -1;
                while((len = is.read(flush)) != -1) {
                    os.write(flush,0,len);
                }
                os.flush();
        } catch (FileNotFoundException e) {
            throw new ErrorCode(4);
        } catch (IOException e) {
            throw new ErrorCode(1);
        }finally {
            //关闭文件流
            if(null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    throw new ErrorCode(1);
                }
            }
        }
	}

    /**
     * 将数据以字节流写入文件
     * 使用RandomAccessFile类
     * @param bytes
     * @param destFilePath
     */
	public static void writes(byte[] bytes, String destFilePath) {
        try {
            RandomAccessFile ra = new RandomAccessFile(destFilePath, "rwd");
            ra.setLength(0);
            ra.write(bytes);
        } catch (IOException e) {
            throw new ErrorCode(1);
        }
    }

    /**
     * 按行写入文件
     * @param path
     * @param content
     */
    public static void writeByLine(String path, String content) {
        try {
            createFile(path);

            FileWriter writer = new FileWriter(path, true);
            writer.write(content + "\n");
            writer.close();
        } catch (IOException e) {
            throw new ErrorCode(1);
        }
    }

    /**
     * 按行读取文件
     * @param path
     * @return
     */
    public static LinkedList<String> readByLine(String path) {

        LinkedList<String> fileList = new LinkedList<String>();
        File file = new File(path);
        if(!file.exists()) {
            return fileList;
        }

        try {
            FileReader fr = new FileReader(path);
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                fileList.add(str);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            throw new ErrorCode(1);
        }

        return fileList;
    }
	
	/*
	public static void writeInBinary(byte[] bytes, String destFilePath) {
		createFile(destFilePath);
		System.out.println("this is a file writes in Binary: " + destFilePath); 
		//创建源
        File dest = new File(destFilePath);//目的地，新文件
        //src字节数组已经存在
        //选择流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream os = null;
        InputStream is = null;//ByteArrayInputStream的父类
        //OutputStream os = null;
        //操作
        try {
            is = new ByteArrayInputStream(bytes);//字节数组与程序之间的管道
            os = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(
                    		destFilePath)));
            //os = new DataOutputStream(dest);//程序与新文件之间的管道
            //一样的字节数组缓冲操作
            byte[] flush = new byte[1024*10];
            int len = -1;
                while((len = is.read(flush)) != -1) {
                    os.write(flush,0,len);
                }
                os.flush();
        } catch (FileNotFoundException e) {
            throw new ErrorCode(4);
        } catch (IOException e) {
            throw new ErrorCode(1);
        }finally {
            if(null != os) {//关闭文件流
                try {
                    os.close();
                } catch (IOException e) {
                	throw new ErrorCode(1);
                }
            }
        }
	}
	
	*/

    /**
     * 以字节流的方式读文件
     * @param targetFilename
     * @return
     */
	public static byte[] reads(String targetFilename) {
		//创建源与目的地
        File src = new File(targetFilename);
        byte[] dest = null;
        //选择流
        InputStream is = null;
        ByteArrayOutputStream baos= null;
        //操作(输入操作)
        try {
            //文件输入流
            is = new FileInputStream(src);
            //字节输出流，不需要指定文件，内存中存在
            baos = new ByteArrayOutputStream();
            byte[] flush = new byte[1024 * 10];
            int len = -1;
            while((len = is.read(flush)) != -1) {
                baos.write(flush,0,len);
            }
            baos.flush();
            dest = baos.toByteArray();
            return dest;
        } catch (FileNotFoundException e) {
        	throw new ErrorCode(4);
        } catch (IOException e) {
        	throw new ErrorCode(1);
        } finally {
            //释放资源,文件需要关闭,字节数组流无需关闭
            if(null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                	throw new ErrorCode(1);
                }
            }
            
        }
	}

    /**
     * 读取id.count 文件
     * @return
     * @throws IOException
     */
	public static int readIdCount() throws IOException {
		File file = new File(FileConstant.ID_COUNT_PATH);
		int ch;
		String temp = "";
        if (file.exists()) {
        	FileReader input = new FileReader(file);
        	while((ch = input.read()) != -1) {
        		temp += (char)ch;
        	}
        	input.close();

            return Integer.parseInt(temp);
        } else {
        	updateIdCount(1);
        	return 1;  	
        }
	}

    /**
     * 更新id.count 文件
     * @param newValue
     * @return
     * @throws IOException
     */
	public static int updateIdCount(int newValue) throws IOException {
		File file = new File(FileConstant.ID_COUNT_PATH);
		FileWriter output;
		if (!file.exists()) {

			if (!file.getParentFile().exists()) {
				// if the parent dir is not exist, then create it.
				if (!file.getParentFile().mkdirs()) {
					throw new ErrorCode(1);
				}
			}
			// create target file.
			try {
				file.createNewFile();
			
			} catch (IOException e) {
				throw new ErrorCode(1);
			}
		}
		output = new FileWriter(file);
		output.write(newValue + "");
		output.flush();
		output.close();
		return 0;
	}

    /**
     * delete a file
     * @param path
     */
	public static void deleteFile(String path) {

        File file = new File(path);
        if(file.exists()) {

            if(!file.delete()) {
                throw new ErrorCode(1);
            }
        } else {
            throw new ErrorCode(4);
        }
    }

    /**
     * delete a line in a file
     * @param path
     * @param line
     */
    public static void deleteLine(String path, String line) {

        File file = new File(path);
        FileWriter writer = null;
        BufferedReader br = null;
        StringBuffer temp = new StringBuffer();
        String tempLine;
        if(file.exists()) {
            try {
                FileReader fr = new FileReader(file);
                br = new BufferedReader(fr);
                while(br.ready()) {
                    tempLine = br.readLine();
                    if(!tempLine.equals(line)) {
                        temp.append(tempLine + "\n");
                    }
                }

                br.close();
                writer = new FileWriter(file);
                writer.write(temp.toString());
                writer.close();
            } catch (Exception e) {
                throw new ErrorCode(1);
            }
        } else {
            throw new ErrorCode(4);
        }
    }

}
