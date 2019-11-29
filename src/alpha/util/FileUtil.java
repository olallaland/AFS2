package alpha.util;

import alpha.constant.FileConstant;
import alpha.exception.ErrorCode;

import java.io.*;
import java.util.LinkedList;

public class FileUtil {
    /**
     * �����ļ������·�������ļ�
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
     * �����ļ����ļ����ҵ���·�����ļ�����Ψһ�ģ�
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
		for(File f : files) {                //����File[]����
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
     * �����ļ���·���ж��Ƿ����
     * @param path
     * @return
     */
	public static boolean exists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * ����block��block id�ҵ���·����block id��Ψһ�ģ�
     * @param target
     * @param path
     * @return
     */
	public static boolean blockExists(String target, StringBuilder path) {
		//String target = filename + FileConstant.META_SUFFIX;
		//System.out.println(target);
		File fmFolder = new File(FileConstant.BM_CWD);
		File[] files = fmFolder.listFiles();
		for(File f : files) {                //����File[]����
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
     * ���������ֽ���д���ļ�
     * @param bytes
     * @param destFilePath
     * @throws IOException
     */
	public static void oldWrites(byte[] bytes, String destFilePath) throws IOException {
		createFile(destFilePath);
		//����Դ
        File dest = new File(destFilePath);

        //ѡ����
        InputStream is = null;
        OutputStream os = null;
        //����
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
            //�ر��ļ���
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
     * ���������ֽ���д���ļ�
     * ʹ��RandomAccessFile��
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
     * ����д���ļ�
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
     * ���ж�ȡ�ļ�
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
            // ���ж�ȡ�ַ���
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
		//����Դ
        File dest = new File(destFilePath);//Ŀ�ĵأ����ļ�
        //src�ֽ������Ѿ�����
        //ѡ����
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream os = null;
        InputStream is = null;//ByteArrayInputStream�ĸ���
        //OutputStream os = null;
        //����
        try {
            is = new ByteArrayInputStream(bytes);//�ֽ����������֮��Ĺܵ�
            os = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(
                    		destFilePath)));
            //os = new DataOutputStream(dest);//���������ļ�֮��Ĺܵ�
            //һ�����ֽ����黺�����
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
            if(null != os) {//�ر��ļ���
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
     * ���ֽ����ķ�ʽ���ļ�
     * @param targetFilename
     * @return
     */
	public static byte[] reads(String targetFilename) {
		//����Դ��Ŀ�ĵ�
        File src = new File(targetFilename);
        byte[] dest = null;
        //ѡ����
        InputStream is = null;
        ByteArrayOutputStream baos= null;
        //����(�������)
        try {
            //�ļ�������
            is = new FileInputStream(src);
            //�ֽ������������Ҫָ���ļ����ڴ��д���
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
            //�ͷ���Դ,�ļ���Ҫ�ر�,�ֽ�����������ر�
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
     * ��ȡid.count �ļ�
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
     * ����id.count �ļ�
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
