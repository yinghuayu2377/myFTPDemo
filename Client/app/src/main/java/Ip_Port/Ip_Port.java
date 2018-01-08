package Ip_Port;

/**
 * Created by lenovo on 2017/5/11.
 */
public class Ip_Port {   //用于保存IP地址以及端口号

    private static String IP="192.168.160.1";  //要连接的服务器的ip地址
    private static int Port = 5507;   //要连接的服务端对应的监听端口

    public static String getIP()
    {
        return IP;
    }

    public static void setIP(String IP)
    {
        Ip_Port.IP=IP;
    }

    public static int getPort()
    {
        return Port;
    }

    public static void setPort(int Port)
    {
        Ip_Port.Port=Port;
    }

}
