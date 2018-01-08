package ClientSocket;

import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import Ip_Port.Ip_Port;

/**
 * Created by lenovo on 2017/3/23.
 */
public class ClientSocket {

    public ArrayList<String> arrayList = new ArrayList<>();
    private Socket client;
    private Writer writer;
    public String str_out_2;

    //与服务器端进行连接
    public void doConnect(String cmd,String type) {

        //与服务端建立连接
        //客户端进行写操作

        try {

            client = new Socket(Ip_Port.getIP(), Ip_Port.getPort());
            writer = new OutputStreamWriter(client.getOutputStream(), "utf-8");

            writer.write(type);
            writer.write(cmd);
            writer.write("eof\n");

            Log.e("string", cmd);
            writer.flush();//写完后要记得flush

            if(type.equals("8")||type.equals("9"))
            {
                writer.close();
            }
            //读取从服务器端传过来的数据
            else {
                ReadString(type);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //建立连接后就可以往服务端发送数据
    }

    //读取从服务器端传过来的数据
    private void ReadString(String type) throws IOException {
        Reader reader = new InputStreamReader(client.getInputStream(), "utf-8");
        client.setSoTimeout(20 * 1000);
        char chars[] = new char[64];
        int len=-1;
        StringBuffer sb = new StringBuffer();
        String temp = "";
        int index;

        try {
            while ((len = reader.read(chars)) != -1) {
                temp = new String(chars, 0, len);
                if ((index = temp.indexOf("eof")) != -1)  //遇到eof就结束
                {
                    sb.append(temp.substring(0, index));
                    break;
                }
                sb.append(new String(chars, 0, len));

            }
        } catch (SocketTimeoutException ex) {
            sb.append("timeout");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String str_out = sb.toString();

        str_out_2=str_out;
        if(type=="1") {
            PutString(str_out);   //将string存放入arraylist中
        }

        //断点下载
        else if(type=="4")
        {
            //获取传回来的文件长度
            getFileSize();

        }

        writer.close();
        reader.close();
        client.close();
    }

    //将string存放入arraylist中
    private void PutString(String str_out) {
        int len_str = str_out.length();
        int in2;
        int index2 = 0;
        int index3;

        if ((in2 = str_out.indexOf("timeout")) == 0) {
            arrayList.clear();
        } else {
            while (len_str > 0) {
                if ((index3 = str_out.indexOf("\n\r")) != -1) {
                    String out_string = str_out.substring(0, index3);
                    index2 = index3 + 2;

                    str_out = str_out.substring(index2, len_str);
                    len_str = len_str - index3 - 2;
                    arrayList.add(out_string);

                }
            }
        }
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public String getStr_out_2(){return str_out_2;}

    //获取传回来的文件长度
    public Long getFileSize()
    {
        return Long.valueOf(str_out_2);
    }
}
