package alpha.util;

import alpha.exception.ErrorCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {
	/**  
	 * Title: toBytes  
	 * Description:���л�����  
	 * @throws Exception
	 */  
	public static byte[] toBytes(Object out) throws Exception {
		//���л��� ��������� --> ��ʾ��һ��Ŀ�� д������
        ObjectOutputStream objectOutputStream = null;
        //�ֽ����������
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            //����һ��������
            byteArrayOutputStream = new ByteArrayOutputStream();
            //�� ���� ���л��� �ֽں�  ���뻺������
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            //���л� ����
            objectOutputStream.writeObject(out);
            //�� �� ���л��ֽ�
            byte[] bytes = byteArrayOutputStream.toByteArray();
     
            //��������
            objectOutputStream.flush();
            //�ͷ���Դ
            objectOutputStream.close();

            return bytes;
        } catch (Exception e){
            throw new ErrorCode(1);
        }
	}
	
	 /*
	    * �����л�
	    * */

	 public static  <T> T deserialize(Class<T> clazz, byte[] content){

	 	ByteArrayInputStream byteArrayInputStream = null;
	 	try {
	 		//�� �õ������л��ֽ� ���� ������
			byteArrayInputStream = new ByteArrayInputStream(content);
			//�����л��� ����������--> ��ʾ�Ŵ� һ�� Դͷ ��ȡ ���� �� ����ȡ �������� ���ֽڣ�
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
	
			return (T)objectInputStream.readObject();
	 	} catch (Exception e){
	 		throw new ErrorCode(14);
	 	}

	 }
}
