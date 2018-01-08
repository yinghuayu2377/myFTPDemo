package operator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import jxl.write.WriteException;

import org.jdom.JDOMException;

import Excel.CreateExcel;


public class Update {
	
	public static void update(String str, Socket socket1) throws IOException, WriteException, JDOMException {
		Socket socket = socket1;

		BufferedInputStream bis=null;
        DataInputStream dis=null;
        bis=new BufferedInputStream(socket.getInputStream());
        dis=new DataInputStream(bis);

        FileOutputStream fos=null;
        BufferedOutputStream bos= null;
        DataOutputStream dos=null;
        
        int in=-1;
        String path = null;
        String file_name = null;
        if ((in = str.indexOf("[Director]")) != -1) {

            path= str.substring(0, in);  //文件夹路径
            System.out.println(path);
            final int len_str_down=str.length();
            file_name = str.substring(in + 10, len_str_down);  //上传文件的名字
            System.out.println(file_name);
        }
        try {

            File file=new File(path);
            if(!file.isDirectory())
            {
                file.mkdirs();   //创建文件夹
            }

            File db=new File(file+File.separator+file_name);
            File myfile=new File(file,file_name);
            try {
                myfile.createNewFile();  //创建文件

            } catch (IOException e) {
                e.printStackTrace();
            }

            fos=new FileOutputStream(db);
            bos = new BufferedOutputStream(fos);
            dos=new DataOutputStream(bos);

            byte[] by = new byte[1024];

         
            int len_up=-1;
            while((len_up=dis.read(by))!=-1)
            {
            	System.out.println(len_up);
               dos.write(by,0,len_up);
            }
           
            dos.flush();

            fos.close();
            dos.close();
            
            bis.close();
            dis.close();
            socket.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if(file_name.equals("message.xml"))
        {
        	CreateExcel.Create();
        	
        }
	}
	
}
