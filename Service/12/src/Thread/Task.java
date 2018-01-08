package Thread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import operator.Dir;
import operator.Downland;
import operator.Open;
import operator.Update;
import operator.Down_duan;

public class Task implements Runnable{

	private Socket socket;
	   
	   public Task(Socket socket){
		   this.socket=socket;
	   }
	   
	   public void run()
	   {
		   try{
		   handleSocket();
		   }catch(Exception e){
			   e.printStackTrace();
		   }
	   }

	   //和客户端通信
       private void handleSocket() throws Exception{
	   //跟客户端建立好连接之后，我们就可以获取socket的InputStream，并从中读取客户端发过来的信息了。   
          Reader reader = new InputStreamReader(socket.getInputStream(),"utf-8");   
          char chars[] = new char[64];   
          int len=-1;   
          StringBuilder sb = new StringBuilder();   
          
          String temp;
          int index;
          String type = null;
          
          while ((len=reader.read(chars)) != -1) {  
        	  temp=new String(chars,0,len);
        	  type=temp.substring(0, 1);
    		  System.out.println(type);
    		  if(type.equals("1")||type.equals("2")||type.equals("3")||type.equals("4")||type.equals("5")||type.equals("8"))
    		  {
        	    if((index=temp.indexOf("eof"))!=-1)  //遇到eof就结束
        	    {
        		    sb.append(temp.substring(1, index));
   
        		  break;
        	    }
        	    sb.append(temp);
    		  }
    		  
    		}
         
          String str=sb.toString();   //客户端发过来的消息
          
          System.out.println(type);
          System.out.println(str);
          
          if(type.equals("1"))  //遍历
          {
            String str1=Dir.exe(str);	   
            Writer writer = new OutputStreamWriter(socket.getOutputStream(),"utf-8");  
            writer.write(str1);  
            writer.flush();  
            writer.close();   
            reader.close();   
            socket.close();
          }
          
          else if(type.equals("2"))  //下载
          {
        	  
        	 Downland.down(str,socket);    //执行下载命令
        	 socket.close();
          }
          
          else if(type.equals("3"))  //上传
		  {
        	  Update.update(str, socket);
	         //   Socket socket2=new ServerSocket(5507).accept();
	             
		  }
          else if(type.equals("4"))  //断点下载
          {
        	  File file = new File(str);
      		  Long len_str4 = file.length();
      		  System.out.println(len_str4);
              Writer writer = new OutputStreamWriter(socket.getOutputStream(),"utf-8");  
              writer.write(String.valueOf(len_str4));   //将要下载的文件大小传回去
              writer.flush();  
              writer.close();   
              reader.close();   
              socket.close();
          }
          
          else if(type.equals("5"))   //开始断点下载
          {
        	  Down_duan.down(str,socket);    //执行开始断点下载命令
          }
          //writer.write("eof\n");
       
		      
          else if(type.equals("8"))   //远程打开
          {
        	  Open.open(str);
        	  socket.close();
          }
          
          else if(type.equals("9"))   //在服务器端打开浏览器
          {
        	  Open.openBrow();
        	  socket.close();
          }
          
	}
}