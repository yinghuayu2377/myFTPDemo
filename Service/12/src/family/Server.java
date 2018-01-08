package family;

import java.io.BufferedReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import operator.Dir;
import Thread.Task;

public class Server {   
    
   public static void main(String args[]) throws IOException {   
      //为了简单起见，所有的异常信息都往外抛   
      int port = 5507;   
      //定义一个ServerSocket监听在端口5507上   
      
	ServerSocket server = new ServerSocket(port);  
      
      while(true)
      {
    	//server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的   
          Socket socket=server.accept();   
         
          //每接收到一个socket就建立一个新的线程来处理它
          new Thread(new Task(socket)).start();
      }
      
   }   
   
  
}  
