package alpha.util;

import alpha.exception.ErrorCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {
	/**  
	 * Title: toBytes  
	 * Description:序列化对象  
	 * @throws Exception
	 */  
	public static byte[] toBytes(Object out) throws Exception {
		//序列化流 （输出流） --> 表示向一个目标 写入数据
        ObjectOutputStream objectOutputStream = null;
        //字节数组输出流
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            //创建一个缓冲区
            byteArrayOutputStream = new ByteArrayOutputStream();
            //将 对象 序列化成 字节后  输入缓冲区中
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            //序列化 对象
            objectOutputStream.writeObject(out);
            //得 到 序列化字节
            byte[] bytes = byteArrayOutputStream.toByteArray();
     
            //清空输出流
            objectOutputStream.flush();
            //释放资源
            objectOutputStream.close();

            return bytes;
        } catch (Exception e){
            throw new ErrorCode(1);
        }
	}
	
	 /*
	    * 反序列化
	    * */

	 public static  <T> T deserialize(Class<T> clazz, byte[] content){

	 	ByteArrayInputStream byteArrayInputStream = null;
	 	try {
	 		//将 得到的序列化字节 丢进 缓冲区
			byteArrayInputStream = new ByteArrayInputStream(content);
			//反序列化流 （输入流）--> 表示着从 一个 源头 读取 数据 ， （读取 缓冲区中 的字节）
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
	
			return (T)objectInputStream.readObject();
	 	} catch (Exception e){
	 		throw new ErrorCode(14);
	 	}

	 }
}
