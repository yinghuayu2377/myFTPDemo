package ClientSocket;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import Ip_Port.Ip_Port;

/**
 * Created by lenovo on 2017/4/13.
 */
public class ClientSocket_update {
    private Socket client;
    private Writer writer;
    private String str_out_update;
    private String path;
    private String path_str_1;

    //与服务器端进行连接
    public void doConnect(String cmd,String type,String path_str) {

        //与服务端建立连接
        //客户端进行写操作

        path=cmd;   //文件夹路径
        path_str_1=path_str;  //SD卡中的文件路径
        int index=-1;
        int len_str=-1;
        len_str=path_str_1.length();
        String str_down="";
        if ((index = path_str_1.lastIndexOf("/")) != -1) {
            str_down=path_str_1.substring(index+1,len_str);   //上传的文件名称
            Log.e("str_down",str_down);
        }
        try {

            client = new Socket(Ip_Port.getIP(), Ip_Port.getPort());
            writer = new OutputStreamWriter(client.getOutputStream(), "utf-8");
            writer.write(type);
            writer.write(path+"[Director]");   //文件夹路径
            writer.write(str_down);
            writer.write("eof\n");

            Log.e("string", path);
            writer.flush();//写完后要记得flush

            //读取上传文件的相关信息
            ReadString(type);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //建立连接后就可以往服务端发送数据
    }

    //读取上传文件的相关信息
    private void ReadString(String type) throws IOException {

    client.setSoTimeout(200 * 1000);

        if(type.equals("3"))
        {
            File file=null;
            FileInputStream fi=null;
            BufferedInputStream bis=null;
            DataInputStream dis=null;

            try {

                Log.e("path_str",path_str_1);
                file=new File(path_str_1);
                fi=new FileInputStream(file);
                bis=new BufferedInputStream(fi);
                dis=new DataInputStream(bis);

                //按字节读取剩余的内容
                try {

                    BufferedOutputStream bos=null;
                    DataOutputStream dos=null;
                    bos=new BufferedOutputStream(client.getOutputStream());
                    dos=new DataOutputStream(bos);

                    byte[] by = new byte[1024];

                    int len = -1; // 按字节读取剩余的内容
                    while((len=dis.read(by))!=-1) {
                        Log.e("len", String.valueOf(len));
                        dos.write(by, 0, len);
                    }
                    dos.flush();
                    dos.close();
                    fi.close();
                    bis.close();
                    dis.close();
                    writer.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
